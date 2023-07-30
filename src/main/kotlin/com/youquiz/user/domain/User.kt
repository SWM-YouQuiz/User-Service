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
) {
    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()
}