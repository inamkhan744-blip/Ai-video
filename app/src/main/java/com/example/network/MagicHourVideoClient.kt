package com.example.network

import android.content.Context
import android.net.Uri
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Small REST client for Magic Hour's official API.
 *
 * The mobile client should ideally call a backend proxy so the bearer token is
 * never shipped in an APK. This class is useful for development builds where
 * MAGIC_HOUR_API_KEY is supplied through the Secrets Gradle Plugin.
 */
class MagicHourVideoClient(
  private val context: Context,
  private val http: OkHttpClient = OkHttpClient()
) {
  companion object {
    private const val BASE_URL = "https://api.magichour.ai"
  }

  private val token: String
    get() = BuildConfig.MAGIC_HOUR_API_KEY.trim()

  suspend fun createVideo(
    imageUri: Uri,
    prompt: String,
    name: String,
    durationSeconds: Int = 5,
    resolution: String = "480p"
  ): String = withContext(Dispatchers.IO) {
    require(token.isNotBlank() && token != "YOUR_MAGIC_HOUR_API_KEY") {
      "Magic Hour API key is missing. Add MAGIC_HOUR_API_KEY to the local .env file."
    }

    val imagePath = uploadImage(imageUri)
    val body = JSONObject().apply {
      put("name", name)
      put("end_seconds", durationSeconds)
      put("model", "default")
      put("resolution", resolution)
      put("audio", false)
      put("assets", JSONObject().put("image_file_path", imagePath))
      put("style", JSONObject().put("prompt", prompt))
    }

    request("POST", "/v1/image-to-video", body.toString())
      .getString("id")
  }

  suspend fun waitForVideo(projectId: String, maxPolls: Int = 60): String = withContext(Dispatchers.IO) {
    repeat(maxPolls) {
      val result = request("GET", "/v1/video-projects/$projectId")
      when (result.optString("status")) {
        "complete" -> {
          val downloads = result.optJSONArray("downloads") ?: JSONArray()
          if (downloads.length() == 0) error("Magic Hour completed without a download URL")
          return@withContext downloads.getJSONObject(0).optString("url")
            .ifBlank { downloads.getJSONObject(0).optString("download_url") }
        }
        "error", "canceled" -> error(result.optJSONObject("error")?.optString("message") ?: "Magic Hour job failed")
      }
      delay(3000)
    }
    error("Magic Hour video timed out while rendering")
  }

  private fun uploadImage(uri: Uri): String {
    val resolver = context.contentResolver
    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
      ?: throw IOException("Could not read selected image")
    val extension = resolver.getType(uri)?.substringAfterLast('/', "jpg") ?: "jpg"
    val requestBody = JSONObject()
      .put("items", JSONArray().put(JSONObject().put("type", "image").put("extension", extension)))
      .toString()
    val uploadInfo = request("POST", "/v1/files/upload-urls", requestBody)
      .getJSONArray("items").getJSONObject(0)
    val uploadUrl = uploadInfo.getString("upload_url")
    val put = Request.Builder().url(uploadUrl)
      .put(bytes.toRequestBody("image/$extension".toMediaType()))
      .build()
    http.newCall(put).execute().use { response ->
      check(response.isSuccessful) { "Image upload failed: HTTP ${response.code}" }
    }
    return uploadInfo.getString("file_path")
  }

  private fun request(method: String, path: String, json: String? = null): JSONObject {
    val builder = Request.Builder().url(BASE_URL + path)
      .addHeader("Accept", "application/json")
      .addHeader("Authorization", "Bearer $token")
    if (json != null) builder.post(json.toRequestBody("application/json".toMediaType()))
    http.newCall(builder.method(method, if (method == "GET") null else json?.toRequestBody("application/json".toMediaType())).build()).execute().use { response ->
      val text = response.body?.string().orEmpty()
      if (!response.isSuccessful) throw IOException("Magic Hour HTTP ${response.code}: $text")
      return JSONObject(text)
    }
  }
}
