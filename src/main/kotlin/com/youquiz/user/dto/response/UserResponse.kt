package com.youquiz.user.dto.response

import com.youquiz.user.domain.User
import com.youquiz.user.domain.enum.Role
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val nickname: String,
    val role: Role,
    val allowPush: Boolean,
    val answerRate: Double,
    val createdDate: LocalDateTime,
    val correctQuizIds: Set<String>,
    val incorrectQuizIds: Set<String>,
    val likedQuizIds: Set<String>
) {
    companion object {
        operator fun invoke(user: User): UserResponse =
            with(user) {
                UserResponse(
                    id = id!!,
                    username = username,
                    nickname = nickname,
                    role = role,
                    allowPush = allowPush,
                    answerRate = answerRate,
                    createdDate = createdDate,
                    correctQuizIds = correctQuizIds,
                    incorrectQuizIds = incorrectQuizIds,
                    likedQuizIds = likedQuizIds
                )
            }
    }
}