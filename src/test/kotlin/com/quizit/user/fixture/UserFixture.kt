package com.quizit.user.fixture

import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Provider
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.UserResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val EMAIL = "email"
const val USERNAME = "username"
const val PASSWORD = "password"
const val IMAGE = "image"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val PROVIDER = Provider.GOOGLE
val CORRECT_QUIZ_IDS = hashSetOf("1")
val INCORRECT_QUIZ_IDS = hashSetOf("1")
val MARKED_QUIZ_IDS = hashSetOf("1")
const val IS_MATCHED = true
val passwordEncoder = BCryptPasswordEncoder()

fun createCreateUserRequest(
    email: String = EMAIL,
    username: String = USERNAME,
    image: String = IMAGE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    provider: Provider = PROVIDER
): CreateUserRequest =
    CreateUserRequest(
        email = email,
        username = username,
        image = image,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        provider = provider
    )

fun createUserResponse(
    id: String = ID,
    email: String = EMAIL,
    username: String = USERNAME,
    image: String = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider = PROVIDER,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        email = email,
        username = username,
        image = image,
        level = level,
        role = role,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        answerRate = answerRate,
        provider = provider,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        markedQuizIds = markedQuizIds
    )

fun createUpdateUserByIdRequest(
    username: String = USERNAME,
    image: String = IMAGE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET
): UpdateUserByIdRequest =
    UpdateUserByIdRequest(
        username = username,
        image = image,
        allowPush = allowPush,
        dailyTarget = dailyTarget
    )

fun createUser(
    id: String = ID,
    email: String = EMAIL,
    username: String = USERNAME,
    image: String = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider = PROVIDER,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS.toHashSet(),
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS.toHashSet(),
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS.toHashSet(),
    createdDate: LocalDateTime = CREATED_DATE
): User = User(
    id = id,
    email = email,
    username = username,
    image = image,
    level = level,
    role = role,
    allowPush = allowPush,
    dailyTarget = dailyTarget,
    answerRate = answerRate,
    provider = provider,
    correctQuizIds = correctQuizIds,
    incorrectQuizIds = incorrectQuizIds,
    markedQuizIds = markedQuizIds,
    createdDate = createdDate
)