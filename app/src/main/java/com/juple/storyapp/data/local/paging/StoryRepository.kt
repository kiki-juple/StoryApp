package com.juple.storyapp.data.local.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.juple.storyapp.data.local.database.StoryDatabase
import com.juple.storyapp.data.local.database.StoryEntity
import com.juple.storyapp.data.remote.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).liveData
    }
}