package com.youquiz.user.fixture

import com.youquiz.user.domain.User
import com.youquiz.user.domain.enum.Role
import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.dto.GetPasswordByUsernameResponse
import com.youquiz.user.dto.UserResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val ANSWER_RATE = 50.0
val CORRECT_QUIZ_IDS = setOf("test")
val INCORRECT_QUIZ_IDS = setOf("test")
val LIKED_QUIZ_IDS = setOf("test")

fun createCreateUserRequest(
    username: String = USERNAME,
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    allowPush: Boolean = ALLOW_PUSH,
): CreateUserRequest =
    CreateUserRequest(
        username = username,
        password = password,
        nickname = nickname,
        allowPush = allowPush
    )

fun createGetPasswordByUsernameResponse(
    password: String = PASSWORD
): GetPasswordByUsernameResponse =
    GetPasswordByUsernameResponse(BCryptPasswordEncoder().encode(password))

fun createUserResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    answerRate: Double = ANSWER_RATE,
    createdDate: LocalDateTime = LocalDateTime.now(),
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    likedQuizIds: Set<String> = LIKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
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

fun createUser(
    id: String = ID,
    username: String = USERNAME,
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    answerRate: Double = ANSWER_RATE,
    correctQuizIds: MutableSet<String> = mutableSetOf("1"),
    incorrectQuizIds: MutableSet<String> = mutableSetOf("1"),
    likedQuizIds: MutableSet<String> = mutableSetOf("1"),
): User = User(
    id = id,
    username = username,
    password = BCryptPasswordEncoder().encode(password),
    nickname = nickname,
    role = role,
    allowPush = allowPush,
    answerRate = answerRate,
    correctQuizIds = correctQuizIds,
    incorrectQuizIds = incorrectQuizIds,
    likedQuizIds = likedQuizIds
)