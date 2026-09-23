package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieProjectDao {
  @Query("SELECT * FROM movie_projects ORDER BY createdAt DESC")
  fun getAllProjects(): Flow<List<MovieProjectEntity>>

  @Query("SELECT * FROM movie_projects WHERE id = :id LIMIT 1")
  suspend fun getProjectById(id: String): MovieProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: MovieProjectEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjects(projects: List<MovieProjectEntity>)

  @Query("DELETE FROM movie_projects WHERE id = :id")
  suspend fun deleteProjectById(id: String)

  @Query("DELETE FROM movie_projects")
  suspend fun deleteAllProjects()

  @Query("SELECT COUNT(*) FROM movie_projects")
  suspend fun getProjectCount(): Int
}
