package com.youquiz.user.service

import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.dto.UserResponse
import com.youquiz.user.exception.UsernameAlreadyExistException
import com.youquiz.user.fixture.createUser
import com.youquiz.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserServiceTest : BehaviorSpec() {
    private val userRepository = mockk<UserRepository>()

    private val userService: UserService = UserService(
        userRepository = userRepository,
        passwordEncoder = BCryptPasswordEncoder()
    )

    init {
        Given("해당 아이디를 가진 유저가 없는 경우") {
            val newUser = createUser().also {
                coEvery { userRepository.save(any()) } returns it
            }

            coEvery { userRepository.findFirstByUsername(any()) } returns null

            When("유저가 회원가입을 시도하면") {
                val userResponse = userService.createUser(
                    newUser.run {
                        CreateUserRequest(
                            username = username,
                            password = password,
                            nickname = nickname,
                            allowPush = allowPush
                        )
                    }
                )

                Then("유저가 생성된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(newUser)
                }
            }
        }

        Given("해당 아이디를 가진 유저가 이미 존재하는 경우") {
            val user = createUser().also {
                coEvery { userRepository.findFirstByUsername(any()) } returns it
            }

            When("유저가 회원가입을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UsernameAlreadyExistException> {
                        userService.createUser(
                            user.run {
                                CreateUserRequest(
                                    username = username,
                                    password = password,
                                    nickname = nickname,
                                    allowPush = allowPush
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}