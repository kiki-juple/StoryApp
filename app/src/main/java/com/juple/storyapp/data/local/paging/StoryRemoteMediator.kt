package com.juple.storyapp.data.local.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.juple.storyapp.data.local.database.RemoteKeys
import com.juple.storyapp.data.local.database.StoryDatabase
import com.juple.storyapp.data.local.database.StoryEntity
import com.juple.storyapp.data.remote.ApiService
import com.juple.storyapp.ui.LoginActivity
import com.juple.storyapp.ui.SplashActivity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeysClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                nextKey
            }
        }

        try {
            val token = SplashActivity.API_TOKEN.ifEmpty {
                LoginActivity.NEW_API_TOKEN
            }
            val responseData =
                apiService.getStories("Bearer $token", page, state.config.pageSize, 0)
            val endOfPaginationReached = responseData.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                for (story in responseData.listStory) {
                    val data = StoryEntity(
                        story.id,
                        story.name,
                        story.description,
                        story.photoUrl,
                        story.createdAt,
                        story.lat,
                        story.lon
                    )
                    database.storyDao().insertStory(data)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
            database.remoteKeysDao().getRemoteKeysId(story.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
            database.remoteKeysDao().getRemoteKeysId(story.id)
        }
    }

    private suspend fun getRemoteKeysClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}