package com.youquiz.user.dto.event

data class CheckAnswerEvent(
    val userId: String,
    val quizId: String,
    val isAnswer: Boolean
)