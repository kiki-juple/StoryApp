package com.juple.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.juple.storyapp.local.UserModel
import com.juple.storyapp.local.UserPreference
import com.juple.storyapp.remote.ApiConfig
import com.juple.storyapp.remote.LoginResponse
import com.juple.storyapp.remote.LoginResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _userLogin = MutableLiveData<LoginResult>()
    val userLogin: LiveData<LoginResult> = _userLogin

    private var _responseCode = MutableLiveData<Int>()
    val responseCode: LiveData<Int> = _responseCode

    private var _showSnackBar = MutableLiveData<Boolean>()
    val showSnackBar: LiveData<Boolean> = _showSnackBar

    private val _snackText = MutableLiveData<String>()
    val snackText: LiveData<String> = _snackText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun loginData(email: String, password: String) {
        _showSnackBar.value = false
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginUser(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                _responseCode.value = response.code()
                Log.e(TAG, "CEK RESPON CODE: ${response.code()}")
                if (response.isSuccessful) {
                    _userLogin.value = response.body()?.loginResult
                } else {
                    _showSnackBar.value = true
                    _snackText.value = response.errorBody()?.string()
                        ?.let { JSONObject(it).getString("message").trim() }
                    Log.e(TAG, "Failure: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _showSnackBar.value = true
                _isLoading.value = false
                _snackText.value = "Check your internet connection"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}