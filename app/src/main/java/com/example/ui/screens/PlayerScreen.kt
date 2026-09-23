package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenTab
import com.example.ui.components.BigRedButton
import com.example.ui.components.CinematicFilmFrame
import com.example.ui.components.PaywallProDialog
import com.example.ui.components.StoryboardGrid
import com.example.ui.components.TypewriterDialogueText
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldDark
import com.example.ui.theme.QismatGoldSubtle
import com.example.viewmodel.StoryViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
  viewModel: StoryViewModel,
  modifier: Modifier = Modifier
) {
  val activeProject by viewModel.activeProject.collectAsState()
  val playerSceneIndex by viewModel.playerSceneIndex.collectAsState()
  val isPlayerPlaying by viewModel.isPlayerPlaying.collectAsState()
  val selectedAmbiance by viewModel.selectedAmbiance.collectAsState()
  val isVoiceoverEnabled by viewModel.isVoiceoverEnabled.collectAsState()
  val isSoundtrackEnabled by viewModel.isSoundtrackEnabled.collectAsState()
  val isProUser by viewModel.isProUser.collectAsState()
  val showPaywallDialog by viewModel.showPaywallDialog.collectAsState()
  var showDirectorNotes by remember { mutableStateOf(false) }

  // Auto-play slideshow Ken Burns effect when playing
  LaunchedEffect(isPlayerPlaying, playerSceneIndex) {
    if (isPlayerPlaying) {
      delay(4200)
      viewModel.nextScene()
    }
  }

  val project = activeProject

  if (project == null || project.scenes.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(CinematicBg)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.Movie,
          contentDescription = "No Film",
          tint = CinematicTextDim,
          modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Abhi koi film select nahi hui",
          color = CinematicWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Create Studio mein jakar apni pehli film banayein!",
          color = CinematicTextMuted,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        BigRedButton(
          text = "Bina Key Ke Film Banao 🎬 Free",
          onClick = { viewModel.selectTab(ScreenTab.CREATE_STUDIO) }
        )
      }
    }
    return
  }

  val currentScene = project.scenes.getOrNull(playerSceneIndex) ?: project.scenes.first()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CinematicBg)
      .padding(horizontal = 16.dp)
      .testTag("player_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // Movie Header & Mode Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = project.title,
            color = CinematicWhite,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = project.mode.title,
              color = QismatGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = " • 📍 ${project.location} • ${project.scenes.size} Scenes",
              color = CinematicTextMuted,
              fontSize = 11.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isProUser || project.isPro) QismatGoldSubtle else Color(0xFF1E1E1E))
            .border(1.dp, if (isProUser || project.isPro) QismatGold else CinematicBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isProUser || project.isPro) "PRO 4K (NO WATERMARK)" else "FREE (WATERMARK)",
            color = if (isProUser || project.isPro) QismatGold else CinematicTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // CINEMATIC STAGE (Letterbox Frame with Ken Burns Effect)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .border(1.dp, CinematicBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          CinematicFilmFrame(
            style = project.style,
            sceneNumber = currentScene.sceneNumber,
            cameraShot = currentScene.camera,
            heroImageUri = project.heroImageUri,
            isFaceLocked = project.isFaceLocked,
            modifier = Modifier.height(210.dp),
            isKenBurnsActive = isPlayerPlaying,
            ageStage = currentScene.ageStage,
            locationName = currentScene.locationName,
            isPro = isProUser || project.isPro
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Subtitles Box with Typewriter Effect
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black.copy(alpha = 0.90f))
              .border(1.dp, CinematicBorder, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = "Quote",
                    tint = QismatGold,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${currentScene.speaker} (${currentScene.characterMood}):",
                    color = QismatGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                if (isVoiceoverEnabled) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Mic,
                      contentDescription = "TTS Active",
                      tint = QismatEmerald,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = "EMOTIONAL TTS",
                      color = QismatEmerald,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              TypewriterDialogueText(
                fullText = currentScene.dialogue,
                charDelayMs = 22L
              )
            }
          }
        }
      }
    }

    // Playback Timeline & Transport Controls
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Progress Scrubber
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = currentScene.timeCode.split("-").firstOrNull()?.trim() ?: "00:00",
              color = CinematicWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (currentScene.ageStage.isNotBlank()) currentScene.ageStage else "Scene ${playerSceneIndex + 1}/${project.scenes.size}",
              color = QismatGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${project.durationMinutes}:00",
              color = CinematicTextMuted,
              fontSize = 12.sp
            )
          }

          Slider(
            value = playerSceneIndex.toFloat(),
            onValueChange = { viewModel.setPlayerSceneIndex(it.toInt()) },
            valueRange = 0f..(project.scenes.size - 1).toFloat().coerceAtLeast(1f),
            steps = (project.scenes.size - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
              thumbColor = QismatGold,
              activeTrackColor = QismatGold,
              inactiveTrackColor = CinematicBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("player_timeline_slider")
          )

          // Playback Buttons & Audio Toggles
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Voiceover toggle
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isVoiceoverEnabled) QismatEmerald.copy(alpha = 0.2f) else CinematicSurface)
                .border(
                  1.dp,
                  if (isVoiceoverEnabled) QismatEmerald else CinematicBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { viewModel.toggleVoiceover() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (isVoiceoverEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                  contentDescription = "Voiceover",
                  tint = if (isVoiceoverEnabled) QismatEmerald else CinematicTextMuted,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isVoiceoverEnabled) "Voice ON" else "Voice OFF",
                  color = if (isVoiceoverEnabled) QismatEmerald else CinematicTextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Transport controls
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { viewModel.previousScene() },
                modifier = Modifier.size(40.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.SkipPrevious,
                  contentDescription = "Previous Scene",
                  tint = CinematicWhite,
                  modifier = Modifier.size(26.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(QismatGold)
                  .clickable { viewModel.togglePlayback() }
                  .testTag("play_pause_button"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isPlayerPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlayerPlaying) "Pause" else "Play",
                  tint = Color.Black,
                  modifier = Modifier.size(32.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              IconButton(
                onClick = { viewModel.nextScene() },
                modifier = Modifier.size(40.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.SkipNext,
                  contentDescription = "Next Scene",
                  tint = CinematicWhite,
                  modifier = Modifier.size(26.dp)
                )
              }
            }

            // Soundtrack toggle
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSoundtrackEnabled) QismatGold.copy(alpha = 0.2f) else CinematicSurface)
                .border(
                  1.dp,
                  if (isSoundtrackEnabled) QismatGold else CinematicBorder,
                  RoundedCornerShape(8.dp)
                )
                .clickable { viewModel.toggleSoundtrack() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (isSoundtrackEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                  contentDescription = "Soundtrack",
                  tint = if (isSoundtrackEnabled) QismatGold else CinematicTextMuted,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isSoundtrackEnabled) "BGM ON" else "BGM OFF",
                  color = if (isSoundtrackEnabled) QismatGold else CinematicTextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // EXPORT MP4 & VIRAL SHARE ROW
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Download MP4
        Button(
          onClick = { viewModel.downloadMovieMp4(project) },
          colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Download MP4", fontWeight = FontWeight.Black, fontSize = 13.sp)
          }
        }

        // Viral Share
        Button(
          onClick = { viewModel.shareReferral() },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2F20), contentColor = QismatEmerald),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Share Film 🎬", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }

    // Ambiance Soundscape Selector (including Nasheed & Qismat Golden Themes)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Audiotrack,
              contentDescription = "Ambiance",
              tint = QismatGold,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Procedural Soundscapes (No API Required)",
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val ambiances = listOf(
              "Qismat Golden Theme",
              "Islamic Nasheed (Acapella Drone)",
              "Time Machine Passage",
              "Karachi Midnight Beat",
              "Cinematic Orchestral"
            )
            items(ambiances) { ambiance ->
              val isSelected = selectedAmbiance == ambiance
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) QismatGold else CinematicSurfaceElevated)
                  .border(
                    1.dp,
                    if (isSelected) QismatGold else CinematicBorder,
                    RoundedCornerShape(10.dp)
                  )
                  .clickable { viewModel.setAmbiance(ambiance) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "🎵 $ambiance",
                  color = if (isSelected) Color.Black else CinematicWhite,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // Director Notes
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showDirectorNotes = !showDirectorNotes },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Director Notes",
                tint = CinematicCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Scene Narrative & Identity Lock Specs",
                color = CinematicWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
            Text(
              text = if (showDirectorNotes) "Hide ▲" else "View ▼",
              color = CinematicCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          AnimatedVisibility(visible = showDirectorNotes) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
              Text(
                text = "Scene Description: ${currentScene.text}",
                color = CinematicTextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Camera Rig: ${currentScene.camera}",
                color = CinematicWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Lighting Setup: ${currentScene.lighting}",
                color = QismatGold,
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Location: ${currentScene.locationName}",
                color = QismatEmerald,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // Storyboard Scenes Gallery Grid (Powered by Coil)
    item {
      StoryboardGrid(
        scenes = project.scenes,
        style = project.style,
        heroImageUri = project.heroImageUri,
        isFaceLocked = project.isFaceLocked,
        isPro = project.isPro,
        selectedSceneNumber = currentScene.sceneNumber,
        onSceneClick = { scene ->
          val targetIndex = project.scenes.indexOfFirst { it.sceneNumber == scene.sceneNumber }
          if (targetIndex >= 0) {
            viewModel.setPlayerSceneIndex(targetIndex)
          }
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Paywall Dialog if opened
  if (showPaywallDialog) {
    PaywallProDialog(
      onDismiss = { viewModel.closePaywallDialog() },
      onUnlockPro = { viewModel.upgradeToPro() },
      onInviteFriend = { viewModel.shareReferral() }
    )
  }
}
