package com.quizit.user.dto.request

data class UpdateUserByIdRequest(
    val nickname: String,
    val allowPush: Boolean,
)