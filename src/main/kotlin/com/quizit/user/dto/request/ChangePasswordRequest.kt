package com.quizit.user.dto.request

data class ChangePasswordRequest(
    val password: String,
    val newPassword: String
)