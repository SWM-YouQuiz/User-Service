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
                val increasedUser = createUser().apply { correctAnswer(ID) }

                Then("해당 유저의 정답률이 상승한다.") {
                    increasedUser.answerRate shouldBeGreaterThan user.answerRate
                }
            }

            When("유저가 퀴즈의 정답을 틀렸다면") {
                val decreasedUser = createUser().apply { incorrectAnswer(ID) }

                Then("해당 유저의 정답률이 감소한다.") {
                    decreasedUser.answerRate shouldBeLessThan user.answerRate
                }
            }

            When("유저가 퀴즈에 좋아요를 했다면") {
                val likeUser = createUser().apply { likeQuiz(ID) }

                Then("해당 유저의 좋아요한 퀴즈가 추가된다.") {
                    likeUser.likedQuizIds.size shouldBeGreaterThan user.likedQuizIds.size
                }
            }

            When("유저가 퀴즈에 좋아요 취소를 했다면") {
                val unlikeUser = createUser().apply { unlikeQuiz(likedQuizIds.random()) }

                Then("해당 유저의 좋아요한 퀴즈가 삭제된다.") {
                    unlikeUser.likedQuizIds.size shouldBeLessThan user.likedQuizIds.size
                }
            }
        }
    }
}