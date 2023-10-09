package com.quizit.user.dto.request

data class UpdateUserByIdRequest(
    val nickname: String,
    val image: String?,
    val allowPush: Boolean,
    val dailyTarget: Int
)