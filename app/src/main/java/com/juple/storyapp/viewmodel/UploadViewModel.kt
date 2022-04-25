package com.juple.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juple.storyapp.remote.ApiConfig
import com.juple.storyapp.remote.DefaultResponse
import com.juple.storyapp.ui.LoginActivity
import com.juple.storyapp.ui.SplashActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel : ViewModel() {

    private val token = SplashActivity.API_TOKEN.ifEmpty {
        LoginActivity.NEW_API_TOKEN
    }

    private var _responseCode = MutableLiveData<Int>()
    val responseCode: LiveData<Int> = _responseCode

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackText = MutableLiveData<String>()
    val snackText: LiveData<String> = _snackText

    fun uploadImage(image: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory("Bearer $token", image, description)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                _isLoading.value = false
                _responseCode.value = response.code()
                Log.e(TAG, "CEK RESPON CODE: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _snackText.value = responseBody.message
                    }
                } else {
                    _snackText.value = response.errorBody()?.string()
                        ?.let { JSONObject(it).getString("message").trim() }
                    Log.e(TAG, "Failed: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                _isLoading.value = false
                _snackText.value = "Check your internet connection"
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "UploadViewModel"
    }
}