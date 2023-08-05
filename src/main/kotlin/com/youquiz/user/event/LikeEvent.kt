package com.youquiz.user.event

data class LikeEvent(
    val userId: String,
    val quizId: String,
    val isLike: Boolean
)