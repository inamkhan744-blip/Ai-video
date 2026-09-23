package com.example.data.local

import com.example.data.DemoFilms
import com.example.data.StylePresets
import com.example.model.FilmScene
import com.example.model.MovieProject
import com.example.model.StudioMode
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * StoryRepository:
 * Single source of truth abstracting local Room database persistence for:
 * - Generated storyboard scenes & movie project metadata
 * - User-created story drafts & prayers (Dua)
 */
class StoryRepository(
  private val movieProjectDao: MovieProjectDao,
  private val storyDraftDao: StoryDraftDao
) {
  private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  private val scenesListType = Types.newParameterizedType(List::class.java, FilmScene::class.java)
  private val scenesAdapter = moshi.adapter<List<FilmScene>>(scenesListType)

  // Reactive Flow of all movie projects
  val allProjects: Flow<List<MovieProject>> = movieProjectDao.getAllProjects().map { entities ->
    entities.map { entityToProject(it) }
  }

  // Reactive Flow of all user story drafts
  val allDrafts: Flow<List<StoryDraftEntity>> = storyDraftDao.getAllDrafts()

  suspend fun getProjectById(id: String): MovieProject? = withContext(Dispatchers.IO) {
    movieProjectDao.getProjectById(id)?.let { entityToProject(it) }
  }

  suspend fun saveProject(project: MovieProject) = withContext(Dispatchers.IO) {
    val entity = projectToEntity(project)
    movieProjectDao.insertProject(entity)
  }

  suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
    movieProjectDao.deleteProjectById(id)
  }

  suspend fun saveDraft(draft: StoryDraftEntity): Long = withContext(Dispatchers.IO) {
    storyDraftDao.insertDraft(draft)
  }

  suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
    storyDraftDao.deleteDraftById(id)
  }

  suspend fun getDraftById(id: Long): StoryDraftEntity? = withContext(Dispatchers.IO) {
    storyDraftDao.getDraftById(id)
  }

  /**
   * Pre-populates the database with demo film projects if empty.
   */
  suspend fun populateDemoProjectsIfEmpty() = withContext(Dispatchers.IO) {
    val count = movieProjectDao.getProjectCount()
    if (count == 0) {
      val entities = DemoFilms.sampleProjects.map { projectToEntity(it) }
      movieProjectDao.insertProjects(entities)
    }
  }

  // Conversion Helpers
  private fun projectToEntity(project: MovieProject): MovieProjectEntity {
    val scenesJson = scenesAdapter.toJson(project.scenes)
    return MovieProjectEntity(
      id = project.id,
      title = project.title,
      heroName = project.heroName,
      heroImageUri = project.heroImageUri,
      isFaceLocked = project.isFaceLocked,
      styleId = project.style.id,
      styleTitle = project.style.title,
      durationMinutes = project.durationMinutes,
      language = project.language,
      storyPrompt = project.storyPrompt,
      mode = project.mode.name,
      location = project.location,
      isKarachiMode = project.isKarachiMode,
      isPro = project.isPro,
      isRendered = project.isRendered,
      scenesJson = scenesJson,
      createdAt = project.createdAt
    )
  }

  private fun entityToProject(entity: MovieProjectEntity): MovieProject {
    val style = StylePresets.allStyles.find { it.id == entity.styleId }
      ?: StylePresets.defaultStyle

    val mode = try {
      StudioMode.valueOf(entity.mode)
    } catch (e: Exception) {
      StudioMode.TIME_MACHINE
    }

    val scenes = try {
      scenesAdapter.fromJson(entity.scenesJson) ?: emptyList()
    } catch (e: Exception) {
      emptyList()
    }

    return MovieProject(
      id = entity.id,
      title = entity.title,
      heroName = entity.heroName,
      heroImageUri = entity.heroImageUri,
      isFaceLocked = entity.isFaceLocked,
      style = style,
      durationMinutes = entity.durationMinutes,
      language = entity.language,
      storyPrompt = entity.storyPrompt,
      scenes = scenes,
      isRendered = entity.isRendered,
      mode = mode,
      location = entity.location,
      isKarachiMode = entity.isKarachiMode,
      isPro = entity.isPro,
      createdAt = entity.createdAt
    )
  }
}
