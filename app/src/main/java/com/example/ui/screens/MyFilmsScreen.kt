package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MovieProject
import com.example.model.ScreenTab
import com.example.ui.components.BigRedButton
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicGreen
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldDark
import com.example.ui.theme.QismatGoldSubtle
import com.example.viewmodel.StoryViewModel

@Composable
fun MyFilmsScreen(
  viewModel: StoryViewModel,
  modifier: Modifier = Modifier
) {
  val allProjects by viewModel.allProjects.collectAsState()
  val isProUser by viewModel.isProUser.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CinematicBg)
      .padding(horizontal = 16.dp)
      .testTag("my_films_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "QISMAT",
              color = CinematicWhite,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
            Text(
              text = " VAULT 🎬",
              color = QismatGold,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          }
          Text(
            text = "${allProjects.size} Movies • Local Room Database 💾",
            color = QismatEmerald,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(QismatGold)
            .clickable { viewModel.selectTab(ScreenTab.CREATE_STUDIO) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "New Movie",
              tint = Color.Black,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "New Film",
              color = Color.Black,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }

    if (allProjects.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = CinematicSurface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.LocalMovies,
              contentDescription = "Empty",
              tint = CinematicTextDim,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Abhi koi film nahi bani",
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Create Studio mein jakar apni 1 photo se film banayein!",
              color = CinematicTextMuted,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            BigRedButton(
              text = "Apni Qismat Dekho 🎬 Free",
              onClick = { viewModel.selectTab(ScreenTab.CREATE_STUDIO) }
            )
          }
        }
      }
    } else {
      items(allProjects, key = { it.id }) { project ->
        FilmProjectVaultCard(
          project = project,
          isPro = isProUser || project.isPro,
          onPlayClick = { viewModel.playMovie(project) },
          onDownloadClick = { viewModel.downloadMovieMp4(project) },
          onDeleteClick = { viewModel.deleteProject(project.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun FilmProjectVaultCard(
  project: MovieProject,
  isPro: Boolean,
  onPlayClick: () -> Unit,
  onDownloadClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp))
      .clickable { onPlayClick() }
      .testTag("vault_card_${project.id}"),
    colors = CardDefaults.cardColors(containerColor = CinematicSurface),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column {
      // Banner Preview
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .background(
            Brush.linearGradient(project.style.gradientHexes.map { Color(it) })
          )
      ) {
        // Play button overlay
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.65f))
            .align(Alignment.Center)
            .clickable { onPlayClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = QismatGold,
            modifier = Modifier.size(30.dp)
          )
        }

        // Mode Tag
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "${project.mode.title} • 📍 ${project.location}",
            color = QismatGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Watermark Tag
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isPro) QismatGold else Color.Black.copy(alpha = 0.8f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isPro) "PRO 4K MASTER" else "Free Watermark",
            color = if (isPro) Color.Black else CinematicWhite.copy(alpha = 0.8f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Details Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = project.title,
            color = CinematicWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${project.durationMinutes} Min • ${project.scenes.size} Scenes",
              color = CinematicTextMuted,
              fontSize = 11.sp
            )
            Text(
              text = " • ${project.style.title}",
              color = CinematicCyan,
              fontSize = 11.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onDownloadClick,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Download MP4",
              tint = QismatGold,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Film",
              tint = CinematicTextDim,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
