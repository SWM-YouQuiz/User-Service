package com.youquiz.user.event

data class IncorrectAnswerEvent(
    val userId: String,
    val quizId: String
)