package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenTab
import com.example.ui.components.AppHeader
import com.example.ui.screens.CreateStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyFilmsScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.QismatGold
import com.example.viewmodel.StoryViewModel

class MainActivity : ComponentActivity() {

  private val storyViewModel: StoryViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        StoryFilmApp(viewModel = storyViewModel)
      }
    }
  }
}

@Composable
fun StoryFilmApp(viewModel: StoryViewModel) {
  val currentTab by viewModel.currentTab.collectAsState()
  val creditsRemaining by viewModel.creditsRemaining.collectAsState()
  val isProUser by viewModel.isProUser.collectAsState()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(CinematicBg),
    containerColor = CinematicBg,
    topBar = {
      AppHeader(
        creditsRemaining = creditsRemaining,
        isProUser = isProUser,
        onLogoClick = { viewModel.selectTab(ScreenTab.HOME) },
        onUpgradeClick = { viewModel.openPaywallDialog() }
      )
    },
    bottomBar = {
      CinematicBottomNavBar(
        currentTab = currentTab,
        onTabSelected = { tab -> viewModel.selectTab(tab) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
      ) { tab ->
        when (tab) {
          ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
          ScreenTab.CREATE_STUDIO -> CreateStudioScreen(viewModel = viewModel)
          ScreenTab.PLAYER -> PlayerScreen(viewModel = viewModel)
          ScreenTab.MY_FILMS -> MyFilmsScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun CinematicBottomNavBar(
  currentTab: ScreenTab,
  onTabSelected: (ScreenTab) -> Unit
) {
  NavigationBar(
    containerColor = CinematicSurface,
    tonalElevation = 8.dp,
    modifier = Modifier
      .navigationBarsPadding()
      .border(
        width = 1.dp,
        color = CinematicBorder,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
      )
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .testTag("cinematic_bottom_nav")
  ) {
    NavigationBarItem(
      selected = currentTab == ScreenTab.HOME,
      onClick = { onTabSelected(ScreenTab.HOME) },
      icon = {
        Icon(
          imageVector = Icons.Default.Home,
          contentDescription = "Home",
          modifier = Modifier.size(22.dp)
        )
      },
      label = {
        Text(
          text = "Home",
          fontSize = 11.sp,
          fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.Black,
        selectedTextColor = QismatGold,
        indicatorColor = QismatGold,
        unselectedIconColor = CinematicTextMuted,
        unselectedTextColor = CinematicTextDim
      ),
      modifier = Modifier.testTag("nav_tab_home")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.CREATE_STUDIO,
      onClick = { onTabSelected(ScreenTab.CREATE_STUDIO) },
      icon = {
        Icon(
          imageVector = Icons.Default.MovieCreation,
          contentDescription = "Create",
          modifier = Modifier.size(22.dp)
        )
      },
      label = {
        Text(
          text = "Create",
          fontSize = 11.sp,
          fontWeight = if (currentTab == ScreenTab.CREATE_STUDIO) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.Black,
        selectedTextColor = QismatGold,
        indicatorColor = QismatGold,
        unselectedIconColor = CinematicTextMuted,
        unselectedTextColor = CinematicTextDim
      ),
      modifier = Modifier.testTag("nav_tab_create")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.PLAYER,
      onClick = { onTabSelected(ScreenTab.PLAYER) },
      icon = {
        Icon(
          imageVector = Icons.Default.PlayCircleFilled,
          contentDescription = "Player",
          modifier = Modifier.size(22.dp)
        )
      },
      label = {
        Text(
          text = "Player",
          fontSize = 11.sp,
          fontWeight = if (currentTab == ScreenTab.PLAYER) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.Black,
        selectedTextColor = QismatGold,
        indicatorColor = QismatGold,
        unselectedIconColor = CinematicTextMuted,
        unselectedTextColor = CinematicTextDim
      ),
      modifier = Modifier.testTag("nav_tab_player")
    )

    NavigationBarItem(
      selected = currentTab == ScreenTab.MY_FILMS,
      onClick = { onTabSelected(ScreenTab.MY_FILMS) },
      icon = {
        Icon(
          imageVector = Icons.Default.VideoLibrary,
          contentDescription = "Vault",
          modifier = Modifier.size(22.dp)
        )
      },
      label = {
        Text(
          text = "Qismat Vault",
          fontSize = 10.sp,
          fontWeight = if (currentTab == ScreenTab.MY_FILMS) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.Black,
        selectedTextColor = QismatGold,
        indicatorColor = QismatGold,
        unselectedIconColor = CinematicTextMuted,
        unselectedTextColor = CinematicTextDim
      ),
      modifier = Modifier.testTag("nav_tab_my_films")
    )
  }
}
