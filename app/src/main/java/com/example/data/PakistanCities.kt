package com.example.data

data class CityLocation(
  val id: String,
  val name: String,
  val group: String,
  val famousPlaces: List<String>,
  val emoji: String = "📍",
  val isRespectfulNoMusic: Boolean = false,
  val primaryLandmark: String = famousPlaces.firstOrNull() ?: ""
) {
  val displayName: String get() = "$emoji $name - $primaryLandmark"
  val fullPlacesList: String get() = famousPlaces.joinToString(", ")

  fun generatePromptContext(): String {
    return if (isRespectfulNoMusic) {
      "Spiritual and highly respectful background of Holy $name ($primaryLandmark), divine serenity, authentic sacred Islamic architecture of $name, serene golden atmosphere, no musical instruments, respectful demeanor"
    } else {
      "Realistic background of $name, $primaryLandmark ($fullPlacesList), authentic Pakistani environment of $name, local people, accurate architecture of $name, no foreign look"
    }
  }
}

object PakistanCitiesData {
  val allCities: List<CityLocation> = listOf(
    // Sindh
    CityLocation(
      id = "karachi",
      name = "Karachi",
      group = "Sindh",
      famousPlaces = listOf("Sea View", "Saddar", "Clifton", "Mazar-e-Quaid", "Port Grand", "Burns Road"),
      emoji = "🌊"
    ),
    CityLocation(
      id = "hyderabad",
      name = "Hyderabad",
      group = "Sindh",
      famousPlaces = listOf("Paka Qila", "Latifabad", "Resham Bazaar"),
      emoji = "🏰"
    ),
    CityLocation(
      id = "sukkur",
      name = "Sukkur",
      group = "Sindh",
      famousPlaces = listOf("Lansdowne Bridge", "Sukkur Barrage", "Sadh Belo"),
      emoji = "🌉"
    ),
    CityLocation(
      id = "larkana",
      name = "Larkana",
      group = "Sindh",
      famousPlaces = listOf("Mohenjo Daro", "Garhi Khuda Bakhsh"),
      emoji = "🏛️"
    ),

    // Punjab
    CityLocation(
      id = "lahore",
      name = "Lahore",
      group = "Punjab",
      famousPlaces = listOf("Badshahi Mosque", "Minar-e-Pakistan", "Anarkali", "Shahi Qila"),
      emoji = "🕌"
    ),
    CityLocation(
      id = "islamabad_rawalpindi",
      name = "Islamabad / Rawalpindi",
      group = "Punjab",
      famousPlaces = listOf("Faisal Mosque", "Murree Road", "Daman-e-Koh", "Raja Bazaar"),
      emoji = "🌲"
    ),
    CityLocation(
      id = "faisalabad",
      name = "Faisalabad",
      group = "Punjab",
      famousPlaces = listOf("Clock Tower", "D Ground", "Ghanta Ghar"),
      emoji = "⏰"
    ),
    CityLocation(
      id = "multan",
      name = "Multan",
      group = "Punjab",
      famousPlaces = listOf("Shrines of Sufis", "Ghanta Ghar", "Haram Gate"),
      emoji = "🏺"
    ),
    CityLocation(
      id = "gujranwala",
      name = "Gujranwala",
      group = "Punjab",
      famousPlaces = listOf("Pehelwan Food Street", "GT Road", "City Center"),
      emoji = "🍲"
    ),
    CityLocation(
      id = "sialkot",
      name = "Sialkot",
      group = "Punjab",
      famousPlaces = listOf("Iqbal Manzil", "Clock Tower", "Cantt"),
      emoji = "⚽"
    ),
    CityLocation(
      id = "gujrat",
      name = "Gujrat",
      group = "Punjab",
      famousPlaces = listOf("Chenab River Banks", "Khatana Twin Towers"),
      emoji = "🏞️"
    ),
    CityLocation(
      id = "sargodha",
      name = "Sargodha",
      group = "Punjab",
      famousPlaces = listOf("Kinnow Orchards", "PAF Base Highway"),
      emoji = "🍊"
    ),
    CityLocation(
      id = "bahawalpur",
      name = "Bahawalpur",
      group = "Punjab",
      famousPlaces = listOf("Noor Mahal", "Derawar Fort Cholistan"),
      emoji = "👑"
    ),
    CityLocation(
      id = "sahiwal",
      name = "Sahiwal",
      group = "Punjab",
      famousPlaces = listOf("Harappa Ancient Ruins", "Canal Road"),
      emoji = "🌾"
    ),

    // KPK
    CityLocation(
      id = "peshawar",
      name = "Peshawar",
      group = "KPK",
      famousPlaces = listOf("Qissa Khwani Bazaar", "Bala Hisar Fort", "Chowk Yadgar"),
      emoji = "🫖"
    ),
    CityLocation(
      id = "swat",
      name = "Swat Valley",
      group = "KPK",
      famousPlaces = listOf("Snowy Mountains", "Swat River", "Malam Jabba"),
      emoji = "🏔️"
    ),
    CityLocation(
      id = "abbottabad",
      name = "Abbottabad",
      group = "KPK",
      famousPlaces = listOf("Shimla Hill", "Ilyasi Mosque Springs"),
      emoji = "🌲"
    ),
    CityLocation(
      id = "mardan",
      name = "Mardan",
      group = "KPK",
      famousPlaces = listOf("Takht-i-Bahi Buddhist Ruins", "Gujar Garhi"),
      emoji = "🏺"
    ),
    CityLocation(
      id = "swabi",
      name = "Swabi",
      group = "KPK",
      famousPlaces = listOf("Tarbela Dam Lake", "Topi Hills"),
      emoji = "🌊"
    ),

    // Balochistan
    CityLocation(
      id = "quetta",
      name = "Quetta",
      group = "Balochistan",
      famousPlaces = listOf("Hanna Lake", "Ziarat Valley", "Liaquat Bazaar"),
      emoji = "🍎"
    ),
    CityLocation(
      id = "gwadar",
      name = "Gwadar",
      group = "Balochistan",
      famousPlaces = listOf("Hammerhead Beach", "Marine Drive Sea Port"),
      emoji = "🚢"
    ),

    // International (For Dreams)
    CityLocation(
      id = "dubai",
      name = "Dubai",
      group = "International (For Dreams)",
      famousPlaces = listOf("Burj Khalifa", "Sheikh Zayed Road", "Palm Jumeirah"),
      emoji = "🏙️"
    ),
    CityLocation(
      id = "makkah_madinah",
      name = "Makkah & Madinah",
      group = "International (For Dreams)",
      famousPlaces = listOf("Khana Kaaba", "Masjid Nabawi", "Jabal al-Noor"),
      emoji = "🕋",
      isRespectfulNoMusic = true
    ),
    CityLocation(
      id = "usa_london",
      name = "USA / London",
      group = "International (For Dreams)",
      famousPlaces = listOf("Times Square New York", "Big Ben London", "Tower Bridge"),
      emoji = "✈️"
    )
  )

  val defaultCity = allCities.first() // Karachi

  val groups: List<String> = listOf(
    "All",
    "Sindh",
    "Punjab",
    "KPK",
    "Balochistan",
    "International (For Dreams)"
  )

  fun findCityByName(name: String): CityLocation {
    return allCities.firstOrNull { it.name.equals(name, ignoreCase = true) || name.contains(it.name, ignoreCase = true) }
      ?: defaultCity
  }
}
