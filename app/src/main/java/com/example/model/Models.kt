package com.example.model

enum class StudioMode(val title: String, val badge: String, val description: String) {
  TIME_MACHINE("⏳ Time Machine", "World First", "1 Photo se 5 Umrein (Bachpan se Budhapa & Legacy)"),
  DUA_SE_FILM("🤲 Dua Se Film", "Spiritual AI", "Apni Dua likho, AI banayega Kamyabi Ka Safar"),
  STORY_TO_FILM("🎬 Qismat Kahani", "Custom Film", "Apni desi kahani likhein aur trailer banayein")
}

data class CinematicStyle(
  val id: String,
  val title: String,
  val genreBadge: String,
  val description: String,
  val colorTone: String,
  val lighting: String,
  val cameraLens: String,
  val gradientHexes: List<Long>,
  val promptKeywords: String,
  val iconEmoji: String
)

data class FilmScene(
  val id: Int,
  val sceneNumber: Int,
  val timeCode: String,
  val text: String,
  val camera: String,
  val style: String,
  val lighting: String,
  val dialogue: String,
  val speaker: String = "Hero",
  val characterMood: String = "Determined",
  val visualSummary: String = "",
  val ageStage: String = "",
  val locationName: String = "",
  val isBismillah: Boolean = false,
  val imageUrl: String? = null
)

data class MovieProject(
  val id: String,
  val title: String,
  val heroName: String = "User Hero",
  val heroImageUri: String? = null,
  val isFaceLocked: Boolean = true,
  val style: CinematicStyle,
  val durationMinutes: Int,
  val language: String,
  val storyPrompt: String,
  val scenes: List<FilmScene>,
  val isRendered: Boolean = true,
  val mode: StudioMode = StudioMode.TIME_MACHINE,
  val location: String = "Karachi",
  val isKarachiMode: Boolean = true,
  val isPro: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

enum class ScreenTab {
  HOME,
  CREATE_STUDIO,
  PLAYER,
  MY_FILMS
}
