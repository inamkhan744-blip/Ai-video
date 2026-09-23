package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkCinematicColorScheme = darkColorScheme(
  primary = CinematicRed,
  onPrimary = CinematicWhite,
  primaryContainer = CinematicRedDark,
  onPrimaryContainer = CinematicWhite,
  secondary = CinematicGold,
  onSecondary = CinematicBg,
  secondaryContainer = CinematicSurfaceElevated,
  onSecondaryContainer = CinematicGold,
  tertiary = CinematicCyan,
  onTertiary = CinematicBg,
  background = CinematicBg,
  onBackground = CinematicWhite,
  surface = CinematicSurface,
  onSurface = CinematicWhite,
  surfaceVariant = CinematicSurfaceElevated,
  onSurfaceVariant = CinematicTextMuted,
  outline = CinematicBorder,
  outlineVariant = CinematicBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkCinematicColorScheme,
    typography = Typography,
    content = content
  )
}

