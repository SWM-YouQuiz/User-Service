package com.youquiz.user.domain

import com.youquiz.user.domain.enum.Role
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class User(
    @Id
    var id: String? = null,
    val username: String,
    val password: String,
    val nickname: String,
    val role: Role,
    val allowPush: Boolean,
    var answerRate: Double,
    val correctQuizIds: MutableSet<String>,
    val incorrectQuizIds: MutableSet<String>,
    val likedQuizIds: MutableSet<String>,
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()
) {
    fun correctAnswer(quizId: String) {
        correctQuizIds.add(quizId)
        changeAnswerRate()
    }

    fun incorrectAnswer(quizId: String) {
        incorrectQuizIds.add(quizId)
        changeAnswerRate()
    }

    fun likeQuiz(quizId: String) {
        likedQuizIds.add(quizId)
    }

    fun unlikeQuiz(quizId: String) {
        likedQuizIds.remove(quizId)
    }

    private fun changeAnswerRate() {
        answerRate = (correctQuizIds.size.toDouble() / (correctQuizIds.size + incorrectQuizIds.size).toDouble()) * 100
    }
}