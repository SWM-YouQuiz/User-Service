package com.youquiz.user.dto.event

data class LikeQuizEvent(
    val userId: String,
    val quizId: String,
    val isLike: Boolean
)