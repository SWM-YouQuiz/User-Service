package com.quizit.user.fixture

import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Provider
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.MatchPasswordRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.MatchPasswordResponse
import com.quizit.user.dto.response.UserResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val USERNAME = "username"
const val NICKNAME = "nickname"
const val PASSWORD = "password"
const val IMAGE = "image"
const val LEVEL = 2
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val PROVIDER = null
val CORRECT_QUIZ_IDS = hashSetOf("1")
val INCORRECT_QUIZ_IDS = hashSetOf("1")
val MARKED_QUIZ_IDS = hashSetOf("1")
const val IS_MATCHED = true
val passwordEncoder = BCryptPasswordEncoder()

fun createCreateUserRequest(
    username: String = USERNAME,
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    image: String? = IMAGE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    provider: Provider? = PROVIDER
): CreateUserRequest =
    CreateUserRequest(
        username = username,
        password = password,
        nickname = nickname,
        image = image,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        provider = provider
    )

fun createMatchPasswordRequest(
    password: String = PASSWORD
): MatchPasswordRequest =
    MatchPasswordRequest(password)

fun createMatchPasswordResponse(
    isMatched: Boolean = IS_MATCHED
): MatchPasswordResponse =
    MatchPasswordResponse(isMatched)

fun createUserResponse(
    id: String = ID,
    username: String = USERNAME,
    nickname: String = NICKNAME,
    image: String? = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider? = PROVIDER,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS,
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        username = username,
        nickname = nickname,
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
    nickname: String = NICKNAME,
    image: String = IMAGE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET
): UpdateUserByIdRequest =
    UpdateUserByIdRequest(
        nickname = nickname,
        image = image,
        allowPush = allowPush,
        dailyTarget = dailyTarget
    )

fun createChangePasswordRequest(
    password: String = PASSWORD,
    newPassword: String = PASSWORD
): ChangePasswordRequest =
    ChangePasswordRequest(
        password = password,
        newPassword = newPassword
    )

fun createUser(
    id: String = ID,
    username: String = USERNAME,
    password: String? = PASSWORD,
    nickname: String = NICKNAME,
    image: String? = IMAGE,
    level: Int = LEVEL,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    provider: Provider? = PROVIDER,
    correctQuizIds: HashSet<String> = CORRECT_QUIZ_IDS.toHashSet(),
    incorrectQuizIds: HashSet<String> = INCORRECT_QUIZ_IDS.toHashSet(),
    markedQuizIds: HashSet<String> = MARKED_QUIZ_IDS.toHashSet(),
    createdDate: LocalDateTime = CREATED_DATE
): User = User(
    id = id,
    username = username,
    password = password?.run(passwordEncoder::encode),
    nickname = nickname,
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