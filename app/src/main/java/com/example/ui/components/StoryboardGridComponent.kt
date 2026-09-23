package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.model.CinematicStyle
import com.example.model.FilmScene
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldSubtle

/**
 * Creates an animated linear gradient shimmer brush for loading skeleton states.
 */
@Composable
fun rememberShimmerBrush(
  targetValue: Float = 1000f,
  durationMillis: Int = 1200
): Brush {
  val shimmerColors = listOf(
    Color(0xFF1E1E28),
    Color(0xFF38384A),
    Color(0xFFFFD700).copy(alpha = 0.28f),
    Color(0xFF38384A),
    Color(0xFF1E1E28)
  )

  val transition = rememberInfiniteTransition(label = "shimmer_transition")
  val translateAnimation by transition.animateFloat(
    initialValue = 0f,
    targetValue = targetValue,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_translate"
  )

  return Brush.linearGradient(
    colors = shimmerColors,
    start = Offset.Zero,
    end = Offset(x = translateAnimation, y = translateAnimation)
  )
}

/**
 * StoryboardGridComponent:
 * Displays generated film scenes as storyboards in a customizable grid layout using Coil.
 * Features animated shimmer and circular progress indicator overlays during image loading/processing.
 */
@Composable
fun StoryboardGrid(
  scenes: List<FilmScene>,
  style: CinematicStyle,
  heroImageUri: String?,
  isFaceLocked: Boolean,
  modifier: Modifier = Modifier,
  isLoading: Boolean = false,
  loadingMessage: String = "AI Storyboard frames generate ho rahe hain...",
  isPro: Boolean = false,
  selectedSceneNumber: Int? = null,
  onSceneClick: (FilmScene) -> Unit = {},
  onEditClick: ((FilmScene) -> Unit)? = null,
  onRegenerateDialogue: ((FilmScene) -> Unit)? = null
) {
  var gridColumns by remember { mutableIntStateOf(2) } // 2 columns by default, can toggle
  var inspectingScene by remember { mutableStateOf<FilmScene?>(null) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .testTag("storyboard_grid_container")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Grid Header Controls: Title & Density Switcher
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Grid",
            tint = QismatGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Storyboard Grid (${scenes.size} Shots)",
            color = CinematicWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(QismatGoldSubtle)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (isLoading) "PROCESSING..." else "COIL AI PREVIEW",
              color = QismatGold,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        // Column count switch: 2 columns vs 1 column
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicSurface)
            .border(1.dp, CinematicBorder, RoundedCornerShape(8.dp))
            .padding(2.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (gridColumns == 2) QismatGold else Color.Transparent)
              .clickable { gridColumns = 2 }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("grid_toggle_2_col")
          ) {
            Text(
              text = "2x2 ⊞",
              color = if (gridColumns == 2) Color.Black else CinematicTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (gridColumns == 1) QismatGold else Color.Transparent)
              .clickable { gridColumns = 1 }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("grid_toggle_1_col")
          ) {
            Text(
              text = "1x1 ☰",
              color = if (gridColumns == 1) Color.Black else CinematicTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Chunked Grid Rows
      val chunkedScenes = scenes.chunked(gridColumns)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        chunkedScenes.forEach { rowScenes ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            rowScenes.forEach { scene ->
              Box(
                modifier = Modifier.weight(1f)
              ) {
                StoryboardGridCell(
                  scene = scene,
                  style = style,
                  heroImageUri = heroImageUri,
                  isFaceLocked = isFaceLocked,
                  isPro = isPro,
                  isSelected = selectedSceneNumber == scene.sceneNumber,
                  onCellClick = {
                    onSceneClick(scene)
                    inspectingScene = scene
                  },
                  onEditClick = if (onEditClick != null) { { onEditClick(scene) } } else null,
                  onRegenerateDialogue = if (onRegenerateDialogue != null) { { onRegenerateDialogue(scene) } } else null
                )
              }
            }
            // Fill remaining slots in last row if uneven
            if (rowScenes.size < gridColumns) {
              val emptySlots = gridColumns - rowScenes.size
              for (i in 0 until emptySlots) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    // Grid-Level Shimmer & Circular Loading Indicator Overlay
    if (isLoading) {
      val shimmerBrush = rememberShimmerBrush()
      Box(
        modifier = Modifier
          .matchParentSize()
          .clip(RoundedCornerShape(14.dp))
          .background(Color.Black.copy(alpha = 0.65f))
          .border(1.5.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
          .testTag("storyboard_grid_loading_overlay"),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(20.dp)
        ) {
          // Circular Loading Indicator with cinematic Gold glow
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(CinematicSurfaceElevated)
              .border(2.dp, QismatGoldSubtle, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              color = QismatGold,
              strokeWidth = 3.5.dp,
              modifier = Modifier.size(42.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Shimmering Banner Bar
          Box(
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .height(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(shimmerBrush),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = QismatGold,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = loadingMessage,
                color = CinematicWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }
  }

  // Fullscreen Storyboard Inspector Dialog
  inspectingScene?.let { scene ->
    StoryboardInspectorDialog(
      scene = scene,
      allScenes = scenes,
      style = style,
      heroImageUri = heroImageUri,
      isFaceLocked = isFaceLocked,
      isPro = isPro,
      onDismiss = { inspectingScene = null },
      onNavigateToScene = { inspectingScene = it },
      onEditClick = {
        inspectingScene = null
        onEditClick?.invoke(scene)
      }
    )
  }
}

/**
 * Individual Storyboard Grid Item with Coil Image Loader and Shimmer/Circular Indicator
 */
@Composable
fun StoryboardGridCell(
  scene: FilmScene,
  style: CinematicStyle,
  heroImageUri: String?,
  isFaceLocked: Boolean,
  isSelected: Boolean,
  onCellClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPro: Boolean = false,
  onEditClick: (() -> Unit)? = null,
  onRegenerateDialogue: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) QismatGold else CinematicBorder,
    label = "cell_border"
  )
  val cellShimmerBrush = rememberShimmerBrush()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable { onCellClick() }
      .testTag("storyboard_cell_${scene.sceneNumber}"),
    colors = CardDefaults.cardColors(containerColor = CinematicSurface)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top 16:9 Frame with Coil Image & Shimmer/Circular Loading Overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
          .background(Color.Black)
      ) {
        // COIL ASYNC IMAGE LOADER with shimmer placeholder & circular progress
        val targetImageModel = scene.imageUrl ?: heroImageUri
        if (targetImageModel != null) {
          SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
              .data(targetImageModel)
              .crossfade(true)
              .build(),
            contentDescription = "Storyboard Scene ${scene.sceneNumber}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
              // Shimmer background + centered circular progress indicator overlay
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(cellShimmerBrush)
                  .testTag("storyboard_cell_loading_${scene.sceneNumber}"),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                  contentAlignment = Alignment.Center
                ) {
                  CircularProgressIndicator(
                    color = QismatGold,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }
            },
            error = {
              // Stylized Fallback with Style gradient & scene icon
              StoryboardFallbackVisual(
                style = style,
                heroImageUri = heroImageUri,
                scene = scene
              )
            }
          )
        } else {
          // Dynamic procedural storyboard artwork when no user photo uploaded
          StoryboardFallbackVisual(
            style = style,
            heroImageUri = null,
            scene = scene
          )
        }

        // Top Cinema Letterbox Strip
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(Color.Black.copy(alpha = 0.88f))
            .align(Alignment.TopCenter)
        )

        // Bottom Cinema Letterbox Strip
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .background(Color.Black.copy(alpha = 0.88f))
            .align(Alignment.BottomCenter)
        )

        // Top Overlay: Scene Number + Timecode
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(QismatGold)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "#${scene.sceneNumber}",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            )
          }

          Text(
            text = scene.timeCode,
            color = CinematicWhite.copy(alpha = 0.9f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Bottom Overlay: Camera Shot & Face Lock
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .align(Alignment.BottomCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = scene.camera,
            color = CinematicCyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          if (isFaceLocked) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = QismatEmerald,
                modifier = Modifier.size(8.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "LOCKED",
                color = QismatEmerald,
                fontSize = 7.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        // Inspect Zoom Button
        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ZoomIn,
            contentDescription = "Inspect",
            tint = CinematicWhite,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Card Content Section: Age / Location & Dialogue
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        // Tag row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (scene.ageStage.isNotBlank()) scene.ageStage else scene.locationName.ifBlank { style.title },
            color = QismatGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Text(
            text = scene.characterMood,
            color = CinematicTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dialogue Preview
        Text(
          text = scene.dialogue,
          color = CinematicWhite,
          fontSize = 11.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Cell Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (onRegenerateDialogue != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CinematicSurfaceElevated)
                .clickable { onRegenerateDialogue() }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = QismatGold,
                modifier = Modifier.size(12.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }

          if (onEditClick != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(QismatGold.copy(alpha = 0.15f))
                .clickable { onEditClick() }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = QismatGold,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Storyboard fallback visual when direct rendering is needed or loading fails
 */
@Composable
private fun StoryboardFallbackVisual(
  style: CinematicStyle,
  heroImageUri: String?,
  scene: FilmScene
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.linearGradient(
          colors = style.gradientHexes.map { Color(it) }
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (heroImageUri != null) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(2.dp, QismatGold, CircleShape)
        ) {
          AsyncImage(
            model = heroImageUri,
            contentDescription = "Hero Face",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }
      } else {
        Text(text = "🎬", fontSize = 20.sp)
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = if (scene.ageStage.isNotBlank()) scene.ageStage.take(14) else style.title,
        color = QismatGold,
        fontSize = 9.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

/**
 * Storyboard Fullscreen / Detailed Inspector Modal
 */
@Composable
fun StoryboardInspectorDialog(
  scene: FilmScene,
  allScenes: List<FilmScene>,
  style: CinematicStyle,
  heroImageUri: String?,
  isFaceLocked: Boolean,
  isPro: Boolean,
  onDismiss: () -> Unit,
  onNavigateToScene: (FilmScene) -> Unit,
  onEditClick: () -> Unit
) {
  val context = LocalContext.current
  val currentIndex = allScenes.indexOfFirst { it.sceneNumber == scene.sceneNumber }
  val hasPrevious = currentIndex > 0
  val hasNext = currentIndex in 0 until (allScenes.size - 1)

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CinematicBg,
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier.border(1.5.dp, QismatGold, RoundedCornerShape(20.dp)),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(QismatGold)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "SCENE #${scene.sceneNumber}",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Storyboard Master",
            color = CinematicWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        IconButton(
          onClick = onDismiss,
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
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // High-Res Coil Storyboard Image Display with Shimmer & Progress Indicator
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CinematicBorder, RoundedCornerShape(12.dp))
            .background(Color.Black)
        ) {
          val targetImage = scene.imageUrl ?: heroImageUri
          val inspectorShimmerBrush = rememberShimmerBrush()
          if (targetImage != null) {
            SubcomposeAsyncImage(
              model = ImageRequest.Builder(context)
                .data(targetImage)
                .crossfade(true)
                .build(),
              contentDescription = "Scene Detail",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
              loading = {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(inspectorShimmerBrush),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(48.dp)
                      .clip(CircleShape)
                      .background(Color.Black.copy(alpha = 0.65f))
                      .border(1.5.dp, QismatGoldSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    CircularProgressIndicator(
                      color = QismatGold,
                      strokeWidth = 3.dp,
                      modifier = Modifier.size(28.dp)
                    )
                  }
                }
              },
              error = {
                StoryboardFallbackVisual(style = style, heroImageUri = null, scene = scene)
              }
            )
          } else {
            StoryboardFallbackVisual(style = style, heroImageUri = null, scene = scene)
          }

          // Watermark overlay
          Text(
            text = if (isPro) "PRO 4K RESOLUTION" else "QISMAT AI STORYBOARD",
            color = if (isPro) QismatGold else Color.White.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
          )

          // Face Lock indicator
          if (isFaceLocked) {
            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = QismatEmerald,
                modifier = Modifier.size(10.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Hero Face Locked",
                color = QismatEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Details breakdown
        Card(
          colors = CardDefaults.cardColors(containerColor = CinematicSurfaceElevated),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "Dialogue / Voiceover:",
              color = QismatGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = scene.dialogue,
              color = CinematicWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "🎥 ${scene.camera}",
                color = CinematicCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "💡 ${scene.lighting}",
                color = CinematicTextMuted,
                fontSize = 10.sp
              )
            }
          }
        }

        // Sequential Navigation Bar: Previous Scene / Next Scene
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              if (hasPrevious) onNavigateToScene(allScenes[currentIndex - 1])
            },
            enabled = hasPrevious,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinematicWhite),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Previous", fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          OutlinedButton(
            onClick = {
              if (hasNext) onNavigateToScene(allScenes[currentIndex + 1])
            },
            enabled = hasNext,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinematicWhite),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Next", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next",
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onEditClick,
        colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Edit Scene", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CinematicTextMuted),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Close", fontSize = 12.sp)
      }
    }
  )
}
