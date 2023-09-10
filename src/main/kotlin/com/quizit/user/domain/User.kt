package com.quizit.user.domain

import com.quizit.user.domain.enum.Role
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
    val image: String,
    var level: Int,
    val role: Role,
    val allowPush: Boolean,
    val dailyTarget: Int,
    var answerRate: Double,
    val correctQuizIds: MutableSet<String>,
    val incorrectQuizIds: MutableSet<String>,
    val markedQuizIds: MutableSet<String>,
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

    fun markQuiz(quizId: String) {
        markedQuizIds.add(quizId)
    }

    fun unmarkQuiz(quizId: String) {
        markedQuizIds.remove(quizId)
    }

    private fun changeAnswerRate() {
        answerRate = (correctQuizIds.size.toDouble() / (correctQuizIds.size + incorrectQuizIds.size).toDouble()) * 100
    }
}