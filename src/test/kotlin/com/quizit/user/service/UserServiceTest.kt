package com.quizit.user.service

import com.quizit.user.adapter.client.QuizClient
import com.quizit.user.adapter.producer.UserProducer
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.PermissionDeniedException
import com.quizit.user.exception.UserAlreadyExistException
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.fixture.*
import com.quizit.user.repository.UserRepository
import com.quizit.user.util.empty
import com.quizit.user.util.getResult
import com.quizit.user.util.returns
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.kotlin.test.expectError

class UserServiceTest : BehaviorSpec() {
    private val userRepository = mockk<UserRepository>()

    private val quizClient = mockk<QuizClient>()

    private val userProducer = mockk<UserProducer>()
        .apply {
            every { deleteUser(any()) } returns empty()
        }

    private val userService = UserService(
        userRepository = userRepository,
        quizClient = quizClient,
        userProducer = userProducer,
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
                    every { userRepository.findByEmail(any()) } returns it
                    every { userRepository.findByEmailAndProvider(any(), any()) } returns it
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

            When("인증 객체를 통해 유저 조회를 시도하면") {
                val result = userService.getUserByAuthentication(createJwtAuthentication())
                    .getResult()

                Then("인증 객체의 식별자에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }

            When("이메일을 통해 유저 조회를 시도하면") {
                val result = userService.getUserByEmail(EMAIL)
                    .getResult()

                Then("이메일에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }


            When("이메일과 OAuth 제공자를 통해 유저 조회를 시도하면") {
                val result = userService.getUserByEmailAndProvider(EMAIL, PROVIDER)
                    .getResult()

                Then("이메일과 OAuth 제공자에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(userResponse)
                        .verifyComplete()
                }
            }

            When("프로필을 수정하면") {
                val updateUserByIdRequest = createUpdateUserByIdRequest(username = "updated_username")
                    .also {
                        every { userRepository.save(any()) } returns createUser(username = it.username)
                    }
                val result = userService.updateUserById(ID, createJwtAuthentication(), updateUserByIdRequest)
                    .getResult()

                Then("유저 정보가 변경된다.") {
                    result.expectSubscription()
                        .assertNext { it.username shouldNotBeEqual USERNAME }
                        .verifyComplete()
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
                    every { userRepository.findByEmail(any()) } returns empty()
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

            When("이메일을 통해 유저 조회를 시도하면") {
                val result = userService.getUserByEmail(EMAIL)
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

        Given("해당 이메일을 가진 유저가 없는 경우") {
            val user = createUser()
                .also {
                    every { userRepository.save(any()) } returns it
                    every { userRepository.findByEmailAndProvider(any(), any()) } returns empty()
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

        Given("해당 이메일을 가진 유저가 이미 존재하는 경우") {
            createUser()
                .also {
                    every { userRepository.findByEmailAndProvider(any(), any()) } returns it
                }

            When("유저가 회원가입을 시도하면") {
                val result = userService.createUser(createCreateUserRequest())
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserAlreadyExistException>()
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