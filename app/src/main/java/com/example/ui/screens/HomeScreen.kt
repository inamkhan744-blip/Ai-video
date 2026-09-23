package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StylePresets
import com.example.model.MovieProject
import com.example.model.ScreenTab
import com.example.model.StudioMode
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.BigRedButton
import com.example.ui.components.CountdownUrgencyBanner
import com.example.ui.components.PakistaniTestimonialsCard
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatEmeraldSubtle
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldAmber
import com.example.ui.theme.QismatGoldDark
import com.example.ui.theme.QismatGoldSubtle
import com.example.viewmodel.StoryViewModel

@Composable
fun HomeScreen(
  viewModel: StoryViewModel,
  modifier: Modifier = Modifier
) {
  val allProjects by viewModel.allProjects.collectAsState()
  val demoDialogProject by viewModel.demoDialogProject.collectAsState()
  val isKarachiMode by viewModel.isKarachiMode.collectAsState()
  val selectedLocation by viewModel.selectedLocation.collectAsState()
  val isProUser by viewModel.isProUser.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CinematicBg)
      .padding(horizontal = 16.dp)
      .testTag("home_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // 1. Urgency Banner
    item {
      Spacer(modifier = Modifier.height(4.dp))
      CountdownUrgencyBanner(
        onClick = { viewModel.openPaywallDialog() }
      )
    }

    // 2. Grand Hero Section: QISMAT AI
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
              listOf(QismatGold, QismatGoldDark, Color(0xFF332000))
            ),
            shape = RoundedCornerShape(22.dp)
          ),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface),
        shape = RoundedCornerShape(22.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Badges Row
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(QismatGoldSubtle)
                .border(1.dp, QismatGold, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Pakistan's #1 Viral AI",
                  color = QismatGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(QismatEmeraldSubtle)
                .border(1.dp, QismatEmerald, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "100% Free • No Key Needed",
                color = QismatEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Headline
          Text(
            text = "Socho Nahi, Apni Qismat Dekho",
            color = CinematicWhite,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "1 Photo Se Poori Zindagi Ka Trailer",
            color = QismatGold,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Time Machine se 5 Umrein dekho ya apni Dua ko Kamyabi film mein badlo. 100% desi Pakistani AI cinema.",
            color = CinematicTextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          BigRedButton(
            text = "Apni Qismat Dekho 🎬 Free",
            onClick = { viewModel.selectTab(ScreenTab.CREATE_STUDIO) },
            icon = {
              Text(text = "✨", fontSize = 16.sp)
            }
          )
        }
      }
    }

    // 3. Three World-First Modes Selector
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "🌟 World-First Modes (Choose One)",
          color = CinematicWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Time Machine
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(listOf(Color(0xFF2E1C00), Color(0xFF150D00)))
              )
              .border(1.dp, QismatGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
              .clickable {
                viewModel.setStudioMode(StudioMode.TIME_MACHINE)
                viewModel.selectTab(ScreenTab.CREATE_STUDIO)
              }
              .padding(12.dp)
          ) {
            Column {
              Text(text = "⏳ World First", color = QismatGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "Time Machine", color = CinematicWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "Bachpan se Budhapa (5 Ages)", color = CinematicTextMuted, fontSize = 10.sp)
            }
          }

          // Dua Se Film
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(listOf(Color(0xFF00220F), Color(0xFF001007)))
              )
              .border(1.dp, QismatEmerald.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
              .clickable {
                viewModel.setStudioMode(StudioMode.DUA_SE_FILM)
                viewModel.selectTab(ScreenTab.CREATE_STUDIO)
              }
              .padding(12.dp)
          ) {
            Column {
              Text(text = "🤲 Spiritual AI", color = QismatEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "Dua Se Film", color = CinematicWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "Kamyabi Ka Safar + Bismillah", color = CinematicTextMuted, fontSize = 10.sp)
            }
          }
        }
      }
    }

    // 4. Karachi Mode & Pakistani Cities Toggle
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, if (isKarachiMode) QismatGold.copy(alpha = 0.5f) else CinematicBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CinematicSurface)
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
              Column {
                Text(
                  text = "📍 Real Karachi Mode (USP)",
                  color = CinematicWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = "Sea View, Mazar-e-Quaid, Clifton, Saddar & Lyari",
                  color = CinematicTextMuted,
                  fontSize = 11.sp
                )
              }
            }

            Switch(
              checked = isKarachiMode,
              onCheckedChange = { viewModel.toggleKarachiMode(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = QismatGold,
                uncheckedThumbColor = CinematicTextDim,
                uncheckedTrackColor = CinematicSurfaceElevated
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // City Selector Pills
          Text(
            text = "Ya apna Pakistani shehar select karein:",
            color = CinematicTextMuted,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          val cities = listOf("Karachi", "Lahore", "Islamabad", "Dubai", "Village / Dehaat")
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cities) { city ->
              val isSelected = selectedLocation == city
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) QismatGold else CinematicSurfaceElevated)
                  .border(1.dp, if (isSelected) QismatGold else CinematicBorder, RoundedCornerShape(10.dp))
                  .clickable { viewModel.setSelectedLocation(city) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = city,
                  color = if (isSelected) Color.Black else CinematicWhite,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }
    }

    // 5. Interactive Before/After Slider
    item {
      BeforeAfterSlider()
    }

    // 6. Pakistani Testimonials Card
    item {
      PakistaniTestimonialsCard()
    }

    // 7. Featured Pakistani Demo Films
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalMovies,
              contentDescription = "Demo Films",
              tint = QismatGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Trending Qismat Trailers",
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }
          Text(
            text = "Tap to Preview",
            color = QismatGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(allProjects) { demo ->
            DemoMovieCard(
              project = demo,
              onClick = { viewModel.openDemoDialog(demo) }
            )
          }
        }
      }
    }

    // 8. Viral Invite Friend Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF00220F), Color(0xFF003819))
            )
          )
          .border(1.dp, QismatEmerald.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .clickable { viewModel.shareReferral() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(QismatEmerald),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Share, contentDescription = "Invite", tint = Color.Black, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Dost Ko Invite Karo 🎁",
                color = CinematicWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Har invite par 1 Free Qismat Film Credit pao!",
                color = QismatEmerald,
                fontSize = 11.sp
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(QismatEmerald)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(text = "Invite", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
          }
        }
      }
    }

    // 9. Footer: Made for Pakistan
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Pakistan's First Qismat AI 🌙",
          color = QismatGold,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Made with ❤️ for Pakistan • 100% Free • No API Key",
          color = CinematicTextMuted,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
      }
    }
  }

  // Demo Dialog Popup
  if (demoDialogProject != null) {
    val project = demoDialogProject!!
    AlertDialog(
      onDismissRequest = { viewModel.closeDemoDialog() },
      containerColor = CinematicSurfaceElevated,
      titleContentColor = CinematicWhite,
      textContentColor = CinematicTextMuted,
      shape = RoundedCornerShape(20.dp),
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = project.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = CinematicWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          IconButton(
            onClick = { viewModel.closeDemoDialog() },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = CinematicTextMuted
            )
          }
        }
      },
      text = {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.linearGradient(project.style.gradientHexes.map { Color(it) })
              )
              .border(1.dp, CinematicBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = project.style.iconEmoji,
                fontSize = 32.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Mode: ${project.mode.title}",
                color = QismatGold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "📍 ${project.location} • ${project.scenes.size} Scenes",
                color = CinematicWhite,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "\"${project.storyPrompt}\"",
            color = CinematicWhite,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.closeDemoDialog()
            viewModel.playMovie(project)
          },
          colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text("🎬 Play Trailer", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        Button(
          onClick = {
            viewModel.closeDemoDialog()
            viewModel.selectTab(ScreenTab.CREATE_STUDIO)
          },
          colors = ButtonDefaults.buttonColors(containerColor = CinematicSurface),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text("Apni Film Banao", color = CinematicWhite)
        }
      }
    )
  }
}

@Composable
fun DemoMovieCard(
  project: MovieProject,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(200.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("demo_card_${project.id}"),
    colors = CardDefaults.cardColors(containerColor = CinematicSurface),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .background(
            Brush.linearGradient(project.style.gradientHexes.map { Color(it) })
          ),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayCircleFilled,
            contentDescription = "Play",
            tint = QismatGold,
            modifier = Modifier.size(36.dp)
          )
        }

        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = project.mode.title,
            color = QismatGold,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "📍 ${project.location}",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = project.title,
          color = CinematicWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "${project.scenes.size} Scenes • 4K Ken Burns",
          color = CinematicTextMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}
