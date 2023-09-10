package com.quizit.user.dto.response

import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val image: String,
    val level: Int,
    val role: Role,
    val allowPush: Boolean,
    val dailyTarget: Int,
    val answerRate: Double,
    val createdDate: LocalDateTime,
    val correctQuizIds: Set<String>,
    val incorrectQuizIds: Set<String>,
    val markedQuizIds: Set<String>
) {
    companion object {
        operator fun invoke(user: User): UserResponse =
            with(user) {
                UserResponse(
                    id = id!!,
                    username = username,
                    nickname = nickname,
                    image = image,
                    level = level,
                    role = role,
                    allowPush = allowPush,
                    dailyTarget = dailyTarget,
                    answerRate = answerRate,
                    createdDate = createdDate,
                    correctQuizIds = correctQuizIds,
                    incorrectQuizIds = incorrectQuizIds,
                    markedQuizIds = markedQuizIds
                )
            }
    }
}