package com.juple.storyapp.local

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean
)
