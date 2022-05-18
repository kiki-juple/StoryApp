package com.juple.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.juple.storyapp.data.local.paging.StoryRepository
import com.juple.storyapp.data.local.preferences.UserPreference
import com.juple.storyapp.data.remote.User
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, storyRepository: StoryRepository) :
    ViewModel() {

    val listStory: LiveData<PagingData<User>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}