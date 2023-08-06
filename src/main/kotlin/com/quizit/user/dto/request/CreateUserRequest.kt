package com.quizit.user.dto.request

data class CreateUserRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val allowPush: Boolean,
)