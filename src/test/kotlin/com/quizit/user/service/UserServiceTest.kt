package com.quizit.user.service

import com.quizit.user.adapter.client.QuizClient
import com.quizit.user.adapter.producer.UserProducer
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.PermissionDeniedException
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.fixture.*
import com.quizit.user.repository.UserRepository
import com.quizit.user.util.empty
import com.quizit.user.util.getResult
import com.quizit.user.util.returns
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class UserServiceTest : BehaviorSpec() {
    private val userRepository = mockk<UserRepository>()

    private val quizClient = mockk<QuizClient>()

    private val userProducer = mockk<UserProducer>()
        .apply {
            every { deleteUser(any()) } returns Mono.empty()
        }

    private val userService = UserService(
        userRepository = userRepository,
        quizClient = quizClient,
        userProducer = userProducer,
        passwordEncoder = passwordEncoder
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("유저가 존재하는 경우") {
            val user = createUser()
                .also {
                    every { userRepository.findAll() } returns listOf(it)
                    every { userRepository.findAllOrderByCorrectQuizIdsSize() } returns listOf(it)
                    every { userRepository.findAllOrderByCorrectQuizIdsSizeInQuizIds(any()) } returns listOf(it)
                    every { userRepository.findById(any<String>()) } returns it
                    every { userRepository.findByUsername(any()) } returns it
                    every { userRepository.deleteById(any<String>()) } returns empty()
                    every { quizClient.getQuizzesByCourseId(any()) } returns listOf(createQuizResponse())
                }
            val userResponse = UserResponse(user)

            When("랭킹 조회를 시도하면") {
                val results = listOf(
                    userService.getRanking()
                        .getResult(),
                    userService.getRankingByCourseId(ID)
                        .getResult()
                )

                Then("모든 유저에 대한 랭킹이 조회된다.") {
                    results.map {
                        it.expectSubscription()
                            .expectNext(userResponse)
                            .verifyComplete()
                    }
                }
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                val result = userService.getUserById(ID)
                    .getResult()

                Then("식별자에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val result = userService.getUserByUsername(USERNAME)
                    .getResult()

                Then("아이디에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }

            When("아이디를 통해 패스워드 일치 여부를 확인하면") {
                val result = userService.matchPassword(USERNAME, createMatchPasswordRequest())
                    .getResult()

                Then("아이디에 맞는 유저의 패스워드 일치 여부가 확인된다.") {
                    result.expectSubscription()
                        .assertNext { it.isMatched shouldBe true }
                        .verifyComplete()
                }
            }

            When("프로필을 수정하면") {
                val updateUserByIdRequest = createUpdateUserByIdRequest(nickname = "updated_nickname")
                    .also {
                        every { userRepository.save(any()) } returns createUser(nickname = it.nickname)
                    }
                val result = userService.updateUserById(ID, createJwtAuthentication(), updateUserByIdRequest)
                    .getResult()

                Then("유저 정보가 변경된다.") {
                    result.expectSubscription()
                        .assertNext { it.nickname shouldNotBeEqual NICKNAME }
                        .verifyComplete()
                }
            }

            When("패스워드를 수정하면") {
                val changePasswordRequest = createChangePasswordRequest(newPassword = "updated_password")
                    .also {
                        every {
                            userRepository.save(any())
                        } returns createUser(password = passwordEncoder.encode(it.newPassword))
                    }

                userService.changePassword(ID, createJwtAuthentication(), changePasswordRequest)
                    .subscribe()

                Then("패스워드가 변경된다.") {
                    verify { userRepository.save(any()) }
                }
            }

            When("회원 탈퇴를 하면") {
                userService.deleteUserById(ID, createJwtAuthentication())
                    .subscribe()

                Then("유저가 삭제된다.") {
                    verify { userRepository.deleteById(any<String>()) }
                }
            }
        }

        Given("유저가 존재하지 않는 경우") {
            createUser()
                .apply {
                    every { userRepository.findById(any<String>()) } returns empty()
                    every { userRepository.findByUsername(any()) } returns empty()
                }

            When("식별자를 통해 유저 조회를 시도하면") {
                val result = userService.getUserById(ID)
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val result = userService.getUserByUsername(USERNAME)
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("아이디를 통해 패스워드 일치 여부를 확인하면") {
                val result = userService.matchPassword(USERNAME, createMatchPasswordRequest())
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("회원 탈퇴를 시도하면") {
                val result = userService.deleteUserById(ID, createJwtAuthentication())
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 없는 경우") {
            val user = createUser()
                .also {
                    every { userRepository.save(any()) } returns it
                    every { userRepository.findByUsername(any()) } returns empty()
                }
            val userResponse = UserResponse(user)

            When("유저가 회원가입을 시도하면") {
                val result = userService.createUser(createCreateUserRequest())
                    .getResult()

                Then("유저가 생성된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 이미 존재하는 경우") {
            createUser()
                .also {
                    every { userRepository.findByUsername(any()) } returns it
                }

            When("유저가 회원가입을 시도하면") {
                val result = userService.createUser(createCreateUserRequest())
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UsernameAlreadyExistException>()
                        .verify()
                }
            }
        }

        Given("권한이 없는 경우") {
            createUser()
                .also {
                    every { userRepository.findById(any<String>()) } returns it
                }

            When("프로필을 수정하면") {
                val result = userService.updateUserById(
                    ID, createJwtAuthentication(id = "invalid_id"), createUpdateUserByIdRequest()
                ).getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("패스워드를 변경하면") {
                val result =
                    userService.changePassword(
                        ID, createJwtAuthentication(id = "invalid_id"), createChangePasswordRequest()
                    ).getResult()


                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("회원 탈퇴를 시도하면") {
                val result = userService.deleteUserById(ID, createJwtAuthentication(id = "invalid_id"))
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }
        }
    }
}