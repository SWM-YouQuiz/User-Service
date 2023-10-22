package com.quizit.user.domain

import com.quizit.user.domain.enum.Provider
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
    var password: String?,
    var nickname: String,
    var image: String?,
    var level: Int,
    val role: Role,
    var allowPush: Boolean,
    var dailyTarget: Int,
    var answerRate: Double,
    val provider: Provider?,
    val correctQuizIds: HashSet<String>,
    val incorrectQuizIds: HashSet<String>,
    val markedQuizIds: HashSet<String>,
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

    fun checkLevel() {
        if (correctQuizIds.size >= level * 5) {
            level += 1
        }
    }

    fun update(nickname: String, image: String?, allowPush: Boolean, dailyTarget: Int): User =
        also {
            it.nickname = nickname
            it.image = image
            it.allowPush = allowPush
            it.dailyTarget = dailyTarget
        }

    fun updatePassword(password: String): User =
        also { it.password = password }

    private fun changeAnswerRate() {
        answerRate = (correctQuizIds.size.toDouble() / (correctQuizIds.size + incorrectQuizIds.size).toDouble()) * 100
    }
}