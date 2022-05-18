package com.juple.storyapp.data.local.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.juple.storyapp.data.local.database.StoryDatabase
import com.juple.storyapp.data.remote.ApiService
import com.juple.storyapp.data.remote.User

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(): LiveData<PagingData<User>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).liveData
    }
}