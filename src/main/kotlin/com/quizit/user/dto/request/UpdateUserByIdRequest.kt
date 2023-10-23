package com.quizit.user.dto.request

data class UpdateUserByIdRequest(
    val username: String,
    val image: String,
    val allowPush: Boolean,
    val dailyTarget: Int
)