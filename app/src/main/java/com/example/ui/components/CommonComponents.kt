package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CinematicStyle
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicCyan
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGreen
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicRedSubtle
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import com.example.ui.theme.QismatGoldAmber
import com.example.ui.theme.QismatGoldDark
import com.example.ui.theme.QismatGoldSubtle
import kotlinx.coroutines.delay

@Composable
fun AppHeader(
  onLogoClick: () -> Unit = {},
  isProUser: Boolean = false,
  creditsRemaining: Int = 1,
  onUpgradeClick: () -> Unit = {},
  freeCredits: Int = creditsRemaining,
  onProClick: () -> Unit = onUpgradeClick
) {
  Surface(
    color = CinematicBg,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("app_header")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onLogoClick() }
      ) {
        // Q Logo with Crescent & Film Reel Accent
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.linearGradient(listOf(QismatGold, QismatGoldDark))
            ),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "Q",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 24.sp
            )
            Text(
              text = "🌙",
              fontSize = 11.sp,
              modifier = Modifier.padding(bottom = 8.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "QISMAT",
              color = CinematicWhite,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = " AI",
              color = QismatGold,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  if (isProUser) Brush.horizontalGradient(listOf(QismatGold, QismatGoldDark))
                  else Brush.horizontalGradient(listOf(Color(0xFF2A2A2A), Color(0xFF1E1E1E)))
                )
                .clickable { onProClick() }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (isProUser) "PRO ⭐" else "FREE",
                color = if (isProUser) Color.Black else QismatGold,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }
          }
          Text(
            text = "Apni Zindagi Ka Trailer 🎬",
            color = CinematicTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Credit Counter / Pro Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(if (isProUser) QismatGoldSubtle else QismatEmerald.copy(alpha = 0.15f))
          .border(
            1.dp,
            if (isProUser) QismatGold else QismatEmerald.copy(alpha = 0.6f),
            RoundedCornerShape(20.dp)
          )
          .clickable { onProClick() }
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isProUser) Icons.Default.Star else Icons.Default.Bolt,
            contentDescription = "Credits",
            tint = if (isProUser) QismatGold else QismatEmerald,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isProUser) "PRO VIP" else "$freeCredits Free Film",
            color = if (isProUser) QismatGold else QismatEmerald,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Urgency Countdown Banner: "Aaj Free - Kal se Rs 1000"
 */
@Composable
fun CountdownUrgencyBanner(
  onClick: () -> Unit = {}
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xFF332000), Color(0xFF553300), Color(0xFF221100))
        )
      )
      .border(1.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "⚡", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = "Aaj Free • Kal Se Rs 1000 Lifetime",
            color = QismatGold,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "Special Pakistan Launch Offer: 1 Photo se poori film",
            color = CinematicWhite.copy(alpha = 0.85f),
            fontSize = 10.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(QismatGold)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "04:12:35",
          color = Color.Black,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

/**
 * Before / After Interactive Split-Slider (Normal Photo vs Cinematic Pakistani Hero)
 */
@Composable
fun BeforeAfterSlider(
  modifier: Modifier = Modifier
) {
  var sliderPosition by remember { mutableFloatStateOf(0.5f) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.5.dp, QismatGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = CinematicSurface)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Transformation",
            tint = QismatGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "1 Photo Se Hero Bano (Before vs After)",
            color = CinematicWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
        Text(
          text = "Drag ↔",
          color = QismatGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(12.dp))
          .pointerInput(Unit) {
            detectDragGestures { change, _ ->
              change.consume()
              val newPos = change.position.x / size.width
              sliderPosition = newPos.coerceIn(0.1f, 0.9f)
            }
          }
      ) {
        val totalWidth = maxWidth

        // Right / Background: "AFTER" (Cinematic Hero in Karachi / 4K Film)
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF2E1C00), Color(0xFF6B4300), Color(0xFF1F1100))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 24.dp)
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(2.5.dp, QismatGold, CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "👑", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "AFTER: 4K Film Hero",
              color = QismatGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "Face Locked • Karachi Sea View",
              color = CinematicWhite.copy(alpha = 0.8f),
              fontSize = 10.sp
            )
          }
        }

        // Left / Foreground: "BEFORE" (Normal Selfie) clipped by sliderPosition
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .width(totalWidth * sliderPosition)
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF1E1E1E), Color(0xFF2B2B2B))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 24.dp)
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(2.dp, CinematicTextMuted, CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🤳", fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "BEFORE: Normal Photo",
              color = CinematicWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Ghar Ki Sadah Selfie",
              color = CinematicTextMuted,
              fontSize = 10.sp
            )
          }
        }

        // Divider Line & Handle
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .width(2.5.dp)
            .background(QismatGold)
            .align(Alignment.CenterStart)
            .graphicsLayer {
              translationX = (sliderPosition * totalWidth.toPx()) - 1.25f
            }
        )

        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(QismatGold)
            .border(2.dp, Color.Black, CircleShape)
            .align(Alignment.CenterStart)
            .graphicsLayer {
              translationX = (sliderPosition * totalWidth.toPx()) - 17f
            },
          contentAlignment = Alignment.Center
        ) {
          Text(text = "↔", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Authentic Pakistani Testimonials Card
 */
@Composable
fun PakistaniTestimonialsCard(
  modifier: Modifier = Modifier
) {
  val testimonials = listOf(
    Pair("Ahmed (Karachi)", "Meri Qismat badal gayi! Apne bachpan se budhapa dekh kar meri ammi ro padin. 100% viral app!"),
    Pair("Zainab (Lahore)", "Mene Dua likhi thi ke mera boutique kamyab ho, AI ne itna emotional trailer banaya ke goosebumps aa gaye!"),
    Pair("Bilal (Rawalpindi)", "Karachi mode aur Face Lock bilkul real hai. Sea View wala scene mind blowing tha!")
  )

  var activeIndex by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(4000)
      activeIndex = (activeIndex + 1) % testimonials.size
    }
  }

  val active = testimonials[activeIndex]

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, CinematicBorder, RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(containerColor = CinematicSurface)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "⭐ ⭐ ⭐ ⭐ ⭐", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Pakistani Users Ka Pyar",
            color = CinematicWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
        Text(
          text = "${activeIndex + 1}/${testimonials.size}",
          color = CinematicTextMuted,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "\"${active.second}\"",
        color = CinematicWhite,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(QismatGold)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = active.first,
          color = QismatGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Bismillah Holy Header for Dua Se Film
 */
@Composable
fun BismillahHeader(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xFF00220F), Color(0xFF003819), Color(0xFF00220F))
        )
      )
      .border(1.dp, QismatEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
      .padding(vertical = 10.dp, horizontal = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
        color = QismatEmerald,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Spiritual AI • Dua Se Kamyabi Ka Safar",
        color = CinematicWhite.copy(alpha = 0.8f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

/**
 * Big Golden Qismat Action Button
 */
@Composable
fun BigRedButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .fillMaxWidth()
      .height(54.dp)
      .testTag("big_red_button"),
    shape = RoundedCornerShape(24.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = QismatGold,
      contentColor = Color.Black,
      disabledContainerColor = Color(0xFF38290B),
      disabledContentColor = CinematicTextDim
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.3.sp
      )
    }
  }
}

