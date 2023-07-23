package com.youquiz.user.dto

data class CreateUserRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val allowPush: Boolean,
)