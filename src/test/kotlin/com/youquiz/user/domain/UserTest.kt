package com.youquiz.user.domain

import com.youquiz.user.fixture.ID
import com.youquiz.user.fixture.createUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan

class UserTest : BehaviorSpec() {
    init {
        Given("유저가 존재하는 경우") {
            val user = createUser()

            When("유저가 해당 퀴즈의 정답을 맞췄다면") {
                val increasedUser = createUser().apply { correctAnswer(ID) }

                Then("해당 유저의 정답률이 상승한다.") {
                    increasedUser.answerRate shouldBeGreaterThan user.answerRate
                }
            }

            When("유저가 해당 퀴즈의 정답을 틀렸다면") {
                val decreasedUser = createUser().apply { incorrectAnswer(ID) }

                Then("해당 유저의 정답률이 감소한다.") {
                    decreasedUser.answerRate shouldBeLessThan user.answerRate
                }
            }
        }
    }
}