/**
 * Pro Paywall Dialog (Rs 500 Lifetime Unlock)
 */
@Composable
fun PaywallProDialog(
  onDismiss: () -> Unit,
  onUnlockPro: () -> Unit,
  onInviteFriend: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onUnlockPro,
        colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text(
          text = "Pro Bano - Rs 500 Lifetime ⚡",
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
      }
    },
    dismissButton = {
      Button(
        onClick = onInviteFriend,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222), contentColor = QismatEmerald),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(44.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Dost Ko Invite Karo (Free Film Pao) 🎁",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    },
    title = {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "👑 QISMAT PRO UNLOCK", color = QismatGold, fontWeight = FontWeight.Black, fontSize = 17.sp)
        Text(text = "Qismat khul gayi! Agli film dekhne ke liye Pro bano", color = CinematicWhite, fontSize = 12.sp, textAlign = TextAlign.Center)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ProFeatureItem("✅ Unlimited 4K Qismat Films (No Limits)")
        ProFeatureItem("✅ No Watermark (Clean 100% Export)")
        ProFeatureItem("✅ All Karachi Locations Unlocked")
        ProFeatureItem("✅ Time Machine & Dua Se Film Unlimited")
        ProFeatureItem("✅ EasyPaisa / JazzCash / Card Supported")
      }
    },
    containerColor = CinematicSurfaceElevated,
    shape = RoundedCornerShape(20.dp)
  )
}

