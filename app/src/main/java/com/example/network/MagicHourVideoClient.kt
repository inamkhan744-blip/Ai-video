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
 * Magic Hour image-to-video client.
 *
 * For production, move this call behind a server proxy so the API token is not
 * embedded in the APK. It is intentionally kept here for development/testing.
 */
class MagicHourVideoClient(
  private val context: Context,
  private val http: OkHttpClient = OkHttpClient()
) {
  companion object {
    private const val BASE_URL = "https://api.magichour.ai"
    private const val PLACEHOLDER_KEY = "YOUR_MAGIC_HOUR_API_KEY"
  }

  private val token: String
    get() = BuildConfig.MAGIC_HOUR_API_KEY.trim()

  fun isConfigured(): Boolean = token.isNotBlank() && token != PLACEHOLDER_KEY

  suspend fun createAndWait(
    imageUri: Uri,
    prompt: String,
    name: String,
    durationSeconds: Int = 5,
    resolution: String = "480p"
  ): String {
    val projectId = createVideo(imageUri, prompt, name, durationSeconds, resolution)
    return waitForVideo(projectId)
  }

  suspend fun createVideo(
    imageUri: Uri,
    prompt: String,
    name: String,
    durationSeconds: Int = 5,
    resolution: String = "480p"
  ): String = withContext(Dispatchers.IO) {
    require(isConfigured()) {
      "Magic Hour API key is missing. Add MAGIC_HOUR_API_KEY to the local .env file."
    }
    require(imageUri.scheme == "content" || imageUri.scheme == "file") {
      "Please select a photo from the phone gallery or camera. Demo web images cannot be uploaded."
    }

    val imagePath = uploadImage(imageUri)
    val body = JSONObject().apply {
      put("name", name)
      put("end_seconds", durationSeconds.coerceIn(2, 10))
      put("model", "default")
      put("resolution", resolution)
      put("audio", false)
      put("assets", JSONObject().put("image_file_path", imagePath))
      put("style", JSONObject().put("prompt", prompt.take(4000)))
    }

    request("POST", "/v1/image-to-video", body.toString()).getString("id")
  }

  suspend fun waitForVideo(projectId: String, maxPolls: Int = 60): String = withContext(Dispatchers.IO) {
    require(isConfigured()) { "Magic Hour API key is missing." }
    repeat(maxPolls) {
      val result = request("GET", "/v1/video-projects/$projectId")
      when (result.optString("status").lowercase()) {
        "complete", "completed", "succeeded" -> {
          val downloads = result.optJSONArray("downloads") ?: JSONArray()
          if (downloads.length() == 0) error("Magic Hour completed without a download URL")
          val item = downloads.getJSONObject(0)
          return@withContext item.optString("url").ifBlank { item.optString("download_url") }
        }
        "error", "failed", "canceled", "cancelled" -> {
          error(result.optJSONObject("error")?.optString("message") ?: "Magic Hour job failed")
        }
      }
      delay(3000)
    }
    error("Magic Hour video timed out while rendering")
  }

  private fun uploadImage(uri: Uri): String {
    val resolver = context.contentResolver
    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
      ?: throw IOException("Could not read selected image")
    require(bytes.isNotEmpty()) { "Selected image is empty" }

    val mime = resolver.getType(uri).orEmpty().lowercase()
    val extension = when (mime) {
      "image/png" -> "png"
      "image/webp" -> "webp"
      else -> "jpg"
    }
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
    val body = json?.toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
      .url(BASE_URL + path)
      .addHeader("Accept", "application/json")
      .addHeader("Authorization", "Bearer $token")
      .method(method, if (method == "GET") null else body)
      .build()

    http.newCall(request).execute().use { response ->
      val text = response.body?.string().orEmpty()
      if (!response.isSuccessful) throw IOException("Magic Hour HTTP ${response.code}: $text")
      return JSONObject(text)
    }
  }
}
