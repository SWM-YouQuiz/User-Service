package com.youquiz.user.service

import com.youquiz.user.dto.UserResponse
import com.youquiz.user.exception.UserNotFoundException
import com.youquiz.user.exception.UsernameAlreadyExistException
import com.youquiz.user.fixture.createCreateUserRequest
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
        Given("유저가 존재하는 경우") {
            val user = createUser().also {
                coEvery { userRepository.findById(any()) } returns it
                coEvery { userRepository.findByUsername(any()) } returns it
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                val userResponse = userService.findById(user.id!!)

                Then("식별자에 맞는 유저가 조회된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(user)
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val userResponse = userService.findByUsername(user.username)

                Then("아이디에 맞는 유저가 조회된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(user)
                }
            }
        }

        Given("유저가 존재하지 않는 경우") {
            val user = createUser().also {
                coEvery { userRepository.findById(any()) } returns null
                coEvery { userRepository.findByUsername(any()) } returns null
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userService.findById(user.id!!)
                    }
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userService.findByUsername(user.username)
                    }
                }
            }
        }

        Given("해당 아이디를 가진 유저가 없는 경우") {
            val newUser = createUser().also {
                coEvery { userRepository.save(any()) } returns it
            }

            coEvery { userRepository.findByUsername(any()) } returns null

            When("유저가 회원가입을 시도하면") {
                val userResponse = userService.createUser(createCreateUserRequest())

                Then("유저가 생성된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(newUser)
                }
            }
        }

        Given("해당 아이디를 가진 유저가 이미 존재하는 경우") {
            val user = createUser().also {
                coEvery { userRepository.findByUsername(any()) } returns it
            }

            When("유저가 회원가입을 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UsernameAlreadyExistException> {
                        userService.createUser(createCreateUserRequest())
                    }
                }
            }
        }
    }
}