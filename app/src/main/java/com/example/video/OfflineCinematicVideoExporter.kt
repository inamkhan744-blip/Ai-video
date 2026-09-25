package com.example.video

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * API-free MP4 exporter. It encodes a sequence of rendered frames into a real
 * H.264 MP4 file in the user's Movies/Qismat AI folder.
 *
 * Audio is intentionally not muxed here; the existing device TTS/music preview
 * remains available and an audio track can be added later without changing the
 * generation API.
 */
class OfflineCinematicVideoExporter(private val context: Context) {
  suspend fun export(
    frames: List<Bitmap>,
    fileName: String,
    fps: Int = 24,
    durationPerFrameMs: Long = 2500L
  ): Result<android.net.Uri> = withContext(Dispatchers.IO) {
    runCatching {
      require(frames.isNotEmpty()) { "At least one frame is required" }
      val first = frames.first()
      val width = (first.width / 2) * 2
      val height = (first.height / 2) * 2
      val values = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, if (fileName.endsWith(".mp4")) fileName else "$fileName.mp4")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Qismat AI")
          put(MediaStore.Video.Media.IS_PENDING, 1)
        }
      }
      val resolver = context.contentResolver
      val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        ?: error("Could not create the output video")

      try {
        resolver.openFileDescriptor(uri, "w")?.use { descriptor ->
          val format = MediaFormat.createVideoFormat("video/avc", width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
            setInteger(MediaFormat.KEY_BIT_RATE, 4_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, fps)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
          }
          val codec = MediaCodec.createEncoderByType("video/avc")
          val muxer = MediaMuxer(descriptor.fileDescriptor, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
          var track = -1
          var muxerStarted = false
          codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
          codec.start()
          val bufferInfo = MediaCodec.BufferInfo()
          val frameCount = (durationPerFrameMs * fps / 1000L).coerceAtLeast(1L)
          var presentation = 0L

          fun drain(end: Boolean) {
            if (end) codec.signalEndOfInputStream()
            while (true) {
              val index = codec.dequeueOutputBuffer(bufferInfo, 10_000)
              when {
                index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                  if (!muxerStarted) { track = muxer.addTrack(codec.outputFormat); muxer.start(); muxerStarted = true }
                }
                index >= 0 -> {
                  val data: ByteBuffer = codec.getOutputBuffer(index) ?: continue
                  if (bufferInfo.size > 0 && muxerStarted) {
                    data.position(bufferInfo.offset); data.limit(bufferInfo.offset + bufferInfo.size)
                    muxer.writeSampleData(track, data, bufferInfo)
                  }
                  codec.releaseOutputBuffer(index, false)
                  if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) return
                }
                else -> if (!end) return
              }
            }
          }

          // Feed solid-color YUV frames. The image sequence is represented by
          // deterministic cinematic cards; UI previews still use the source photo.
          for (frame in frames) {
            val yuv = bitmapToI420(frame, width, height)
            repeat(frameCount.toInt()) {
              val input = codec.dequeueInputBuffer(10_000)
              if (input >= 0) {
                codec.getInputBuffer(input)?.apply { clear(); put(yuv) }
                codec.queueInputBuffer(input, 0, yuv.size, presentation, 0)
                presentation += 1_000_000L / fps
              }
              drain(false)
            }
          }
          drain(true)
          codec.stop(); codec.release()
          if (muxerStarted) muxer.stop()
          muxer.release()
        } ?: error("Could not open output video")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          resolver.update(uri, ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) }, null, null)
        }
        uri
      } catch (error: Throwable) {
        resolver.delete(uri, null, null)
        throw error
      }
    }
  }

  private fun bitmapToI420(bitmap: Bitmap, width: Int, height: Int): ByteArray {
    val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
    val pixels = IntArray(width * height)
    scaled.getPixels(pixels, 0, width, 0, 0, width, height)
    val out = ByteArray(width * height * 3 / 2)
    var y = 0; var u = width * height; var v = u + width * height / 4
    for (row in 0 until height) for (col in 0 until width) {
      val c = pixels[row * width + col]
      val r = (c shr 16) and 255; val g = (c shr 8) and 255; val b = c and 255
      out[y++] = ((66 * r + 129 * g + 25 * b + 128 shr 8) + 16).coerceIn(0, 255).toByte()
      if (row % 2 == 0 && col % 2 == 0) {
        out[u++] = ((-38 * r - 74 * g + 112 * b + 128 shr 8) + 128).coerceIn(0, 255).toByte()
        out[v++] = ((112 * r - 94 * g - 18 * b + 128 shr 8) + 128).coerceIn(0, 255).toByte()
      }
    }
    scaled.recycle()
    return out
  }
}
