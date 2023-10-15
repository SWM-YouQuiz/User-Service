package com.quizit.user.fixture

import com.quizit.user.dto.response.QuizResponse
import java.time.LocalDateTime

const val QUESTION = "question"
val OPTIONS = (0..4).map { "$it" }
const val CORRECT_COUNT = 10L
const val INCORRECT_COUNT = 10L
val MARKED_USER_IDS = hashSetOf("1")
val LIKED_USER_IDS = hashSetOf("1")
val UNLIKED_USER_IDS = hashSetOf("1")

fun createQuizResponse(
    id: String = ID,
    question: String = QUESTION,
    writerId: String = ID,
    chapterId: String = ID,
    options: List<String> = OPTIONS,
    answerRate: Double = ANSWER_RATE,
    correctCount: Long = CORRECT_COUNT,
    incorrectCount: Long = INCORRECT_COUNT,
    markedUserIds: HashSet<String> = MARKED_USER_IDS,
    likedUserIds: HashSet<String> = LIKED_USER_IDS,
    unlikedUserIds: HashSet<String> = UNLIKED_USER_IDS,
    createdDate: LocalDateTime = CREATED_DATE
): QuizResponse =
    QuizResponse(
        id = id,
        question = question,
        writerId = writerId,
        chapterId = chapterId,
        options = options,
        answerRate = answerRate,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        markedUserIds = markedUserIds,
        likedUserIds = likedUserIds,
        unlikedUserIds = unlikedUserIds,
        createdDate = createdDate
    )