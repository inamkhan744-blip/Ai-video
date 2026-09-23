package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase:
 * Local Room Database storing:
 * 1. Generated storyboard metadata and film projects (`movie_projects`)
 * 2. User-created story drafts and spiritual prayers (`story_drafts`)
 */
@Database(
  entities = [
    MovieProjectEntity::class,
    StoryDraftEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun movieProjectDao(): MovieProjectDao
  abstract fun storyDraftDao(): StoryDraftDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "qismat_database.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
