package com.juple.storyapp.data.local.preferences

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean
)
