package com.quizit.user.dto.event

data class MarkQuizEvent(
    val userId: String,
    val quizId: String,
    val isMarked: Boolean
)