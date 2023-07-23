package com.youquiz.user.dto

import com.youquiz.user.domain.User
import com.youquiz.user.domain.enum.Role
import java.time.LocalDateTime

class UserResponse(
    val id: Long,
    val username: String,
    val password: String,
    val nickname: String,
    val role: Role,
    val allowPush: Boolean,
    val createdDate: LocalDateTime
) {
    companion object {
        operator fun invoke(user: User): UserResponse =
            with(user) {
                UserResponse(
                    id = id!!,
                    username = username,
                    password = password,
                    nickname = nickname,
                    role = role,
                    allowPush = allowPush,
                    createdDate = createdDate
                )
            }
    }
}