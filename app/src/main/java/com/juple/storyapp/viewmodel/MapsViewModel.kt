package com.juple.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juple.storyapp.data.remote.ApiConfig
import com.juple.storyapp.data.remote.Story
import com.juple.storyapp.data.remote.StoryResponse
import com.juple.storyapp.ui.LoginActivity
import com.juple.storyapp.ui.SplashActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private val token = SplashActivity.API_TOKEN.ifEmpty {
        LoginActivity.NEW_API_TOKEN
    }

    private val _lisStory = MutableLiveData<List<Story>>()
    val lisStory: LiveData<List<Story>> = _lisStory

    private val _snackText = MutableLiveData<String>()
    val snackText: LiveData<String> = _snackText

    private val _responseCode = MutableLiveData<Int>()
    val responseCode: LiveData<Int> = _responseCode

    init {
        getStoryWithLocation()
    }

    private fun getStoryWithLocation() {
        val client = ApiConfig.getApiService().getStoriesWithLocation(
            "Bearer $token",
            50,
            1
        )
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _responseCode.value = response.code()
                if (response.isSuccessful) {
                    _lisStory.value = response.body()?.listStory
                } else {
                    _snackText.value = response.errorBody()?.string()?.let {
                        JSONObject(it).getString("message").trim()
                    }
                    Log.e("MapsViewModel", response.body().toString())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _snackText.value = "Check your internet connection"
            }

        })
    }
}