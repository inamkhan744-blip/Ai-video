package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CityLocation
import com.example.data.PakistanCitiesData
import com.example.data.StylePresets
import com.example.model.FilmScene
import com.example.model.StudioMode
import com.example.model.VideoGenerationMode
import com.example.ui.components.BigRedButton
import com.example.ui.components.BismillahHeader
import com.example.ui.components.CameraXCaptureDialog
import com.example.ui.components.CinematicFilmFrame
import com.example.ui.components.CinematicStyleCard
import com.example.ui.components.DashedUploadBox
import com.example.ui.components.PaywallProDialog
import com.example.ui.components.StoryboardGrid
import com.example.ui.components.StoryDraftsDialog
import com.example.data.local.StoryDraftEntity
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGreen
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatEmeraldSubtle
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldDark
import com.example.ui.theme.QismatGoldSubtle
import com.example.viewmodel.StoryViewModel

@Composable
fun CreateStudioScreen(
  viewModel: StoryViewModel,
  modifier: Modifier = Modifier
) {
  val selectedMode by viewModel.selectedMode.collectAsState()
  val isKarachiMode by viewModel.isKarachiMode.collectAsState()
  val selectedLocation by viewModel.selectedLocation.collectAsState()
  val selectedCityLocation by viewModel.selectedCityLocation.collectAsState()
  val detectedCityMessage by viewModel.detectedCityMessage.collectAsState()
  var isLocationDropdownOpen by remember { mutableStateOf(false) }
  var citySearchQuery by remember { mutableStateOf("") }
  var selectedCityGroup by remember { mutableStateOf("All") }
  val currentStep by viewModel.currentStep.collectAsState()
  val heroImageUri by viewModel.heroImageUri.collectAsState()
  val isFaceLocked by viewModel.isFaceLocked.collectAsState()
  val selectedStyle by viewModel.selectedStyle.collectAsState()
  val durationMinutes by viewModel.durationMinutes.collectAsState()
  val storyText by viewModel.storyText.collectAsState()
  val duaText by viewModel.duaText.collectAsState()
  val selectedLanguage by viewModel.selectedLanguage.collectAsState()
  val isSplittingScenes by viewModel.isSplittingScenes.collectAsState()
  val isRenderingMovie by viewModel.isRenderingMovie.collectAsState()
  val renderProgress by viewModel.renderProgress.collectAsState()
  val renderStatusText by viewModel.renderStatusText.collectAsState()
  val generatedScenes by viewModel.generatedScenes.collectAsState()
  val isProUser by viewModel.isProUser.collectAsState()
  val showPaywallDialog by viewModel.showPaywallDialog.collectAsState()
  val selectedGenerationMode by viewModel.selectedGenerationMode.collectAsState()
  val availableGenerationModes = viewModel.videoGenerationModes()

  val context = LocalContext.current
  val pendingHeroImageUri by viewModel.pendingHeroImageUri.collectAsState()

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.setPendingHeroImageUri(uri.toString())
    }
  }

  var showCameraXDialog by remember { mutableStateOf(false) }
  var showDraftsDialog by remember { mutableStateOf(false) }
  val allDrafts by viewModel.allDrafts.collectAsState()

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      viewModel.setHeroImageBitmap(bitmap, isPending = true)
    }
  }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      showCameraXDialog = true
    } else {
      Toast.makeText(context, "Camera permission zaroori hai photo lene ke liye", Toast.LENGTH_SHORT).show()
    }
  }

  val onCameraClick: () -> Unit = {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    if (hasPermission) {
      showCameraXDialog = true
    } else {
      cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  var voiceTargetMode by remember { mutableStateOf<StudioMode?>(null) }
  val speechRecognizerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val spokenText = spokenList?.firstOrNull()
      if (!spokenText.isNullOrBlank()) {
        when (voiceTargetMode) {
          StudioMode.DUA_SE_FILM -> viewModel.appendDuaVoice(spokenText)
          StudioMode.STORY_TO_FILM -> viewModel.appendStoryVoice(spokenText)
          else -> viewModel.appendStoryVoice(spokenText)
        }
        Toast.makeText(context, "Voice add ho gayi: \"$spokenText\"", Toast.LENGTH_SHORT).show()
      }
    }
  }

  val launchVoiceInput: (StudioMode) -> Unit = { mode ->
    voiceTargetMode = mode
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK")
      putExtra(RecognizerIntent.EXTRA_PROMPT, if (mode == StudioMode.DUA_SE_FILM) "Apni Dua bolein (Urdu/Hindi)..." else "Apni film ki kahani bolein...")
    }
    try {
      speechRecognizerLauncher.launch(intent)
    } catch (_: Exception) {
      val fallbackIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      }
      try {
        speechRecognizerLauncher.launch(fallbackIntent)
      } catch (_: Exception) {
        Toast.makeText(context, "Voice input is device par dastiyab nahi hai", Toast.LENGTH_SHORT).show()
      }
    }
  }

  var sceneBeingEdited by remember { mutableStateOf<FilmScene?>(null) }
  var editedDialogueText by remember { mutableStateOf("") }
  var editedSceneDesc by remember { mutableStateOf("") }
  var editedCameraShot by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CinematicBg)
      .padding(horizontal = 16.dp)
      .testTag("create_studio_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, QismatGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "🎬 Select Studio Mode:",
            color = QismatGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            StudioModeTab(
              title = "⏳ Time Machine",
              subtitle = "5 Umrein",
              isSelected = selectedMode == StudioMode.TIME_MACHINE,
              onClick = { viewModel.setStudioMode(StudioMode.TIME_MACHINE) },
              modifier = Modifier.weight(1f)
            )
            StudioModeTab(
              title = "🤲 Dua Se Film",
              subtitle = "Spiritual",
              isSelected = selectedMode == StudioMode.DUA_SE_FILM,
              onClick = { viewModel.setStudioMode(StudioMode.DUA_SE_FILM) },
              modifier = Modifier.weight(1f)
            )
            StudioModeTab(
              title = "✍️ Custom Story",
              subtitle = "Urdu/Hindi",
              isSelected = selectedMode == StudioMode.STORY_TO_FILM,
              onClick = { viewModel.setStudioMode(StudioMode.STORY_TO_FILM) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Render Mode",
            color = QismatGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val generationOptions = availableGenerationModes
            generationOptions.forEach { mode ->
              val isSelected = selectedGenerationMode == mode
              val label = when (mode) {
                VideoGenerationMode.OFFLINE_CINEMATIC -> "Offline MP4"
                VideoGenerationMode.CLOUD_AI -> "Cloud AI"
              }
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) QismatGold.copy(alpha = 0.18f) else CinematicSurface)
                  .border(1.dp, if (isSelected) QismatGold else CinematicBorder, RoundedCornerShape(12.dp))
                  .clickable { viewModel.setGenerationMode(mode) }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  color = if (isSelected) QismatGold else CinematicWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (selectedGenerationMode == VideoGenerationMode.OFFLINE_CINEMATIC) "No API key required — saves real MP4 to Movies/Qismat AI." else "Cloud AI mode is optional and needs a valid Magic Hour API key.",
            color = CinematicTextMuted,
            fontSize = 10.sp
          )
        }
      }
    }

    item {
      StudioStepperHeader(
        activeStep = currentStep,
        onStepClick = { step -> viewModel.setStep(step) }
      )
    }

    item {
      StepperSection(
        stepNumber = 1,
        title = "Hero Face Lock Pro",
        subtitle = "Camera se live selfie lein ya gallery se front-face photo select karein",
        isExpanded = currentStep == 1,
        isCompleted = heroImageUri != null || currentStep > 1,
        onHeaderClick = { viewModel.setStep(1) }
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          if (pendingHeroImageUri != null) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, QismatGold, RoundedCornerShape(16.dp)),
              colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = QismatGold,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Face Preview & Confirmation",
                      color = QismatGold,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                  }
                  Text(
                    text = "AI Auto Face Centered",
                    color = CinematicTextMuted,
                    fontSize = 10.sp
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                  modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, QismatGold, CircleShape)
                    .background(Color.Black),
                  contentAlignment = Alignment.Center
                ) {
                  AsyncImage(
                    model = pendingHeroImageUri,
                    contentDescription = "Pending Hero Face",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = "Kia ye chehra theek hai? Hum is chehre ko tamam scenes mein 100% lock rakhein ge.",
                  color = CinematicWhite,
                  fontSize = 12.sp,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  OutlinedButton(
                    onClick = { viewModel.retakeHeroImage() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CinematicWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinematicBorder),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retake", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = { viewModel.confirmHeroImage() },
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Use This Face ✅", fontSize = 12.sp, fontWeight = FontWeight.Black)
                  }
                }
              }
            }
          } else {
            DashedUploadBox(
              imageUri = heroImageUri,
              isFaceLocked = isFaceLocked,
              onUploadClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = onCameraClick,
                modifier = Modifier
                  .weight(1f)
                  .testTag("camera_capture_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CinematicSurfaceElevated, contentColor = CinematicWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, QismatGold.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = "Camera",
                  tint = QismatGold,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Live Camera Selfie",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Button(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("gallery_picker_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CinematicSurfaceElevated, contentColor = CinematicWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CinematicBorder),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.PhotoLibrary,
                  contentDescription = "Gallery",
                  tint = CinematicCyan,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Phone Gallery",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Ya Pakistani demo hero chuno:",
              color = CinematicTextMuted,
              fontSize = 11.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              DemoAvatarChip(name = "Hero Daniyal (Karachi)") {
                viewModel.setHeroImageUri("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150")
              }
              DemoAvatarChip(name = "Heroine Fatima (Lahore)") {
                viewModel.setHeroImageUri("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150")
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .border(1.dp, if (isFaceLocked) QismatEmerald.copy(alpha = 0.5f) else CinematicBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isFaceLocked) QismatEmerald.copy(alpha = 0.2f) else CinematicSurface),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Face Lock",
                    tint = if (isFaceLocked) QismatEmerald else CinematicTextMuted,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Face Lock Keypoints Active",
                    color = CinematicWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = if (isFaceLocked) "Har scene mein 100% yahi chehra rahega ✅" else "Chehra lock nahi hai",
                    color = if (isFaceLocked) QismatEmerald else CinematicTextDim,
                    fontSize = 11.sp
                  )
                }
              }

              Switch(
                checked = isFaceLocked,
                onCheckedChange = { viewModel.toggleFaceLock(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.Black,
                  checkedTrackColor = QismatEmerald,
                  uncheckedThumbColor = CinematicTextMuted,
                  uncheckedTrackColor = CinematicSurface
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          BigRedButton(
            text = "Agla Step: Qismat Story & Mode ➡️",
            onClick = { viewModel.setStep(2) }
          )
        }
      }
    }

    item {
      StepperSection(
        stepNumber = 2,
        title = when (selectedMode) {
          StudioMode.TIME_MACHINE -> "⏳ Time Machine (5 Umrein)"
          StudioMode.DUA_SE_FILM -> "🤲 Dua Se Film (Spiritual)"
          StudioMode.STORY_TO_FILM -> "🎬 Custom Story & Style"
        },
        subtitle = "Apni kahani ya dua likhein aur Pakistani locations chunein",
        isExpanded = currentStep == 2,
        isCompleted = currentStep > 2 || generatedScenes.isNotEmpty(),
        onHeaderClick = { viewModel.setStep(2) }
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(CinematicSurfaceElevated)
                .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { viewModel.saveCurrentStoryDraft() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("save_draft_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.BookmarkBorder,
                  contentDescription = "Save Draft",
                  tint = QismatGold,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Draft Save Karein",
                  color = QismatGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (allDrafts.isNotEmpty()) QismatGold.copy(alpha = 0.15f) else CinematicSurfaceElevated)
                .border(1.dp, if (allDrafts.isNotEmpty()) QismatGold else CinematicBorder, RoundedCornerShape(10.dp))
                .clickable { showDraftsDialog = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("view_drafts_button")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Bookmark,
                  contentDescription = "Saved Drafts",
                  tint = if (allDrafts.isNotEmpty()) QismatGold else CinematicTextMuted,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Saved Drafts (${allDrafts.size})",
                  color = if (allDrafts.isNotEmpty()) QismatGold else CinematicWhite,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          when (selectedMode) {
            StudioMode.TIME_MACHINE -> {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1500))
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏳", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Time Machine: 1 Photo se 5 Umrein",
                      color = QismatGold,
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                  TimeMachineStageItem("1. Bachpan (5 Years)", "Primary school in Karachi, innocent smile, marbles")
                  TimeMachineStageItem("2. Jawani (25 Years)", "University hero, Sea View sunset, ambitious dream")
                  TimeMachineStageItem("3. Shaadi & Kamyabi (35 Years)", "Pakistani businessman, Dubai skyline, royal sherwani")
                  TimeMachineStageItem("4. Budhapa (65 Years)", "Kind elder with grandkids, serene courtyard wisdom")
                  TimeMachineStageItem("5. Legacy (Shaan)", "Golden framed portrait on museum wall, timeless hero")
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
            }

            StudioMode.DUA_SE_FILM -> {
              BismillahHeader()
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Apni Dua / Niyyah Yahan Likhein:",
                  color = CinematicWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QismatEmerald.copy(alpha = 0.2f))
                    .border(1.dp, QismatEmerald, RoundedCornerShape(20.dp))
                    .clickable { launchVoiceInput(StudioMode.DUA_SE_FILM) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("dua_mic_button")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Mic,
                      contentDescription = "Bolein",
                      tint = QismatEmerald,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Dua Bolein 🎙️",
                      color = QismatEmerald,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = duaText,
                onValueChange = { viewModel.setDuaText(it) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp)
                  .testTag("dua_input_textarea"),
                placeholder = {
                  Text(
                    text = "Ya Allah mujhe bada software builder bana de taake main apne walidain ka sar fakhar se buland karun...",
                    color = CinematicTextDim,
                    fontSize = 12.sp
                  )
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = CinematicSurface,
                  unfocusedContainerColor = CinematicSurface,
                  focusedBorderColor = QismatEmerald,
                  unfocusedBorderColor = CinematicBorder,
                  focusedTextColor = CinematicWhite,
                  unfocusedTextColor = CinematicWhite
                ),
                shape = RoundedCornerShape(14.dp)
              )

              Spacer(modifier = Modifier.height(8.dp))

              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                  StoryStarterChip("🤲 Walidain Ka Sar Fakhar Se Buland") {
                    viewModel.setDuaText("Ya Allah mujhe itna kamyab bana ke mere walidain dunya aur aakhirat mein mujh par fakhar karein.")
                  }
                }
                item {
                  StoryStarterChip("💼 Pakistani Tech Empire") {
                    viewModel.setDuaText("Ya Allah mujhe aisi company banane ki taufeeq de jahan se hazaron Pakistaniyon ko halal rizq mile.")
                  }
                }
                item {
                  StoryStarterChip("💖 Ghar Ki Khushhali") {
                    viewModel.setDuaText("Ya Allah mere ghar mein barkat, shifa aur be-shumar khushiyan ata farma.")
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
            }

            StudioMode.STORY_TO_FILM -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Apni Kahani Likho (Urdu/Hindi/English):",
                  color = CinematicWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QismatGold.copy(alpha = 0.2f))
                    .border(1.dp, QismatGold, RoundedCornerShape(20.dp))
                    .clickable { launchVoiceInput(StudioMode.STORY_TO_FILM) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("story_mic_button")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Mic,
                      contentDescription = "Bolein",
                      tint = QismatGold,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Kahani Bolein 🎙️",
                      color = QismatGold,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = storyText,
                onValueChange = { viewModel.setStoryText(it) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(120.dp)
                  .testTag("story_input_textarea"),
                placeholder = {
                  Text(
                    text = "Karachi ke pur-umeed naujawan ki dastaan jo din raat mehnat karke dunya mein apna naam banata hai...",
                    color = CinematicTextDim,
                    fontSize = 12.sp
                  )
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = CinematicSurface,
                  unfocusedContainerColor = CinematicSurface,
                  focusedBorderColor = QismatGold,
                  unfocusedBorderColor = CinematicBorder,
                  focusedTextColor = CinematicWhite,
                  unfocusedTextColor = CinematicWhite
                ),
                shape = RoundedCornerShape(14.dp)
              )

              Spacer(modifier = Modifier.height(8.dp))

              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                  StoryStarterChip("🌊 Karachi Sea View Action") {
                    viewModel.setStoryText("Karachi Clifton ke samandar par hero dushmanon ka muqabla karta hai aur city ka hero banta hai.")
                  }
                }
                item {
                  StoryStarterChip("❤️ Lahore Old City Love Story") {
                    viewModel.setStoryText("Androon Lahore ke haveliyon ke darmiyan 1970 ki khubsurat Pakistani mohabbat ki dastaan.")
                  }
                }
                item {
                  StoryStarterChip("🏔️ Quetta Hills Adventure") {
                    viewModel.setStoryText("Quetta ke sard pahadon mein hero khazana dhoondte hue ek pur-asraar dost se milta hai.")
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
            }
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .testTag("location_selector_card"),
            colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = QismatGold,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Film Location:",
                    color = CinematicWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(QismatEmerald.copy(alpha = 0.25f), QismatGold.copy(alpha = 0.25f))))
                    .border(1.dp, QismatEmerald, RoundedCornerShape(20.dp))
                    .clickable { viewModel.autoDetectCity() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("auto_detect_city_button")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.MyLocation,
                      contentDescription = "Auto Detect",
                      tint = QismatEmerald,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      text = "Auto-Detect My City",
                      color = QismatEmerald,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              if (detectedCityMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = detectedCityMessage ?: "",
                  color = QismatEmerald,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(QismatGold.copy(alpha = 0.12f))
                  .border(1.dp, QismatGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                  .clickable { isLocationDropdownOpen = !isLocationDropdownOpen }
                  .padding(12.dp)
                  .testTag("selected_city_dropdown_toggle")
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = selectedCityLocation.emoji,
                        fontSize = 16.sp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "${selectedCityLocation.name} - ${selectedCityLocation.primaryLandmark}",
                        color = CinematicWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(QismatGoldDark)
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = selectedCityLocation.category,
                          color = Color.Black,
                          fontWeight = FontWeight.Bold,
                          fontSize = 8.sp
                        )
                      }
                    }
                  }
                  Icon(
                    imageVector = if (isLocationDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle location list",
                    tint = QismatGold,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              AnimatedVisibility(
                visible = isLocationDropdownOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
              ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                  val visibleCities = PakistanCitiesData.allCities.filter {
                    citySearchQuery.isBlank() || city -> city.name.contains(citySearchQuery, ignoreCase = true) || city.primaryLandmark.contains(citySearchQuery, ignoreCase = true)
                  }
                  if (visibleCities.isNotEmpty()) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Search city",
                        color = CineamaticTextMuted,
                        fontSize = 10.sp
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                      value = citySearchQuery,
                      onValueChange = { citySearchQuery = it },
                      modifier = Modifier.fillMaxWidth(),
                      placeholder = { Text("Search city...", color = CinematicTextDim, fontSize = 11.sp) },
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QismatGold,
                        unfocusedBorderColor = CinematicBorder,
                        focusedTextColor = CinematicWhite,
                        unfocusedTextColor = CinematicWhite,
                        focusedContainerColor = CinematicSurface,
                        unfocusedContainerColor = CinematicSurface
                      )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      items(visibleCities.take(8)) { city ->
                        CityChip(
                          city = city,
                          isSelected = selectedCityLocation.name == city.name,
                          onClick = {
                            viewModel.selectCity(city)
                            isLocationDropdownOpen = false
                          }
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          BigRedButton(
            text = "Render My Qismat Film 🎬",
            onClick = { viewModel.renderMovieStoryboard() }
          )
        }
      }
    }

    if (isRenderingMovie) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = renderStatusText,
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
              progress = renderProgress,
              modifier = Modifier.fillMaxWidth(),
              color = QismatGold,
              trackColor = CinematicSurface
            )
          }
        }
      }
    }

    if (generatedScenes.isNotEmpty()) {
      item {
        StoryboardGrid(
          scenes = generatedScenes,
          onEditScene = { scene ->
            sceneBeingEdited = scene
            editedDialogueText = scene.dialogue
            editedSceneDesc = scene.text
            editedCameraShot = scene.camera
          },
          onRegenerateScene = { sceneNumber -> viewModel.regenerateSceneDialogue(sceneNumber) }
        )
      }
    }
  }

  if (showPaywallDialog) {
    PaywallProDialog(
      onClose = { viewModel.closePaywallDialog() },
      onUpgrade = { viewModel.upgradeToPro() }
    )
  }

  if (showDraftsDialog) {
    StoryDraftsDialog(
      drafts = allDrafts,
      onDismiss = { showDraftsDialog = false },
      onLoadDraft = { draft ->
        viewModel.loadStoryDraft(draft)
        showDraftsDialog = false
      },
      onDeleteDraft = { draft -> viewModel.deleteStoryDraft(draft.id) }
    )
  }
}

