package com.quizit.user.dto.request

data class CreateUserRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val image: String,
    val allowPush: Boolean,
    val dailyTarget: Int,
)