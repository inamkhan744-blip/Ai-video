package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a stored Movie Project & Storyboard metadata record.
 * Stores film configuration, scene count, and JSON-serialized scenes list.
 */
@Entity(tableName = "movie_projects")
data class MovieProjectEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val heroName: String = "Hero",
  val heroImageUri: String? = null,
  val isFaceLocked: Boolean = true,
  val styleId: String,
  val styleTitle: String,
  val durationMinutes: Int,
  val language: String,
  val storyPrompt: String,
  val mode: String, // "TIME_MACHINE", "DUA_SE_FILM", "STORY_TO_FILM"
  val location: String = "Karachi",
  val isKarachiMode: Boolean = true,
  val isPro: Boolean = false,
  val isRendered: Boolean = true,
  val scenesJson: String, // Serialized list of FilmScene
  val createdAt: Long = System.currentTimeMillis()
)
