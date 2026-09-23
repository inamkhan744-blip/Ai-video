package com.example.data

import com.example.model.FilmScene
import com.example.model.MovieProject
import com.example.model.StudioMode

object DemoFilms {
  val sampleProjects: List<MovieProject> = listOf(
    // 1. Time Machine (5 Stages of Life)
    MovieProject(
      id = "time_machine_hamza",
      title = "Hamza: Bachpan Se Budhapa (Time Machine)",
      heroName = "Hamza",
      mode = StudioMode.TIME_MACHINE,
      location = "Karachi",
      isKarachiMode = true,
      style = StylePresets.allStyles[0], // Qismat Golden
      durationMinutes = 3,
      language = "Urdu",
      storyPrompt = "Hamza ki zindagi ka 70 saala safar: Karachi ke school se shuru ho kar Dubai mein kamyabi aur aakhir mein potay potiyon ke sath pur-sukoon zindagi.",
      scenes = listOf(
        FilmScene(
          id = 1,
          sceneNumber = 1,
          timeCode = "0:00 - 0:35",
          ageStage = "Bachpan (5 Years)",
          text = "Scene 1: Bachpan (5 Years). Primary school uniform pehne, baraf gola khate hue Karachi Saddar ki galiyon mein masoom muskurahat.",
          camera = "Low Angle Ground Level Child View",
          style = "Qismat Golden 4K",
          lighting = "Warm Nostalgic Afternoon Sunlight",
          dialogue = "Bachpan: 'Ammi kehti hain main bara ho kar aik din dunya par chha jaunga!'",
          speaker = "Chota Hamza",
          characterMood = "Emotional",
          locationName = "Saddar, Karachi",
          visualSummary = "Age progression: 5 year old Pakistani boy, innocent sparkling eyes, school bag, vintage Karachi street."
        ),
        FilmScene(
          id = 2,
          sceneNumber = 2,
          timeCode = "0:35 - 1:10",
          ageStage = "Jawani (25 Years)",
          text = "Scene 2: Jawani (25 Years). Karachi University ki library aur Sea View par doston ke sath mustaqbil ke khwab dekhte hue.",
          camera = "85mm Medium Cinematic Portrait",
          style = "Qismat Golden 4K",
          lighting = "Golden Hour Coastal Glow & Sea Breeze",
          dialogue = "Jawani: 'Mehnat itni khamoshi se karo ke tumhari kamyabi shor macha de.'",
          speaker = "Jawan Hamza",
          characterMood = "Determined",
          locationName = "Sea View Clifton",
          visualSummary = "Age progression: 25 year old handsome Pakistani young man, sharp jawline, passionate eyes, Karachi breeze."
        ),
        FilmScene(
          id = 3,
          sceneNumber = 3,
          timeCode = "1:10 - 1:45",
          ageStage = "Shaadi & Kamyabi (35 Years)",
          text = "Scene 3: Shaadi & Kamyabi (35 Years). Tech CEO ban kar Dubai aur Karachi dono jagah empire banaya, shandar sherwani mein doston ka jashn.",
          camera = "Wide Cinematic Crane Pullback",
          style = "Qismat Golden 4K",
          lighting = "Rich Ambient Chandelier & City Skyline",
          dialogue = "Kamyabi: 'Khuda ne meri har koshish ko azeem kamyabi mein badal dia.'",
          speaker = "Hamza CEO",
          characterMood = "Triumphant",
          locationName = "Port Grand & Dubai",
          visualSummary = "Age progression: 35 year old successful executive, confident smile, tailored royal suit, high-rise office."
        ),
        FilmScene(
          id = 4,
          sceneNumber = 4,
          timeCode = "1:45 - 2:20",
          ageStage = "Budhapa (65 Years)",
          text = "Scene 4: Budhapa (65 Years). Sufaid baal, chashma lagaye, apne potay potiyon ko kahani sunate hue muskurahat.",
          camera = "Soft Warm Macro Portrait",
          style = "Qismat Golden 4K",
          lighting = "Soft Fireplace & Golden Window Rays",
          dialogue = "Budhapa: 'Zindagi mein paisa nahi, apnon ki duaayein sab se bara sarmaya hoti hain.'",
          speaker = "Dada Hamza",
          characterMood = "Budhapa",
          locationName = "Home Courtyard",
          visualSummary = "Age progression: 65 year old elder, elegant silver beard, kind wrinkles, glowing with contentment."
        ),
        FilmScene(
          id = 5,
          sceneNumber = 5,
          timeCode = "2:20 - 3:00",
          ageStage = "Legacy (Tatheer & Shaan)",
          text = "Scene 5: Legacy. Deewar par sonay ke frame mein tasveer, naye bachay unki misaal dete hue roshan mustaqbil ki taraf barhte hain.",
          camera = "Slow Drift Push-in on Golden Frame",
          style = "Qismat Golden 4K",
          lighting = "Subtle Noor Spotlight & Floating Dust Particles",
          dialogue = "Legacy: 'Insaan chala jata hai magar uski nek qismat sadiyon tak zinda rehti hai.'",
          speaker = "Narration",
          characterMood = "Legacy",
          locationName = "Heritage Hall of Fame",
          visualSummary = "Golden framed portrait on museum wall, flowers, eternal legacy of Pakistani hero."
        )
      )
    ),

    // 2. Dua Se Film (Spiritual Journey)
    MovieProject(
      id = "dua_bilal_tech",
      title = "Dua Se Film: 'Ya Allah Mujhe Kamyab Kar'",
      heroName = "Bilal",
      mode = StudioMode.DUA_SE_FILM,
      location = "Karachi",
      isKarachiMode = true,
      style = StylePresets.allStyles[2], // Sufi Spiritual
      durationMinutes = 2,
      language = "Urdu",
      storyPrompt = "Ya Allah mujhe ek bara software house banane ki himmat de taake main hazaron Pakistani naujawanon ko rozgar de sakun.",
      scenes = listOf(
        FilmScene(
          id = 1,
          sceneNumber = 1,
          timeCode = "0:00 - 0:30",
          isBismillah = true,
          ageStage = "Niyyah (Tahajjud)",
          text = "Bismillahir Rahmanir Raheem. Bilal raat ke aakhri pehar musallay par aansuon ke sath dua mang raha hai.",
          camera = "Low Angle Prayer Shot",
          style = "Sufi Noor (Spiritual)",
          lighting = "Emerald Moonlight Ray Streaming through Window",
          dialogue = "Dua: 'بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم - Ya Allah, meri niyat saaf hai, tu mere raste asaan farma.'",
          speaker = "Bilal",
          characterMood = "Dua",
          locationName = "Mazar-e-Quaid Mosque",
          visualSummary = "Bismillah header, young Pakistani man praying tahajjud, tears of sincerity, green aura."
        ),
        FilmScene(
          id = 2,
          sceneNumber = 2,
          timeCode = "0:30 - 1:00",
          ageStage = "Imtihan (Mehnat)",
          text = "Purane laptop par raat bhar coding karte hue, bijli chali jati hai magar candles mein kaam jari rehta hai.",
          camera = "Extreme Close-up on Determined Eyes",
          style = "Sufi Noor (Spiritual)",
          lighting = "Warm Flickering Candle Flame in Karachi Darkness",
          dialogue = "Bilal: 'Mera Rab mujhe kabhi mayoos nahi karega, koshish mera farz hai.'",
          speaker = "Bilal",
          characterMood = "Determined",
          locationName = "Lyari Workspace",
          visualSummary = "Candle lit Pakistani youth coding, sweat on brow, unfaltering faith, dark room."
        ),
        FilmScene(
          id = 3,
          sceneNumber = 3,
          timeCode = "1:00 - 1:30",
          ageStage = "Mu'jiza (First Milestone)",
          text = "International client ka pehla bada contract sign hota hai, Bilal pehla sajda shukar karta hai.",
          camera = "Slow High Angle Prostration",
          style = "Sufi Noor (Spiritual)",
          lighting = "Bright Divine Morning Sunlight",
          dialogue = "Bilal: 'Alhamdulillah! Khuda ne meri dua sun li... ab safar aage barhega.'",
          speaker = "Bilal",
          characterMood = "Dua",
          locationName = "Clifton Office",
          visualSummary = "Young Pakistani professional bowing in gratitude, modern glass office, morning rays."
        ),
        FilmScene(
          id = 4,
          sceneNumber = 4,
          timeCode = "1:30 - 2:00",
          ageStage = "Kamyabi (Rozgar ki Fauj)",
          text = "500 Pakistani ladke aur ladkiyan AI aur coding kar rahe hain, Bilal sab ko guide karte hue muskurata hai.",
          camera = "Steadicam Tracking Walkthrough",
          style = "Sufi Noor (Spiritual)",
          lighting = "Inspiring Clean Modern Lighting & Green Accents",
          dialogue = "Bilal: 'Jab niyat sab ka bhala karne ki ho, to Qismat khud rasta bana deti hai.'",
          speaker = "Bilal",
          characterMood = "Triumphant",
          locationName = "National Tech Park Karachi",
          visualSummary = "Thriving Pakistani software campus, hundreds of empowered youth, CEO leading with humility."
        )
      )
    )
  )
}
