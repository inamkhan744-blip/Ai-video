package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDraftDao {
  @Query("SELECT * FROM story_drafts ORDER BY updatedAt DESC")
  fun getAllDrafts(): Flow<List<StoryDraftEntity>>

  @Query("SELECT * FROM story_drafts WHERE id = :id LIMIT 1")
  suspend fun getDraftById(id: Long): StoryDraftEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDraft(draft: StoryDraftEntity): Long

  @Update
  suspend fun updateDraft(draft: StoryDraftEntity)

  @Query("DELETE FROM story_drafts WHERE id = :id")
  suspend fun deleteDraftById(id: Long)

  @Query("DELETE FROM story_drafts")
  suspend fun deleteAllDrafts()
}