@Composable
private fun ProFeatureItem(text: String) {
  Text(text = text, color = CinematicWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
}

@Composable
fun CinematicStyleCard(
  style: CinematicStyle,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) QismatGold else CinematicBorder,
    label = "borderColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() }
      .testTag("style_card_${style.id}"),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) Color(0xFF1E1708) else CinematicSurface
    ),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  style.gradientHexes.map { Color(it) }
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = style.iconEmoji,
              fontSize = 18.sp
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = style.title,
              color = CinematicWhite,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = style.genreBadge,
              color = if (isSelected) QismatGold else CinematicTextMuted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        if (isSelected) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Selected",
            tint = QismatGold,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Box(
            modifier = Modifier
              .size(20.dp)
              .border(1.5.dp, CinematicTextDim, CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = style.description,
        color = CinematicTextMuted,
        fontSize = 13.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "🎨 ${style.colorTone}",
            color = CinematicCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "🎥 ${style.cameraLens}",
            color = CinematicWhite,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun DashedUploadBox(
  imageUri: String?,
  isFaceLocked: Boolean,
  onUploadClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strokeColor = if (imageUri != null) QismatEmerald else QismatGold

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(160.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(CinematicSurface)
      .drawBehind {
        val strokeWidth = 2.dp.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
        drawRoundRect(
          color = strokeColor,
          style = Stroke(width = strokeWidth, pathEffect = pathEffect),
          cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
        )
      }
      .clickable { onUploadClick() }
      .testTag("dashed_upload_box"),
    contentAlignment = Alignment.Center
  ) {
    if (imageUri != null) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(2.5.dp, QismatEmerald, CircleShape)
        ) {
          AsyncImage(
            model = imageUri,
            contentDescription = "Hero Face",
            modifier = Modifier.fillMaxSize()
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Face Locked",
            tint = QismatEmerald,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "100% Face Lock Active ✅",
            color = QismatEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
        Text(
          text = "Tap karke doosri photo select karein",
          color = CinematicTextMuted,
          fontSize = 11.sp
        )
      }
    } else {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(QismatGoldSubtle),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Upload Face",
            tint = QismatGold,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Apni 1 Saaf Photo Upload Karo (Front Face)",
          color = CinematicWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "AI 1 photo se poori zindagi ka trailer banayega",
          color = QismatGold,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

/**
 * Cinematic Film Frame with Ken Burns Pan/Zoom Motion & Watermark Logic
 */
@Composable
fun CinematicFilmFrame(
  style: CinematicStyle,
  sceneNumber: Int,
  cameraShot: String,
  heroImageUri: String?,
  isFaceLocked: Boolean,
  modifier: Modifier = Modifier,
  isKenBurnsActive: Boolean = false,
  ageStage: String = "",
  locationName: String = "",
  isPro: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
  val scale by infiniteTransition.animateFloat(
    initialValue = if (isKenBurnsActive) 1.0f else 1.0f,
    targetValue = if (isKenBurnsActive) 1.15f else 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ken_burns_scale"
  )
  val panX by infiniteTransition.animateFloat(
    initialValue = if (isKenBurnsActive) -10f else 0f,
    targetValue = if (isKenBurnsActive) 10f else 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ken_burns_pan"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, CinematicBorder, RoundedCornerShape(12.dp))
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
          translationX = panX
        }
        .background(
          Brush.linearGradient(
            colors = style.gradientHexes.map { Color(it) }
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
          translationX = panX * 0.5f
        }
        .padding(top = 18.dp, bottom = 18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (heroImageUri != null) {
        Box(
          modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .border(2.5.dp, QismatGold, CircleShape)
        ) {
          AsyncImage(
            model = heroImageUri,
            contentDescription = "Hero",
            modifier = Modifier.fillMaxSize()
          )
        }
      } else {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .border(2.dp, QismatGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🎬", fontSize = 26.sp)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (ageStage.isNotBlank()) ageStage.uppercase() else style.title.uppercase(),
        color = QismatGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )
      if (locationName.isNotBlank()) {
        Text(
          text = "📍 $locationName",
          color = Color.White.copy(alpha = 0.9f),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Top Letterbox Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .background(Color.Black.copy(alpha = 0.92f))
        .align(Alignment.TopCenter)
    )

    // Bottom Letterbox Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .background(Color.Black.copy(alpha = 0.92f))
        .align(Alignment.BottomCenter)
    )

    // Top overlay labels
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp)
        .align(Alignment.TopCenter),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "SCENE #$sceneNumber",
          color = QismatGold,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        if (ageStage.isNotBlank()) {
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• $ageStage",
            color = CinematicCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Watermark if not pro
      if (!isPro) {
        Text(
          text = "QISMAT AI - Free Version",
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium
        )
      } else {
        Text(
          text = "PRO 4K MASTER",
          color = QismatGold,
          fontSize = 9.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    // Bottom overlay labels
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp)
        .align(Alignment.BottomCenter),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = cameraShot,
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 10.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      if (isFaceLocked) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Face Locked",
            tint = QismatEmerald,
            modifier = Modifier.size(10.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "FACE LOCKED",
            color = QismatEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Typewriter Effect for Movie Subtitles and Dialogues
 */
@Composable
fun TypewriterDialogueText(
  fullText: String,
  modifier: Modifier = Modifier,
  charDelayMs: Long = 24L
) {
  var displayedChars by remember(fullText) { mutableIntStateOf(0) }

  LaunchedEffect(fullText) {
    displayedChars = 0
    for (i in 1..fullText.length) {
      displayedChars = i
      delay(charDelayMs)
    }
  }

  val visibleText = fullText.take(displayedChars)

  Text(
    text = visibleText,
    color = CinematicWhite,
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 18.sp,
    modifier = modifier
  )
}
