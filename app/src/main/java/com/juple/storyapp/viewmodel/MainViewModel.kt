package com.juple.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juple.storyapp.local.UserPreference
import com.juple.storyapp.remote.ApiConfig
import com.juple.storyapp.remote.StoryResponse
import com.juple.storyapp.remote.User
import com.juple.storyapp.ui.LoginActivity
import com.juple.storyapp.ui.SplashActivity
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    private val token = SplashActivity.API_TOKEN.ifEmpty {
        LoginActivity.NEW_API_TOKEN
    }

    private val _listStory = MutableLiveData<List<User>>()
    val listStory: LiveData<List<User>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackText = MutableLiveData<String>()
    val snackText: LiveData<String> = _snackText

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    init {
        getListStory()
    }

    private fun getListStory() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories("Bearer $token", 50)
        Log.d(TAG, "cek token: $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                Log.e(TAG, "CEK RESPON CODE: ${response.code()}")
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                } else {
                    _snackText.value = response.errorBody()?.string()
                        ?.let { JSONObject(it).getString("message").trim() }
                    Log.e(TAG, "Failed: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _snackText.value = "Check your internet connection"
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}