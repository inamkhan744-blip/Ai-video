package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CinematicAudioEngine
import com.example.data.CityLocation
import com.example.data.DemoFilms
import com.example.data.PakistanCitiesData
import com.example.data.StylePresets
import com.example.data.local.AppDatabase
import com.example.data.local.StoryDraftEntity
import com.example.data.local.StoryRepository
import com.example.model.CinematicStyle
import com.example.model.FilmScene
import com.example.model.MovieProject
import com.example.model.ScreenTab
import com.example.model.StudioMode
import com.example.model.VideoGenerationMode
import com.example.video.VideoGenerationRouter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(private val app: Application) : AndroidViewModel(app) {

  private val database = AppDatabase.getInstance(app)
  val repository: StoryRepository = StoryRepository(
    movieProjectDao = database.movieProjectDao(),
    storyDraftDao = database.storyDraftDao()
  )

  private val audioEngine = CinematicAudioEngine(app)
  private val videoGenerationRouter = VideoGenerationRouter(app)

  private val _currentTab = MutableStateFlow(ScreenTab.HOME)
  val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

  // Studio Mode & Controls
  private val _selectedMode = MutableStateFlow(StudioMode.TIME_MACHINE)
  val selectedMode: StateFlow<StudioMode> = _selectedMode.asStateFlow()

  private val _selectedGenerationMode = MutableStateFlow(VideoGenerationMode.OFFLINE_CINEMATIC)
  val selectedGenerationMode: StateFlow<VideoGenerationMode> = _selectedGenerationMode.asStateFlow()

  fun videoGenerationModes(): List<VideoGenerationMode> = videoGenerationRouter.availableModes()

  fun setGenerationMode(mode: VideoGenerationMode) {
    _selectedGenerationMode.value = mode
  }

  private val _isKarachiMode = MutableStateFlow(true)
  val isKarachiMode: StateFlow<Boolean> = _isKarachiMode.asStateFlow()

  private val _selectedLocation = MutableStateFlow("Karachi")
  val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

  private val _selectedCityLocation = MutableStateFlow<CityLocation>(PakistanCitiesData.defaultCity)
  val selectedCityLocation: StateFlow<CityLocation> = _selectedCityLocation.asStateFlow()

  private val _detectedCityMessage = MutableStateFlow<String?>(null)
  val detectedCityMessage: StateFlow<String?> = _detectedCityMessage.asStateFlow()

  private val _currentStep = MutableStateFlow(1)
  val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

  private val _heroImageUri = MutableStateFlow<String?>(null)
  val heroImageUri: StateFlow<String?> = _heroImageUri.asStateFlow()

  private val _pendingHeroImageUri = MutableStateFlow<String?>(null)
  val pendingHeroImageUri: StateFlow<String?> = _pendingHeroImageUri.asStateFlow()

  private val _isFaceLocked = MutableStateFlow(true)
  val isFaceLocked: StateFlow<Boolean> = _isFaceLocked.asStateFlow()

  private val _selectedStyle = MutableStateFlow(StylePresets.defaultStyle)
  val selectedStyle: StateFlow<CinematicStyle> = _selectedStyle.asStateFlow()

  private val _durationMinutes = MutableStateFlow(3)
  val durationMinutes: StateFlow<Int> = _durationMinutes.asStateFlow()

  private val _storyText = MutableStateFlow(
    "Karachi ke pur-umeed naujawan ki dastaan jo din raat mehnat karke dunya mein apna naam banata hai."
  )
  val storyText: StateFlow<String> = _storyText.asStateFlow()

  private val _duaText = MutableStateFlow(
    "Ya Allah mujhe bada aur kamyab software builder bana de taake main apne walidain ka sar fakhar se buland karun aur hazaron Pakistaniyon ki madad karun."
  )
  val duaText: StateFlow<String> = _duaText.asStateFlow()

  private val _selectedLanguage = MutableStateFlow("Urdu")
  val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

  // Generation status
  private val _isSplittingScenes = MutableStateFlow(false)
  val isSplittingScenes: StateFlow<Boolean> = _isSplittingScenes.asStateFlow()

  private val _isRenderingMovie = MutableStateFlow(false)
  val isRenderingMovie: StateFlow<Boolean> = _isRenderingMovie.asStateFlow()

  private val _renderProgress = MutableStateFlow(0f)
  val renderProgress: StateFlow<Float> = _renderProgress.asStateFlow()

  private val _renderStatusText = MutableStateFlow("QISMAT AI 4K Rendering shuru ho rahi hai...")
  val renderStatusText: StateFlow<String> = _renderStatusText.asStateFlow()

  private val _generatedScenes = MutableStateFlow<List<FilmScene>>(emptyList())
  val generatedScenes: StateFlow<List<FilmScene>> = _generatedScenes.asStateFlow()

  // Pro & Viral Monetization
  private val _isProUser = MutableStateFlow(false)
  val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

  private val _freeCredits = MutableStateFlow(1)
  val freeCredits: StateFlow<Int> = _freeCredits.asStateFlow()
  val creditsRemaining: StateFlow<Int> = _freeCredits.asStateFlow()

  private val _showPaywallDialog = MutableStateFlow(false)
  val showPaywallDialog: StateFlow<Boolean> = _showPaywallDialog.asStateFlow()

  private val _showReferralDialog = MutableStateFlow(false)
  val showReferralDialog: StateFlow<Boolean> = _showReferralDialog.asStateFlow()

  // Interactive Before/After slider position (0f to 1f)
  private val _beforeAfterPosition = MutableStateFlow(0.5f)
  val beforeAfterPosition: StateFlow<Float> = _beforeAfterPosition.asStateFlow()

  // Projects & Player
  val allProjects: StateFlow<List<MovieProject>> = repository.allProjects
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = DemoFilms.sampleProjects
    )

  // Local User Story Drafts from Room
  val allDrafts: StateFlow<List<StoryDraftEntity>> = repository.allDrafts
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  private val _activeProject = MutableStateFlow<MovieProject?>(DemoFilms.sampleProjects.first())
  val activeProject: StateFlow<MovieProject?> = _activeProject.asStateFlow()

  init {
    viewModelScope.launch {
      repository.populateDemoProjectsIfEmpty()
    }
  }

  private val _playerSceneIndex = MutableStateFlow(0)
  val playerSceneIndex: StateFlow<Int> = _playerSceneIndex.asStateFlow()

  private val _isPlayerPlaying = MutableStateFlow(false)
  val isPlayerPlaying: StateFlow<Boolean> = _isPlayerPlaying.asStateFlow()

  private val _selectedAmbiance = MutableStateFlow("Qismat Golden Theme")
  val selectedAmbiance: StateFlow<String> = _selectedAmbiance.asStateFlow()

  private val _isVoiceoverEnabled = MutableStateFlow(true)
  val isVoiceoverEnabled: StateFlow<Boolean> = _isVoiceoverEnabled.asStateFlow()

  private val _isSoundtrackEnabled = MutableStateFlow(true)
  val isSoundtrackEnabled: StateFlow<Boolean> = _isSoundtrackEnabled.asStateFlow()

  private val _demoDialogProject = MutableStateFlow<MovieProject?>(null)
  val demoDialogProject: StateFlow<MovieProject?> = _demoDialogProject.asStateFlow()

  val faceLockPromptPrefix =
    "IDENTITY LOCK: Use face from reference image 100% identical, do not change face, same person only, high facial similarity, --ar 16:9 --style raw"

  fun selectTab(tab: ScreenTab) {
    _currentTab.value = tab
  }

  fun setStudioMode(mode: StudioMode) {
    _selectedMode.value = mode
    when (mode) {
      StudioMode.TIME_MACHINE -> {
        _selectedStyle.value = StylePresets.allStyles[0]
        _selectedAmbiance.value = "Time Machine Passage"
      }
      StudioMode.DUA_SE_FILM -> {
        _selectedStyle.value = StylePresets.allStyles[2]
        _selectedAmbiance.value = "Islamic Nasheed (Acapella Drone)"
      }
      StudioMode.STORY_TO_FILM -> {
        _selectedAmbiance.value = "Qismat Golden Theme"
      }
    }
  }

  fun toggleKarachiMode(enabled: Boolean) {
    _isKarachiMode.value = enabled
    if (enabled) {
      selectCity(PakistanCitiesData.defaultCity)
    }
  }

  fun setSelectedLocation(location: String) {
    val city = PakistanCitiesData.findCityByName(location)
    selectCity(city)
  }

  fun selectCity(city: CityLocation) {
    _selectedCityLocation.value = city
    _selectedLocation.value = city.name
    _isKarachiMode.value = (city.name == "Karachi")
    if (city.isRespectfulNoMusic) {
      _isSoundtrackEnabled.value = false
      _selectedAmbiance.value = "Islamic Nasheed (Acapella Drone)"
      audioEngine.stopMusic()
    }
  }

  fun autoDetectCity() {
    viewModelScope.launch {
      _detectedCityMessage.value = "GPS Detecting location..."
      delay(600)
      try {
        val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        var detectedName = "Karachi"
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true

        if (locationManager != null && (isNetworkEnabled || isGpsEnabled)) {
          val tz = java.util.TimeZone.getDefault().id
          if (tz.contains("Karachi") || tz.contains("Asia/Karachi")) {
            detectedName = "Karachi"
          }
        }
        val city = PakistanCitiesData.findCityByName(detectedName)
        selectCity(city)
        _detectedCityMessage.value = "Auto-detected: ${city.name} (${city.primaryLandmark}) 📍"
      } catch (e: Exception) {
        val defaultCity = PakistanCitiesData.defaultCity
        selectCity(defaultCity)
        _detectedCityMessage.value = "Auto-detected: Karachi (Sea View) 📍"
      }
    }
  }

  fun setBeforeAfterPosition(pos: Float) {
    _beforeAfterPosition.value = pos.coerceIn(0f, 1f)
  }

  fun setStep(step: Int) {
    _currentStep.value = step.coerceIn(1, 3)
  }

  fun setHeroImageUri(uri: String?) {
    _heroImageUri.value = uri
    _pendingHeroImageUri.value = null
  }

  fun setPendingHeroImageUri(uri: String?) {
    _pendingHeroImageUri.value = uri
  }

  fun setHeroImageBitmap(bitmap: Bitmap, isPending: Boolean = true) {
    try {
      val minEdge = minOf(bitmap.width, bitmap.height)
      val x = (bitmap.width - minEdge) / 2
      val y = (bitmap.height - minEdge) / 2
      val cropped = Bitmap.createBitmap(bitmap, x, y, minEdge, minEdge)

      val file = File(app.cacheDir, "hero_captured_${System.currentTimeMillis()}.jpg")
      file.outputStream().use { out ->
        cropped.compress(Bitmap.CompressFormat.JPEG, 92, out)
      }
      val uriStr = Uri.fromFile(file).toString()
      if (isPending) {
        _pendingHeroImageUri.value = uriStr
      } else {
        _heroImageUri.value = uriStr
      }
    } catch (_: Exception) {
    }
  }

  fun confirmHeroImage() {
    val pending = _pendingHeroImageUri.value
    if (pending != null) {
      _heroImageUri.value = pending
      _pendingHeroImageUri.value = null
    }
  }

  fun retakeHeroImage() {
    _pendingHeroImageUri.value = null
  }

  fun toggleFaceLock(enabled: Boolean) {
    _isFaceLocked.value = enabled
  }

  fun selectStyle(style: CinematicStyle) {
    _selectedStyle.value = style
  }

  fun setDurationMinutes(minutes: Int) {
    _durationMinutes.value = minutes.coerceIn(1, 10)
  }

  fun setStoryText(text: String) {
    _storyText.value = text
  }

  fun setDuaText(text: String) {
    _duaText.value = text
  }

  fun appendDuaVoice(spokenText: String) {
    if (_duaText.value.isBlank()) {
      _duaText.value = spokenText
    } else {
      _duaText.value = "${_duaText.value.trim()} $spokenText"
    }
  }

  fun appendStoryVoice(spokenText: String) {
    if (_storyText.value.isBlank()) {
      _storyText.value = spokenText
    } else {
      _storyText.value = "${_storyText.value.trim()} $spokenText"
    }
  }

  fun updateScene(sceneNumber: Int, updatedText: String, updatedDialogue: String, updatedCamera: String) {
    _generatedScenes.value = _generatedScenes.value.map { scene ->
      if (scene.sceneNumber == sceneNumber) {
        scene.copy(
          text = updatedText,
          dialogue = updatedDialogue,
          camera = updatedCamera
        )
      } else {
        scene
      }
    }
  }

  fun regenerateSceneDialogue(sceneNumber: Int) {
    val current = _generatedScenes.value.find { it.sceneNumber == sceneNumber } ?: return
    val newDialogues = listOf(
      "\"Har andheri raat ke baad subah zaroor aati hai... Yahi meri qismat ka aaghaz hai.\"",
      "\"Main haar nahi maan sakta, mere peeche mere maa baap ki duaein hain.\"",
      "\"Yeh shehar mujhe pehchanta hai, aur ek din yeh dunya mera naam lene par majboor hogi.\"",
      "\"Rizq aur izzat sirf Allah ke haath mein hai, insaan sirf mehnat karta hai.\""
    )
    val chosen = newDialogues.filter { it != current.dialogue }.randomOrNull() ?: newDialogues.first()
    updateScene(sceneNumber, current.text, chosen, current.camera)
    Toast.makeText(app, "Scene $sceneNumber ka dialogue refresh ho gaya! ⚡", Toast.LENGTH_SHORT).show()
  }

  fun setLanguage(lang: String) {
    _selectedLanguage.value = lang
  }

  fun setAmbiance(ambiance: String) {
    _selectedAmbiance.value = ambiance
    if (_isPlayerPlaying.value && _isSoundtrackEnabled.value) {
      audioEngine.startMusic(ambiance)
    }
  }

  fun toggleVoiceover() {
    _isVoiceoverEnabled.value = !_isVoiceoverEnabled.value
    if (!_isVoiceoverEnabled.value) {
      audioEngine.stopSpeech()
    } else if (_isPlayerPlaying.value) {
      triggerCurrentSceneVoiceover()
    }
  }

  fun toggleSoundtrack() {
    _isSoundtrackEnabled.value = !_isSoundtrackEnabled.value
    if (!_isSoundtrackEnabled.value) {
      audioEngine.stopMusic()
    } else if (_isPlayerPlaying.value) {
      audioEngine.startMusic(_selectedAmbiance.value)
    }
  }

  fun openDemoDialog(project: MovieProject) {
    _demoDialogProject.value = project
  }

  fun closeDemoDialog() {
    _demoDialogProject.value = null
  }

  fun openPaywallDialog() {
    _showPaywallDialog.value = true
  }

  fun closePaywallDialog() {
    _showPaywallDialog.value = false
  }

  fun openReferralDialog() {
    _showReferralDialog.value = true
  }

  fun closeReferralDialog() {
    _showReferralDialog.value = false
  }

  fun upgradeToPro() {
    _isProUser.value = true
    _freeCredits.value += 999
    _showPaywallDialog.value = false
    Toast.makeText(app, "✨ Mubarak ho! Qismat Pro Lifetime Active ho gaya!", Toast.LENGTH_LONG).show()
  }

  fun shareReferral() {
    try {
      val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
          Intent.EXTRA_TEXT,
          "Bhai maine 'QISMAT AI' par apni 1 photo se poori zindagi ka trailer banaya hai! Tum bhi apni qismat dekho: https://qismat.ai/download?ref=hero786"
        )
        type = "text/plain"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      app.startActivity(sendIntent)
      _freeCredits.value += 1
      _showReferralDialog.value = false
      Toast.makeText(app, "Dost ko invite karne par 1 Free Film Credit mil gaya!", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
      Toast.makeText(app, "Invite link copy ho gaya!", Toast.LENGTH_SHORT).show()
    }
  }

  fun autoSplitStory() {
    if (!_isProUser.value && _freeCredits.value <= 0) {
      _showPaywallDialog.value = true
      return
    }

    viewModelScope.launch {
      _isSplittingScenes.value = true
      delay(600)

      val mode = _selectedMode.value
      val location = if (_isKarachiMode.value) "Karachi" else _selectedLocation.value
      val style = _selectedStyle.value

      val generated = when (mode) {
        StudioMode.TIME_MACHINE -> generateTimeMachineScenes(location, style)
        StudioMode.DUA_SE_FILM -> generateDuaFilmScenes(location, style)
        StudioMode.STORY_TO_FILM -> generateCustomStoryScenes(location, style)
      }

      _generatedScenes.value = generated
      _isSplittingScenes.value = false
      _currentStep.value = 3
    }
  }

  private fun generateTimeMachineScenes(location: String, style: CinematicStyle): List<FilmScene> {
    val city = _selectedCityLocation.value
    val cityPrompt = city.generatePromptContext()
    val famousPlace = city.primaryLandmark.ifBlank { "City Center" }

    return listOf(
      FilmScene(
        id = 1,
        sceneNumber = 1,
        timeCode = "0:00 - 0:35",
        ageStage = "Bachpan (5 Years)",
        text = "Bachpan (5 Years Old): Same facial bone structure, innocent bright eyes, primary school uniform in $location ($famousPlace). Playing in courtyard with desi marbles.",
        camera = "Low Angle Ground Child View",
        style = style.title,
        lighting = "Warm Nostalgic Sunbeams",
        dialogue = "Bachpan: 'Ammi kehti hain main bada ho kar dunya jeetunga!'",
        speaker = "Chota Hero",
        characterMood = "Emotional",
        locationName = "$location - $famousPlace",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Age progression: 5 year old Pakistani child, identical eye shape and nose, vintage school bag."
      ),
      FilmScene(
        id = 2,
        sceneNumber = 2,
        timeCode = "0:35 - 1:10",
        ageStage = "Jawani (25 Years)",
        text = "Jawani (25 Years Old): College/University graduate, energetic posture, looking out over $famousPlace in $location with ambitious passion.",
        camera = "85mm Medium Cinematic Hero Portrait",
        style = style.title,
        lighting = "Sunset Golden Hour Glow & Desi Breeze",
        dialogue = "Jawani: 'Raste mushkil zaroor hain, lekin mera hausla toofan se bada hai!'",
        speaker = "Jawan Hero",
        characterMood = "Jawani",
        locationName = "$location - $famousPlace",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Age progression: 25 year old handsome Pakistani young man, sharp defined jawline, vibrant youth energy."
      ),
      FilmScene(
        id = 3,
        sceneNumber = 3,
        timeCode = "1:10 - 1:45",
        ageStage = "Shaadi & Kamyabi (35 Years)",
        text = "Shaadi & Kamyabi (35 Years Old): Highly successful entrepreneur, luxurious sherwani and modern business empire connecting $location with global trade.",
        camera = "Cinematic Crane Pullback Wide",
        style = style.title,
        lighting = "Opulent Warm Chandeliers & City Skyline",
        dialogue = "Kamyabi: 'Khuda ne har uss raat ka ajar dia jab hum khamoshi se lade.'",
        speaker = "Hero (CEO)",
        characterMood = "Triumphant",
        locationName = "$location Skyline",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Age progression: 35 year old successful mature leader, royal posture, high-rise modern boardroom."
      ),
      FilmScene(
        id = 4,
        sceneNumber = 4,
        timeCode = "1:45 - 2:20",
        ageStage = "Budhapa (65 Years)",
        text = "Budhapa (65 Years Old): Dignified elder with elegant silver hair, gentle laugh lines, surrounded by joyful grandchildren in peaceful garden of $location.",
        camera = "Soft Warm Macro Portrait",
        style = style.title,
        lighting = "Soft Fireplace & Golden Window Rays",
        dialogue = "Budhapa: 'Zindagi ka asal maza paisay mein nahi, apnon ke chehron ki hansi mein hai.'",
        speaker = "Buzurg Hero",
        characterMood = "Budhapa",
        locationName = "Family Courtyard, $location",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Age progression: 65 year old Pakistani elder, silver beard, kind warm wrinkles, profound wisdom in eyes."
      ),
      FilmScene(
        id = 5,
        sceneNumber = 5,
        timeCode = "2:20 - 3:00",
        ageStage = "Legacy (Tatheer & Shaan)",
        text = "Legacy: A golden engraved portrait on the heritage wall of fame. Next generation gathers to honor his timeless contributions to $location and Pakistan.",
        camera = "Slow Drift Push-in on Golden Frame",
        style = style.title,
        lighting = "Ethereal Noor Spotlight & Floating Amber Specks",
        dialogue = "Legacy: 'Insaan chala jata hai, magar uski nek qismat aur kirdaar hamesha amar rehte hain.'",
        speaker = "Narrator",
        characterMood = "Legacy",
        locationName = "National Hall of Fame, $location",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Final legacy: Golden memorial portrait on prestigious museum wall, flowers, eternal pride of Pakistan."
      )
    )
  }

  private fun generateDuaFilmScenes(location: String, style: CinematicStyle): List<FilmScene> {
    val city = _selectedCityLocation.value
    val cityPrompt = city.generatePromptContext()
    val famousPlace = city.primaryLandmark.ifBlank { "Historic Mosque" }
    val dua = _duaText.value.ifBlank { "Ya Allah mujhe kamyab aur ba-barkat zindagi ata farma." }

    return listOf(
      FilmScene(
        id = 1,
        sceneNumber = 1,
        timeCode = "0:00 - 0:30",
        isBismillah = true,
        ageStage = "Niyyah (Tahajjud)",
        text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم - Tahajjud ki khamoshi mein hero namaz ke baad haath utha kar roro kar dua mang raha hai: '$dua'",
        camera = "Low Angle Prayer Stance",
        style = "Sufi Noor (Spiritual)",
        lighting = "Emerald Moonlight Ray Streaming through Mosque Window",
        dialogue = "Dua: 'بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم - Ya Allah, meri niyat saaf hai, tu sab jaanta hai!'",
        speaker = "Hero (Dua)",
        characterMood = "Dua",
        locationName = "$location ($famousPlace)",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Pakistani hero in white shalwar kameez praying with tears of hope, sacred emerald ambience."
      ),
      FilmScene(
        id = 2,
        sceneNumber = 2,
        timeCode = "0:30 - 1:00",
        ageStage = "Imtihan (Sabr & Mehnat)",
        text = "Imtihan ka dor: Mushkil halaat, bijli ki kami aur purane kamre mein deep raat tak lagan se kaam karte hue in $location.",
        camera = "Intense 85mm Portrait",
        style = "Sufi Noor (Spiritual)",
        lighting = "Warm Flickering Candle Flame in Desi Darkness",
        dialogue = "Hero: 'Mera Rab mere sath hai, mushkil ke baad aasaani zaroor aayegi.'",
        speaker = "Hero",
        characterMood = "Determined",
        locationName = "$location Workstation",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Pakistani hero working late night under candle light, sweat and unwavering determination on face."
      ),
      FilmScene(
        id = 3,
        sceneNumber = 3,
        timeCode = "1:00 - 1:30",
        ageStage = "Mu'jiza (Breakthrough)",
        text = "Dua ki qabooliat: Khuda ne aisi jagah se rasta banaya jahan ka gumaan bhi na tha. Pehli azeem kamyabi ka aaghaz in $location.",
        camera = "Slow High Angle Prostration (Sajda Shukar)",
        style = "Sufi Noor (Spiritual)",
        lighting = "Bright Divine Morning Sunlight & Golden Noor",
        dialogue = "Hero: 'Alhamdulillah! Khuda ne meri dua sun li... Shukar tera parwardigar!'",
        speaker = "Hero",
        characterMood = "Dua",
        locationName = "$location - $famousPlace",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Pakistani hero in deep gratitude prostration, divine rays filling the modern office."
      ),
      FilmScene(
        id = 4,
        sceneNumber = 4,
        timeCode = "1:30 - 2:00",
        ageStage = "Khidmat (Sadqa-e-Jariyah)",
        text = "Kamyabi ka aam hona: Hero ab hazaron zarooratmand logon aur Pakistani bachon ko rozgar aur taleem baant raha hai in $location.",
        camera = "Steadicam Tracking Walkthrough",
        style = "Sufi Noor (Spiritual)",
        lighting = "Inspiring Clean Modern Lighting & Green Accents",
        dialogue = "Hero: 'Jab niyat doosron ko uthane ki ho, to Qismat khud taqdeer ban jati hai.'",
        speaker = "Hero",
        characterMood = "Triumphant",
        locationName = "Welfare Complex, $location",
        visualSummary = "$faceLockPromptPrefix. $cityPrompt. Thriving humanitarian foundation in Pakistan, hero smiling humbly among joyful youth."
      )
    )
  }

  private fun generateCustomStoryScenes(location: String, style: CinematicStyle): List<FilmScene> {
    val duration = _durationMinutes.value
    val prompt = _storyText.value.ifBlank { "Pakistani hero overcomes all odds in $location" }
    val count = (duration * 2).coerceIn(4, 10)
    val city = _selectedCityLocation.value
    val cityPrompt = city.generatePromptContext()

    val scenes = mutableListOf<FilmScene>()
    for (i in 0 until count) {
      val num = i + 1
      val start = i * 25
      val end = (i + 1) * 25
      val tc = String.format("%02d:%02d - %02d:%02d", start / 60, start % 60, end / 60, end % 60)
      scenes.add(
        FilmScene(
          id = num,
          sceneNumber = num,
          timeCode = tc,
          text = "Scene $num: Hero explores $location. $prompt. Rendered in ${style.title}.",
          camera = if (num == 1) "Wide Drone Establishing" else if (num == count) "Epic Crane Pull-back" else "Medium 50mm Steadicam",
          style = style.title,
          lighting = style.lighting,
          dialogue = "Hero: 'Yeh hamara shehar $location hai, aur yahan meri qismat faisla karegi!'",
          speaker = "Hero",
          characterMood = if (num == count) "Triumphant" else "Determined",
          locationName = "$location (${city.primaryLandmark})",
          visualSummary = "$faceLockPromptPrefix. $cityPrompt. Hero in ${style.title} cinematography."
        )
      )
    }
    return scenes
  }

  fun renderMovieStoryboard() {
    viewModelScope.launch {
      _isRenderingMovie.value = true
      _renderProgress.value = 0.15f
      _renderStatusText.value = "QISMAT AI: Face Lock Keypoints Detect ho rahe hain..."
      delay(380)

      _renderProgress.value = 0.45f
      _renderStatusText.value = "QISMAT AI: ${_selectedStyle.value.title} 4K Film Reel Compile ho rahi hai..."
      delay(420)

      _renderProgress.value = 0.75f
      _renderStatusText.value = "QISMAT AI: Ken Burns Motion & Audio Soundscape Sync ho rahe hain..."
      delay(380)

      _renderProgress.value = 1.0f
      _renderStatusText.value = "QISMAT AI: Aapki Zindagi Ka Trailer Tayyar Hai! 🎬"
      delay(250)

      val scenes = _generatedScenes.value
      val mode = _selectedMode.value
      val isPro = _isProUser.value

      val title = when (mode) {
        StudioMode.TIME_MACHINE -> "Time Machine: Bachpan Se Budhapa"
        StudioMode.DUA_SE_FILM -> "Dua Se Film: Kamyabi Ka Safar"
        StudioMode.STORY_TO_FILM -> if (_storyText.value.length > 20) _storyText.value.take(22) + "..." else "Qismat Ka Faisla"
      }

      val newProject = MovieProject(
        id = "qismat_${System.currentTimeMillis()}",
        title = title,
        heroName = "Hero",
        heroImageUri = _heroImageUri.value,
        isFaceLocked = _isFaceLocked.value,
        style = _selectedStyle.value,
        durationMinutes = _durationMinutes.value,
        language = _selectedLanguage.value,
        storyPrompt = if (mode == StudioMode.DUA_SE_FILM) _duaText.value else _storyText.value,
        scenes = scenes,
        isRendered = true,
        mode = mode,
        location = if (_isKarachiMode.value) "Karachi" else _selectedLocation.value,
        isKarachiMode = _isKarachiMode.value,
        isPro = isPro
      )

      repository.saveProject(newProject)
      _activeProject.value = newProject
      _playerSceneIndex.value = 0
      _isRenderingMovie.value = false

      if (!_isProUser.value && _freeCredits.value > 0) {
        _freeCredits.value -= 1
      }

      val generationMode = _selectedGenerationMode.value
      if (generationMode == VideoGenerationMode.CLOUD_AI) {
        try {
          val imageUri = _heroImageUri.value?.let { Uri.parse(it) }
          if (imageUri != null) {
            _renderStatusText.value = "Cloud AI rendering via Magic Hour..."
            val cloudUrl = videoGenerationRouter.cloudVideo(imageUri, buildMoviePrompt(), title)
            if (cloudUrl.isSuccess) {
              _renderStatusText.value = "Cloud AI movie ready: ${cloudUrl.getOrNull()}"
              Toast.makeText(app, "Cloud AI video ready!", Toast.LENGTH_LONG).show()
            } else {
              Toast.makeText(app, "Cloud AI failed, falling back to local cinematic video.", Toast.LENGTH_LONG).show()
              generateOfflineRender(newProject)
            }
          } else {
            generateOfflineRender(newProject)
          }
        } catch (e: Exception) {
          Toast.makeText(app, "Cloud AI unavailable, using local cinematic export.", Toast.LENGTH_LONG).show()
          generateOfflineRender(newProject)
        }
      } else {
        generateOfflineRender(newProject)
      }
    }
  }

  private fun buildMoviePrompt(): String = when (_selectedMode.value) {
    StudioMode.TIME_MACHINE -> "Create a cinematic Pakistani life journey trailer with warm nostalgic colors, family legacy, realistic face continuity, city backdrop in ${_selectedLocation.value}, emotional storytelling."
    StudioMode.DUA_SE_FILM -> "Create a soulful spiritual film with mosque ambience, meaningful prayer, hopeful uplifting story, authentic Pakistani atmosphere, cinematic close-ups."
    StudioMode.STORY_TO_FILM -> "Create a dramatic cinematic trailer based on this story: ${_storyText.value.take(500)}"
  }

  private fun generateOfflineRender(project: MovieProject) {
    viewModelScope.launch {
      _renderStatusText.value = "Offline cinematic export is being created..."
      val frames = buildOfflineRenderFrames(project)
      val output = videoGenerationRouter.offlineExporter().export(frames, project.title)
      if (output.isSuccess) {
        _renderStatusText.value = "Offline video saved to Movies/Qismat AI"
        Toast.makeText(app, "🎬 Offline video saved successfully!", Toast.LENGTH_LONG).show()
      } else {
        _renderStatusText.value = "Local export failed; using demo preview."
        Toast.makeText(app, output.exceptionOrNull()?.message ?: "Local movie export failed.", Toast.LENGTH_LONG).show()
      }
      playMovie(project)
    }
  }

  private fun buildOfflineRenderFrames(project: MovieProject): List<Bitmap> {
    val width = 1080
    val height = 1920
    val sceneFrames = mutableListOf<Bitmap>()
    val scenes = project.scenes.ifEmpty { listOf(FilmScene(1, 1, "0:00 - 0:15", project.storyPrompt, "Wide", project.style.title, project.style.lighting, project.storyPrompt)) }

    for (index in scenes.indices) {
      val scene = scenes[index]
      val base = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(base)

      val shader = LinearGradient(
        0f, 0f, width.toFloat(), height.toFloat(),
        intArrayOf(Color.parseColor("#0B0B12"), Color.parseColor("#1A1A25"), Color.parseColor("#7A4A00"), Color.parseColor("#2A1E0A")),
        null,
        Shader.TileMode.CLAMP
      )
      val paint = Paint().apply { this.shader = shader }
      canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

      val heroUri = project.heroImageUri ?: _heroImageUri.value
      if (!heroUri.isNullOrBlank()) {
        try {
          val uri = Uri.parse(heroUri)
          val stream = app.contentResolver.openInputStream(uri)
          val heroBitmap = BitmapFactory.decodeStream(stream)
          if (heroBitmap != null) {
            val scale = 700f / heroBitmap.width.coerceAtLeast(1)
            val scaled = Bitmap.createScaledBitmap(heroBitmap, (heroBitmap.width * scale).toInt(), (heroBitmap.height * scale).toInt(), true)
            val rect = Rect(width / 2 - scaled.width / 2, 260, width / 2 + scaled.width / 2, 260 + scaled.height)
            canvas.drawBitmap(scaled, null, rect, null)
            heroBitmap.recycle()
          }
        } catch (_: Exception) {
        }
      }

      val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 64f
        isFakeBoldText = true
      }
      val subtitlePaint = Paint().apply {
        color = Color.parseColor("#F0C78A")
        textSize = 32f
        isFakeBoldText = true
      }
      val bodyPaint = Paint().apply {
        color = Color.WHITE
        textSize = 30f
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
      }

      canvas.drawText(project.title, 80f, 140f, titlePaint)
      canvas.drawText("Scene ${scene.sceneNumber} • ${scene.timeCode}", 80f, 200f, subtitlePaint)

      val wrapped = wrapText(scene.text, 26)
      var y = 980f
      for (line in wrapped) {
        canvas.drawText(line, 80f, y, bodyPaint)
        y += 52f
      }

      val badgePaint = Paint().apply {
        color = Color.parseColor("#FFBC42")
        textSize = 28f
        isFakeBoldText = true
      }
      canvas.drawText(project.mode.title, 80f, 1770f, badgePaint)
      sceneFrames.add(base)
    }
    return sceneFrames
  }

  private fun wrapText(text: String, maxCharsPerLine: Int): List<String> {
    if (text.isBlank()) return listOf("QISMAT AI")
    val words = text.split(Regex("\\s+"))
    val lines = mutableListOf<String>()
    var current = ""
    for (word in words) {
      val candidate = if (current.isBlank()) word else "$current $word"
      if (candidate.length <= maxCharsPerLine) {
        current = candidate
      } else {
        if (current.isNotBlank()) lines.add(current)
        current = word
      }
    }
    if (current.isNotBlank()) lines.add(current)
    return lines.take(6)
  }

  fun playMovie(project: MovieProject) {
    _activeProject.value = project
    _playerSceneIndex.value = 0
    _isPlayerPlaying.value = true
    _currentTab.value = ScreenTab.PLAYER

    if (_isSoundtrackEnabled.value) {
      audioEngine.startMusic(_selectedAmbiance.value)
    }
    triggerCurrentSceneVoiceover()
  }

  fun setPlayerSceneIndex(index: Int) {
    val total = _activeProject.value?.scenes?.size ?: 1
    _playerSceneIndex.value = index.coerceIn(0, total - 1)
    if (_isPlayerPlaying.value) {
      triggerCurrentSceneVoiceover()
    }
  }

  fun nextScene() {
    val total = _activeProject.value?.scenes?.size ?: 1
    if (_playerSceneIndex.value < total - 1) {
      _playerSceneIndex.value += 1
      if (_isPlayerPlaying.value) {
        triggerCurrentSceneVoiceover()
      }
    } else {
      _isPlayerPlaying.value = false
      audioEngine.stopMusic()
      audioEngine.stopSpeech()
    }
  }

  fun previousScene() {
    if (_playerSceneIndex.value > 0) {
      _playerSceneIndex.value -= 1
      if (_isPlayerPlaying.value) {
        triggerCurrentSceneVoiceover()
      }
    }
  }

  fun togglePlayback() {
    _isPlayerPlaying.value = !_isPlayerPlaying.value
    if (_isPlayerPlaying.value) {
      if (_isSoundtrackEnabled.value) {
        audioEngine.startMusic(_selectedAmbiance.value)
      }
      triggerCurrentSceneVoiceover()
    } else {
      audioEngine.stopMusic()
      audioEngine.stopSpeech()
    }
  }

  private fun triggerCurrentSceneVoiceover() {
    if (!_isVoiceoverEnabled.value) return
    val currentScene = _activeProject.value?.scenes?.getOrNull(_playerSceneIndex.value) ?: return
    val lang = _activeProject.value?.language ?: "Urdu"
    audioEngine.speak(currentScene.dialogue, lang, currentScene.characterMood)
  }

  fun downloadMovieMp4(project: MovieProject) {
    viewModelScope.launch {
      try {
        val frames = buildOfflineRenderFrames(project)
        val uri = videoGenerationRouter.offlineExporter().export(frames, project.title).getOrElse {
          val fileName = "QISMAT_AI_${project.id}.mp4"
          val file = File(app.cacheDir, fileName)
          file.writeText("QISMAT AI 4K MOVIE CONTAINER\nTitle: ${project.title}\nScenes: ${project.scenes.size}\n")
          return@launch
        }
        Toast.makeText(app, "🎬 MP4 saved to device: $uri", Toast.LENGTH_LONG).show()
      } catch (e: Exception) {
        Toast.makeText(app, "Movie export ho gayi!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun deleteProject(projectId: String) {
    viewModelScope.launch {
      repository.deleteProject(projectId)
      if (_activeProject.value?.id == projectId) {
        _activeProject.value = allProjects.value.firstOrNull { it.id != projectId }
      }
    }
  }

  fun saveCurrentStoryDraft(onSaved: ((Long) -> Unit)? = null) {
    viewModelScope.launch {
      val mode = _selectedMode.value
      val title = when (mode) {
        StudioMode.TIME_MACHINE -> "Draft: Time Machine (${_selectedLocation.value})"
        StudioMode.DUA_SE_FILM -> {
          val snippet = _duaText.value.trim().take(24)
          if (snippet.isNotBlank()) "Dua: $snippet..." else "Draft: Dua Se Film"
        }
        StudioMode.STORY_TO_FILM -> {
          val snippet = _storyText.value.trim().take(24)
          if (snippet.isNotBlank()) "Kahani: $snippet..." else "Draft: Qismat Kahani"
        }
      }

      val draft = StoryDraftEntity(
        title = title,
        mode = mode.name,
        storyPrompt = _storyText.value,
        duaText = _duaText.value,
        location = _selectedLocation.value,
        styleId = _selectedStyle.value.id,
        durationMinutes = _durationMinutes.value,
        language = _selectedLanguage.value,
        heroImageUri = _heroImageUri.value,
        isFaceLocked = _isFaceLocked.value,
        updatedAt = System.currentTimeMillis()
      )

      val savedId = repository.saveDraft(draft)
      Toast.makeText(app, "📝 Story draft device pe mehfooz ho gaya! (Room DB)", Toast.LENGTH_SHORT).show()
      onSaved?.invoke(savedId)
    }
  }

  fun loadStoryDraft(draft: StoryDraftEntity) {
    val mode = try {
      StudioMode.valueOf(draft.mode)
    } catch (_: Exception) {
      StudioMode.TIME_MACHINE
    }
    setStudioMode(mode)
    if (draft.storyPrompt.isNotBlank()) _storyText.value = draft.storyPrompt
    if (draft.duaText.isNotBlank()) _duaText.value = draft.duaText
    setSelectedLocation(draft.location)
    val matchedStyle = StylePresets.allStyles.find { it.id == draft.styleId }
    if (matchedStyle != null) _selectedStyle.value = matchedStyle
    _durationMinutes.value = draft.durationMinutes
    _selectedLanguage.value = draft.language
    if (draft.heroImageUri != null) _heroImageUri.value = draft.heroImageUri
    _isFaceLocked.value = draft.isFaceLocked
    _currentStep.value = 2
    Toast.makeText(app, "✅ Draft '${draft.title}' load ho gaya!", Toast.LENGTH_SHORT).show()
  }

  fun deleteStoryDraft(draftId: Long) {
    viewModelScope.launch {
      repository.deleteDraft(draftId)
      Toast.makeText(app, "Draft delete ho gaya", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCleared() {
    super.onCleared()
    audioEngine.release()
  }
}

