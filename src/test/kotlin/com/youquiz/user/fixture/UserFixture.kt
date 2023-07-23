package com.youquiz.user.fixture

import com.youquiz.user.domain.User
import com.youquiz.user.domain.enum.Role
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

const val USERNAME = "test"
const val NICKNAME = "test"
const val PASSWORD = "test"
val ROLE = Role.USER
const val ALLOW_PUSH = true

fun createUser(
    id: Long = ID,
    username: String = USERNAME,
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
): User = User(
    id = id,
    username = username,
    password = BCryptPasswordEncoder().encode(password),
    nickname = nickname,
    role = role,
    allowPush = allowPush
)