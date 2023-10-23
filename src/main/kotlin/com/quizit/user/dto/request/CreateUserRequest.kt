package com.quizit.user.dto.request

import com.quizit.user.domain.enum.Provider

data class CreateUserRequest(
    val email: String,
    val username: String,
    val image: String,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val provider: Provider
)