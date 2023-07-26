package com.youquiz.user.domain

import com.youquiz.user.domain.enum.Role
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table
class User(
    @Id
    var id: Long? = null,
    val username: String,
    val password: String,
    val nickname: String,
    val role: Role,
    val allowPush: Boolean,
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()
}