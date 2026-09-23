package com.example

import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.model.ScreenTab
import com.example.viewmodel.StoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("QISMAT AI", appName)
  }

  @Test
  fun `story viewmodel expands scenes without api key`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    viewModel.setStoryText("Hero crosses the snowy mountain and awakens the crystal guardian.")
    viewModel.setDurationMinutes(3)
    viewModel.autoSplitStory()

    // Advance Robolectric main looper to complete coroutine delays
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

    val scenes = viewModel.generatedScenes.value
    assertTrue("Should generate at least 5 scenes for 3 min movie", scenes.size >= 5)
    assertEquals(3, viewModel.currentStep.value)
    assertNotNull(scenes.first().dialogue)
    assertTrue(scenes.first().visualSummary.contains("IDENTITY LOCK") || scenes.first().visualSummary.lowercase().contains("face"))
  }

  @Test
  fun `story viewmodel renders movie storyboard successfully`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    viewModel.setStoryText("A futuristic chase across neon city.")
    viewModel.autoSplitStory()
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

    viewModel.renderMovieStoryboard()
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(4))

    assertFalse(viewModel.isRenderingMovie.value)
    assertEquals(ScreenTab.PLAYER, viewModel.currentTab.value)
    assertNotNull(viewModel.activeProject.value)
    assertTrue(viewModel.isPlayerPlaying.value)
  }

  @Test
  fun `story viewmodel selects Pakistan city and injects authentic prompt`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    val lahore = com.example.data.PakistanCitiesData.findCityByName("Lahore")
    viewModel.selectCity(lahore)

    assertEquals("Lahore", viewModel.selectedLocation.value)
    assertEquals("Lahore", viewModel.selectedCityLocation.value.name)

    viewModel.autoSplitStory()
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

    val scenes = viewModel.generatedScenes.value
    assertTrue(scenes.isNotEmpty())
    // Verify city prompt injection with authentic Pakistani environment
    val firstPrompt = scenes.first().visualSummary
    assertTrue(firstPrompt.contains("Lahore"))
    assertTrue(firstPrompt.contains("Badshahi Mosque"))
    assertTrue(firstPrompt.contains("authentic Pakistani environment"))
  }

  @Test
  fun `story viewmodel updates storyboard scene dialogue and details`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    viewModel.autoSplitStory()
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

    val scenes = viewModel.generatedScenes.value
    assertTrue(scenes.isNotEmpty())
    val firstScene = scenes.first()

    viewModel.updateScene(
      sceneNumber = firstScene.sceneNumber,
      updatedText = "Updated scene visual description",
      updatedDialogue = "Zindagi ka naya mor",
      updatedCamera = "Drone Orbit 360"
    )

    val updatedFirst = viewModel.generatedScenes.value.first { it.sceneNumber == firstScene.sceneNumber }
    assertEquals("Zindagi ka naya mor", updatedFirst.dialogue)
    assertEquals("Drone Orbit 360", updatedFirst.camera)
    assertEquals("Updated scene visual description", updatedFirst.text)
  }

  @Test
  fun `story viewmodel handles hero photo capture and uri state for film transformation`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    val sampleCapturedUri = "file:///data/user/0/com.example/cache/hero_captured_test.jpg"
    viewModel.setPendingHeroImageUri(sampleCapturedUri)

    assertEquals(sampleCapturedUri, viewModel.pendingHeroImageUri.value)

    // Confirm hero image
    viewModel.setHeroImageUri(sampleCapturedUri)
    assertEquals(sampleCapturedUri, viewModel.heroImageUri.value)
    assertNull(viewModel.pendingHeroImageUri.value)
    assertTrue(viewModel.isFaceLocked.value)
  }

  @Test
  fun `room database stores and retrieves user story drafts`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(app)

    viewModel.setStoryText("Karachi Ke Pur-umeed Naujawan Ki Kamyabi Ka Safar")
    viewModel.setSelectedLocation("Karachi")

    var savedDraftId: Long = 0
    viewModel.saveCurrentStoryDraft { id ->
      savedDraftId = id
    }

    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))

    assertTrue(savedDraftId > 0)
    val drafts = viewModel.allDrafts.value
    assertTrue(drafts.any { it.id == savedDraftId })

    // Verify loading the draft back
    val savedDraft = drafts.first { it.id == savedDraftId }
    viewModel.setStoryText("Different text")
    viewModel.loadStoryDraft(savedDraft)
    assertEquals("Karachi Ke Pur-umeed Naujawan Ki Kamyabi Ka Safar", viewModel.storyText.value)

    // Delete draft
    viewModel.deleteStoryDraft(savedDraftId)
    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))
    assertFalse(viewModel.allDrafts.value.any { it.id == savedDraftId })
  }
}
