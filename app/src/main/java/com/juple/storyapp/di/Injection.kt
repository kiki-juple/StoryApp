package com.juple.storyapp.di

import android.content.Context
import com.juple.storyapp.data.local.database.StoryDatabase
import com.juple.storyapp.data.local.paging.StoryRepository
import com.juple.storyapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}