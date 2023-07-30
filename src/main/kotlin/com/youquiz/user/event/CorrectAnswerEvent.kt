package com.youquiz.user.event

data class CorrectAnswerEvent(
    val userId: String,
    val quizId: String
)