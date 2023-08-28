package com.quizit.user.service

import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.fixture.*
import com.quizit.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserServiceTest : BehaviorSpec() {
    private val userRepository = mockk<UserRepository>()

    private val passwordEncoder = BCryptPasswordEncoder()

    private val userService: UserService = UserService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder
    )

    init {
        Given("유저가 존재하는 경우") {
            val user = createUser().also {
                coEvery { userRepository.findAll() } returns flowOf(it)
                coEvery { userRepository.findById(any()) } returns it
                coEvery { userRepository.findByUsername(any()) } returns it
            }

            coEvery { userRepository.deleteById(any()) } just runs

            When("모든 유저 조회를 시도하면") {
                val userResponses = userService.getUsers()

                Then("모든 유저가 조회된다.") {
                    userResponses.collect { it shouldBeEqualToComparingFields UserResponse(user) }
                }
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                val userResponse = userService.getUserById(user.id!!)

                Then("식별자에 맞는 유저가 조회된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(user)
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val userResponse = userService.getUserByUsername(user.username)

                Then("아이디에 맞는 유저가 조회된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(user)
                }
            }

            When("아이디를 통해 패스워드 조회를 시도하면") {
                val getPasswordResponse = userService.getPasswordByUsername(user.username)

                Then("아이디에 맞는 유저의 패스워드가 조회된다.") {
                    getPasswordResponse.password shouldBe user.password
                }
            }

            When("프로필을 수정하면") {
                val updateUserByIdRequest = createUpdateUserByIdRequest(nickname = "update").also {
                    coEvery { userRepository.save(any()) } returns createUser(nickname = it.nickname)
                }
                val userResponse = userService.updateUserById(ID, createJwtAuthentication(), updateUserByIdRequest)

                Then("유저 정보가 변경된다.") {
                    userResponse.nickname shouldNotBeEqual user.nickname
                }
            }

            When("패스워드를 수정하면") {
                val changePasswordRequest = createChangePasswordRequest(newPassword = "update").also {
                    coEvery { userRepository.save(any()) } returns createUser(
                        password = passwordEncoder.encode(it.newPassword)
                    )
                }
                val userResponse = userService.changePassword(ID, createJwtAuthentication(), changePasswordRequest)

                Then("패스워드가 변경된다.") {
                    coVerify { userRepository.save(any()) }
                }
            }

            When("회원 탈퇴를 하면") {
                userService.deleteUserById(ID, createJwtAuthentication())

                Then("유저가 삭제된다.") {
                    coVerify { userRepository.deleteById(any()) }
                }
            }
        }

        Given("유저가 존재하지 않는 경우") {
            val user = createUser()

            coEvery { userRepository.findById(any()) } returns null
            coEvery { userRepository.findByUsername(any()) } returns null

            When("식별자를 통해 유저 조회를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userService.getUserById(user.id!!)
                    }
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userService.getUserByUsername(user.username)
                    }
                }
            }

            When("아이디를 통해 패스워드 조회를 시도하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userService.getPasswordByUsername(user.username)
                    }
                }
            }
        }

        Given("해당 아이디를 가진 유저가 없는 경우") {
            val user = createUser().also {
                coEvery { userRepository.save(any()) } returns it
            }

            coEvery { userRepository.findByUsername(any()) } returns null

            When("유저가 회원가입을 시도하면") {
                val userResponse = userService.createUser(createCreateUserRequest())

                Then("유저가 생성된다.") {
                    userResponse shouldBeEqualToComparingFields UserResponse(user)
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