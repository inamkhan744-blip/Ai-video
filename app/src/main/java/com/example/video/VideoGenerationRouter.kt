package com.example.video

import android.content.Context
import android.net.Uri
import com.example.model.VideoGenerationMode
import com.example.network.MagicHourVideoClient

/** Chooses local generation by default and cloud AI only when explicitly requested. */
class VideoGenerationRouter(context: Context) {
  private val cloud = MagicHourVideoClient(context)
  private val offline = OfflineCinematicVideoExporter(context)

  fun availableModes(): List<VideoGenerationMode> = buildList {
    add(VideoGenerationMode.OFFLINE_CINEMATIC)
    if (cloud.isConfigured()) add(VideoGenerationMode.CLOUD_AI)
  }

  fun offlineExporter(): OfflineCinematicVideoExporter = offline

  suspend fun cloudVideo(imageUri: Uri, prompt: String, title: String): Result<String> =
    runCatching { cloud.createAndWait(imageUri, prompt, title) }
}
