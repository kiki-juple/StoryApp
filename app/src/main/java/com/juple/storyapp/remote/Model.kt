package com.juple.storyapp.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String
) : Parcelable

data class DefaultResponse(
    val error: Boolean,
    val message: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<User>
)