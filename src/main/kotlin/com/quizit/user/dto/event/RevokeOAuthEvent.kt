package com.quizit.user.dto.event

import com.quizit.user.domain.enum.Provider

data class RevokeOAuthEvent(
    val email: String,
    val provider: Provider
)