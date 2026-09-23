package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a saved user story draft or prayer (Dua) prompt.
 */
@Entity(tableName = "story_drafts")
data class StoryDraftEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val mode: String, // "TIME_MACHINE", "DUA_SE_FILM", "STORY_TO_FILM"
  val storyPrompt: String,
  val duaText: String = "",
  val location: String = "Karachi",
  val styleId: String = "qismat_gold",
  val durationMinutes: Int = 3,
  val language: String = "Urdu",
  val heroImageUri: String? = null,
  val isFaceLocked: Boolean = true,
  val updatedAt: Long = System.currentTimeMillis(),
  val createdAt: Long = System.currentTimeMillis()
)
