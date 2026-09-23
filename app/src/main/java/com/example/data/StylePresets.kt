package com.example.data

import com.example.model.CinematicStyle

object StylePresets {
  val allStyles: List<CinematicStyle> = listOf(
    CinematicStyle(
      id = "qismat_gold",
      title = "Qismat Golden 4K",
      genreBadge = "Pakistani Cinema",
      description = "Royal gold amber palette, emotional slow motion, dramatic Pakistani hero presence with warm cinematic lighting.",
      colorTone = "Imperial Gold & Warm Amber",
      lighting = "Sunset Golden Hour & Diffused Pakistani Sunshine",
      cameraLens = "85mm Arri Master Prime T1.3",
      gradientHexes = listOf(0xFFFFD700, 0xFFFF8C00, 0xFF1A1000),
      promptKeywords = "royal golden cinematic Pakistani film, emotional grandeur, 4k masterwork, dramatic heroic lighting, 16:9 widescreen",
      iconEmoji = "👑"
    ),
    CinematicStyle(
      id = "karachi_noir",
      title = "Karachi Midnight",
      genreBadge = "Desi Neo-Noir",
      description = "Sea View coastal mist, historic Empress Market streetlights, neon rickshaws & moody shadows.",
      colorTone = "Deep Cobalt & Amber Neon",
      lighting = "Sodium Streetlights & Ocean Reflections",
      cameraLens = "50mm Anamorphic Cine Lens",
      gradientHexes = listOf(0xFF00E5FF, 0xFFFF8C00, 0xFF050510),
      promptKeywords = "Karachi Pakistan real city street, Sea View Clifton coastline, vintage rickshaws, realistic Pakistani night life",
      iconEmoji = "🌙"
    ),
    CinematicStyle(
      id = "sufi_spiritual",
      title = "Sufi Noor (Spiritual)",
      genreBadge = "Divine & Ethereal",
      description = "Glowing emerald light beams, soft white smoke, peaceful historical shrine architecture & divine serenity.",
      colorTone = "Emerald Green & Divine White Noor",
      lighting = "Heavenly Ray Shimmer & Soft Lanterns",
      cameraLens = "35mm Ethereal Soft Focus",
      gradientHexes = listOf(0xFF00E676, 0xFFFFD700, 0xFF001A0B),
      promptKeywords = "peaceful spiritual light, divine noor, Islamic calligraphy ambiance, serene patience and faith, heavenly rays",
      iconEmoji = "🤲"
    ),
    CinematicStyle(
      id = "cyberpunk",
      title = "Cyberpunk Karachi 2099",
      genreBadge = "Sci-Fi Neon",
      description = "Futuristic flyovers, high-tech flying Qingqi rickshaws, holographic Urdu billboards & synthwave atmosphere.",
      colorTone = "Neon Cyan & Magenta",
      lighting = "Anamorphic Blue Rim Light & Neon Glow",
      cameraLens = "35mm Anamorphic T1.5 Lens",
      gradientHexes = listOf(0xFF00E5FF, 0xFFFF007F, 0xFF120024),
      promptKeywords = "futuristic Karachi cyberpunk, volumetric neon fog, reflective puddle streets, futuristic Urdu HUD",
      iconEmoji = "⚡"
    ),
    CinematicStyle(
      id = "bollywood",
      title = "Pakistani Lollywood Classic",
      genreBadge = "Grand Drama",
      description = "Opulent Lahore havelis, rich warm saffron & crimson tones, dramatic wind in hair & emotional slow-mo.",
      colorTone = "Royal Saffron, Crimson & Gold",
      lighting = "Golden Hour Sunlight & Warm Backlight",
      cameraLens = "85mm Portrait Cine Lens",
      gradientHexes = listOf(0xFFFFB300, 0xFFFF3D00, 0xFF3E0007),
      promptKeywords = "grand Pakistani cinema, royal heritage court, vibrant marigold petals, dramatic slow-motion, heroic lighting",
      iconEmoji = "🎬"
    ),
    CinematicStyle(
      id = "anime",
      title = "Anime Cinematic",
      genreBadge = "Japanese Animation",
      description = "Makoto Shinkai style lush skies, vibrant hand-drawn cel shading, atmospheric bloom & falling sakura.",
      colorTone = "Pastel Sky Blue, Violet & Emerald",
      lighting = "Ethereal God Rays & Sunset Shimmer",
      cameraLens = "Animation Wide 24mm Perspective",
      gradientHexes = listOf(0xFF80D8FF, 0xFFB388FF, 0xFF002B49),
      promptKeywords = "Makoto Shinkai anime style, hyper-detailed painterly clouds, vibrant cel shading, anime hero expression",
      iconEmoji = "✨"
    )
  )

  val defaultStyle: CinematicStyle = allStyles[0] // Qismat Golden as default
}
