package com.quizit.user.dto.response

import java.time.LocalDateTime

data class QuizResponse(
    val id: String,
    val question: String,
    val writerId: String,
    val chapterId: String,
    val answerRate: Double,
    val options: List<String>,
    val correctCount: Long,
    val incorrectCount: Long,
    val markedUserIds: HashSet<String>,
    val likedUserIds: HashSet<String>,
    val unlikedUserIds: HashSet<String>,
    val createdDate: LocalDateTime,
)