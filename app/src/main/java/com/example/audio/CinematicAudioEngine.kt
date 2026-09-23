package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlin.math.sin

class CinematicAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isTtsReady = false

  private var audioTrack: AudioTrack? = null
  private var isMusicPlaying = false
  private var musicThread: Thread? = null

  init {
    try {
      tts = TextToSpeech(context, this)
    } catch (e: Exception) {
      Log.e("CinematicAudioEngine", "TTS init error: ${e.message}")
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isTtsReady = true
      tts?.setSpeechRate(0.90f)
      tts?.setPitch(0.95f)
    }
  }

  fun speak(text: String, language: String, mood: String = "Normal") {
    if (!isTtsReady || tts == null) return

    val cleanText = text.replace(Regex("^(Hero|Saathi|Protagonist|Villain|Dua|Bachpan|Jawani|Budhapa):\\s*"), "")
      .replace("'", "")
      .replace("\"", "")

    try {
      // Dynamic Emotional Voiceover Modulation
      when (mood.lowercase()) {
        "budhapa", "sad", "emotional", "legacy" -> {
          tts?.setSpeechRate(0.80f) // Slower, nostalgic, touching
          tts?.setPitch(0.90f)
        }
        "action", "jawani", "energy" -> {
          tts?.setSpeechRate(1.15f) // Energetic youth tempo
          tts?.setPitch(1.02f)
        }
        "dua", "spiritual", "peaceful" -> {
          tts?.setSpeechRate(0.84f) // Reverent, serene recital
          tts?.setPitch(0.96f)
        }
        else -> {
          tts?.setSpeechRate(0.92f)
          tts?.setPitch(0.95f)
        }
      }

      val locale = when (language.lowercase()) {
        "urdu" -> Locale("ur", "PK")
        "hindi" -> Locale("hi", "IN")
        else -> Locale.US
      }
      val result = tts?.setLanguage(locale)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        tts?.setLanguage(Locale.US)
      }
      tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "QISMAT_CINEMATIC_UTTERANCE")
    } catch (e: Exception) {
      Log.e("CinematicAudioEngine", "TTS speak error: ${e.message}")
    }
  }

  fun stopSpeech() {
    try {
      tts?.stop()
    } catch (_: Exception) {}
  }

  /**
   * Generates a rich, ambient cinematic film score or Islamic vocal drone synthesizer loop.
   * Completely offline, zero network quota, authentic Hans Zimmer & spiritual ambiance.
   */
  fun startMusic(ambianceName: String) {
    stopMusic()
    isMusicPlaying = true

    val isNasheed = ambianceName.contains("Nasheed", ignoreCase = true) || ambianceName.contains("Dua", ignoreCase = true)

    val baseFreq = when {
      isNasheed -> 82.41                                              // E2 serene vocal meditation drone
      ambianceName.contains("Cyberpunk", ignoreCase = true) -> 65.41 // C2 dark saw pad
      ambianceName.contains("Bollywood", ignoreCase = true) -> 73.42 // D2 warm acoustic drone
      ambianceName.contains("Karachi", ignoreCase = true) -> 61.74   // B1 coastal midnight pulse
      ambianceName.contains("Time", ignoreCase = true) -> 55.00      // A1 deep emotional passage of time
      else -> 65.41                                                  // C2 golden orchestral
    }

    musicThread = Thread {
      val sampleRate = 22050
      val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      ).coerceAtLeast(sampleRate)

      try {
        audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(bufferSize)
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()

        audioTrack?.play()

        val buffer = ShortArray(bufferSize / 2)
        var phase1 = 0.0
        var phase2 = 0.0
        var phase3 = 0.0
        var lfoPhase = 0.0

        val twoPi = 2.0 * Math.PI

        while (isMusicPlaying) {
          for (i in buffer.indices) {
            val lfo = if (isNasheed) {
              0.6 + 0.4 * sin(lfoPhase) // Gentle organic breathing hum
            } else {
              0.7 + 0.3 * sin(lfoPhase)
            }

            val s1 = sin(phase1) * if (isNasheed) 0.55 else 0.45
            val s2 = sin(phase2) * if (isNasheed) 0.20 else 0.25
            val s3 = sin(phase3) * if (isNasheed) 0.10 else 0.15

            val sample = ((s1 + s2 + s3) * lfo * 0.42 * Short.MAX_VALUE).toInt()
              .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            buffer[i] = sample

            phase1 = (phase1 + twoPi * baseFreq / sampleRate) % twoPi
            phase2 = (phase2 + twoPi * (baseFreq * 1.5) / sampleRate) % twoPi
            phase3 = (phase3 + twoPi * (baseFreq * 2.0) / sampleRate) % twoPi
            lfoPhase = (lfoPhase + twoPi * (if (isNasheed) 0.08 else 0.14) / sampleRate) % twoPi
          }

          audioTrack?.write(buffer, 0, buffer.size)
        }
      } catch (e: Exception) {
        Log.e("CinematicAudioEngine", "AudioTrack stream error: ${e.message}")
      } finally {
        try {
          audioTrack?.stop()
          audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
      }
    }.apply {
      isDaemon = true
      start()
    }
  }

  fun stopMusic() {
    isMusicPlaying = false
    try {
      musicThread?.interrupt()
      musicThread = null
    } catch (_: Exception) {}
  }

  fun release() {
    stopMusic()
    stopSpeech()
    try {
      tts?.shutdown()
      tts = null
    } catch (_: Exception) {}
  }
}
