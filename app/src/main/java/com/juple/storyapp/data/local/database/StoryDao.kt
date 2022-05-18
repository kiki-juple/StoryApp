package com.juple.storyapp.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juple.storyapp.data.remote.User

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<User>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, User>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}