@Composable
private fun StudioModeTab(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) QismatGold.copy(alpha = 0.18f) else CinematicSurface)
      .border(1.dp, if (isSelected) QismatGold else CinematicBorder, RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = title,
        color = if (isSelected) QismatGold else CinematicWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
      Text(
        text = subtitle,
        color = if (isSelected) QismatGold else CinematicTextMuted,
        fontSize = 8.sp
      )
    }
  }
}

@Composable
private fun StepperSection(
  stepNumber: Int,
  title: String,
  subtitle: String,
  isExpanded: Boolean,
  isCompleted: Boolean,
  onHeaderClick: () -> Unit,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, if (isCompleted) QismatGold.copy(alpha = 0.7f) else CinematicBorder, RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onHeaderClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(if (isCompleted) QismatGold else CinematicBorder),
            contentAlignment = Alignment.Center
          ) {
            Text(text = stepNumber.toString(), color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = title,
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = subtitle,
              color = CinematicTextMuted,
              fontSize = 10.sp
            )
          }
        }
        Icon(
          imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = QismatGold,
          modifier = Modifier.size(18.dp)
        )
      }
      AnimatedVisibility(visible = isExpanded, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier.padding(top = 12.dp)) {
          content()
        }
      }
    }
  }
}

@Composable
private fun StudioStepperHeader(
  activeStep: Int,
  onStepClick: (Int) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    listOf(1, 2, 3).forEach { step ->
      val selected = step == activeStep
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(if (selected) QismatGold else CinematicSurface)
          .border(1.dp, if (selected) QismatGold else CinematicBorder, RoundedCornerShape(12.dp))
          .clickable { onStepClick(step) }
          .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Step $step",
          color = if (selected) Color.Black else CinematicWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun DemoAvatarChip(
  name: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(QismatGold.copy(alpha = 0.16f))
      .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Text(
      text = name,
      color = CinematicWhite,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun CityChip(
  city: com.example.data.CityLocation,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) QismatGold.copy(alpha = 0.18f) else CinematicSurface)
      .border(1.dp, if (isSelected) QismatGold else CinematicBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Text(
      text = "${city.emoji} ${city.name}",
      color = if (isSelected) QismatGold else CinematicWhite,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun TimeMachineStageItem(title: String, subtitle: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(QismatGold)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(
        text = title,
        color = CinematicWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
      Text(
        text = subtitle,
        color = CinematicTextMuted,
        fontSize = 9.sp
      )
    }
  }
}

@Composable
private fun StoryStarterChip(text: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(QismatGold.copy(alpha = 0.12f))
      .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Text(
      text = text,
      color = CinematicWhite,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

