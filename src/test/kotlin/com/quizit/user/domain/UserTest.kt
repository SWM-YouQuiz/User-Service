package com.quizit.user.domain

import com.quizit.user.fixture.ID
import com.quizit.user.fixture.createUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan

class UserTest : BehaviorSpec() {
    init {
        Given("유저가 존재하는 경우") {
            val user = createUser()

            When("유저가 퀴즈의 정답을 맞췄다면") {
                val correctAnswerUser = createUser().apply {
                    correctAnswer(ID)
                }

                Then("해당 유저의 정답률이 상승한다.") {
                    correctAnswerUser.answerRate shouldBeGreaterThan user.answerRate
                }
            }

            When("유저가 퀴즈의 정답을 틀렸다면") {
                val incorrectAnswerUser = createUser().apply { incorrectAnswer(ID) }

                Then("해당 유저의 정답률이 감소한다.") {
                    incorrectAnswerUser.answerRate shouldBeLessThan user.answerRate
                }
            }

            When("유저가 퀴즈를 저장했다면") {
                val markUser = createUser().apply { markQuiz(ID) }

                Then("해당 퀴즈가 유저의 저장한 퀴즈에 추가된다.") {
                    markUser.markedQuizIds.size shouldBeGreaterThan user.markedQuizIds.size
                }
            }

            When("유저가 퀴즈를 저장 취소했다면") {
                val unmarkUser = createUser().apply { unmarkQuiz(markedQuizIds.random()) }

                Then("해당 퀴즈가 유저의 저장한 퀴즈에서 삭제된다.") {
                    unmarkUser.markedQuizIds.size shouldBeLessThan user.markedQuizIds.size
                }
            }

            When("유저의 경험치가 일정 수준 이상 도달했다면") {
                val levelUpUser = createUser().apply { levelUp() }

                Then("해당 유저의 레벨이 증가한다.") {
                    levelUpUser.level shouldBeGreaterThan user.level
                }
            }
        }
    }
}