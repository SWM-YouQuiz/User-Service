package com.quizit.user.fixture

import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.MatchPasswordRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.MatchPasswordResponse
import com.quizit.user.dto.response.UserResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

const val USERNAME = "earlgrey02@github.com"
const val NICKNAME = "earlgrey02"
const val PASSWORD = "root"
const val IMAGE = "test"
val ROLE = Role.USER
const val ALLOW_PUSH = true
const val DAILY_TARGET = 10
const val ANSWER_RATE = 50.0
val CORRECT_QUIZ_IDS = mutableSetOf("quiz_1")
val INCORRECT_QUIZ_IDS = mutableSetOf("quiz_1")
val LIKED_QUIZ_IDS = mutableSetOf("quiz_1")
const val IS_MATCHED = true

fun createCreateUserRequest(
    username: String = USERNAME,
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    image: String = IMAGE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET
): CreateUserRequest =
    CreateUserRequest(
        username = username,
        password = password,
        nickname = nickname,
        image = image,
        allowPush = allowPush,
        dailyTarget = dailyTarget
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
    image: String = IMAGE,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    createdDate: LocalDateTime = CREATED_DATE,
    correctQuizIds: Set<String> = CORRECT_QUIZ_IDS,
    incorrectQuizIds: Set<String> = INCORRECT_QUIZ_IDS,
    likedQuizIds: Set<String> = LIKED_QUIZ_IDS,
): UserResponse =
    UserResponse(
        id = id,
        username = username,
        nickname = nickname,
        image = image,
        role = role,
        allowPush = allowPush,
        dailyTarget = dailyTarget,
        answerRate = answerRate,
        createdDate = createdDate,
        correctQuizIds = correctQuizIds,
        incorrectQuizIds = incorrectQuizIds,
        likedQuizIds = likedQuizIds
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
    password: String = PASSWORD,
    nickname: String = NICKNAME,
    image: String = IMAGE,
    role: Role = ROLE,
    allowPush: Boolean = ALLOW_PUSH,
    dailyTarget: Int = DAILY_TARGET,
    answerRate: Double = ANSWER_RATE,
    correctQuizIds: MutableSet<String> = CORRECT_QUIZ_IDS.toMutableSet(),
    incorrectQuizIds: MutableSet<String> = INCORRECT_QUIZ_IDS.toMutableSet(),
    likedQuizIds: MutableSet<String> = LIKED_QUIZ_IDS.toMutableSet(),
    createdDate: LocalDateTime = CREATED_DATE
): User = User(
    id = id,
    username = username,
    password = BCryptPasswordEncoder().encode(password),
    nickname = nickname,
    image = image,
    role = role,
    allowPush = allowPush,
    dailyTarget = dailyTarget,
    answerRate = answerRate,
    correctQuizIds = correctQuizIds,
    incorrectQuizIds = incorrectQuizIds,
    likedQuizIds = likedQuizIds,
    createdDate = createdDate
)