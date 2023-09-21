package com.quizit.user.service

import com.quizit.user.exception.PermissionDeniedException
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.fixture.*
import com.quizit.user.repository.UserRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

class UserServiceTest : BehaviorSpec() {
    private val userRepository = mockk<UserRepository>()

    private val userService = UserService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        Given("유저가 존재하는 경우") {
            createUser().also {
                every { userRepository.findAll() } returns Flux.just(it)
                every { userRepository.findAllOrderByCorrectQuizIdsSize() } returns Flux.just(it)
                every { userRepository.findById(any<String>()) } returns Mono.just(it)
                every { userRepository.findByUsername(any()) } returns Mono.just(it)
                every { userRepository.deleteById(any<String>()) } returns Mono.empty()
            }

            When("유저 랭킹 조회를 시도하면") {
                val result = StepVerifier.create(userService.getRanking())

                Then("모든 유저에 대한 랭킹이 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createUserResponse())
                        .verifyComplete()
                }
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                val result = StepVerifier.create(userService.getUserById(ID))

                Then("식별자에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createUserResponse())
                        .verifyComplete()
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val result = StepVerifier.create(userService.getUserByUsername(USERNAME))

                Then("아이디에 맞는 유저가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createUserResponse())
                        .verifyComplete()
                }
            }

            When("아이디를 통해 패스워드 일치 여부를 확인하면") {
                val result = StepVerifier.create(userService.matchPassword(USERNAME, createMatchPasswordRequest()))

                Then("아이디에 맞는 유저의 패스워드 일치 여부가 확인된다.") {
                    result.expectSubscription()
                        .assertNext { it.isMatched shouldBe true }
                        .verifyComplete()
                }
            }

            When("프로필을 수정하면") {
                val updateUserByIdRequest = createUpdateUserByIdRequest(nickname = "updated_nickname")
                    .also {
                        every { userRepository.save(any()) } returns Mono.just(createUser(nickname = it.nickname))
                    }
                val result = StepVerifier.create(
                    userService.updateUserById(ID, createJwtAuthentication(), updateUserByIdRequest)
                )

                Then("유저 정보가 변경된다.") {
                    result.expectSubscription()
                        .assertNext { it.nickname shouldNotBeEqual NICKNAME }
                        .verifyComplete()
                }
            }

            When("패스워드를 수정하면") {
                val changePasswordRequest = createChangePasswordRequest(newPassword = "updated_password")
                    .also {
                        every { userRepository.save(any()) } returns Mono.just(
                            createUser(password = passwordEncoder.encode(it.newPassword))
                        )
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
            createUser().apply {
                every { userRepository.findById(any<String>()) } returns Mono.empty()
                every { userRepository.findByUsername(any()) } returns Mono.empty()
            }

            When("식별자를 통해 유저 조회를 시도하면") {
                val result = StepVerifier.create(userService.getUserById(ID))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("아이디를 통해 유저 조회를 시도하면") {
                val result = StepVerifier.create(userService.getUserByUsername(USERNAME))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("아이디를 통해 패스워드 일치 여부를 확인하면") {
                val result = StepVerifier.create(userService.matchPassword(USERNAME, createMatchPasswordRequest()))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }

            When("회원 탈퇴를 시도하면") {
                val result = StepVerifier.create(userService.deleteUserById(ID, createJwtAuthentication()))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UserNotFoundException>()
                        .verify()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 없는 경우") {
            createUser().also {
                every { userRepository.save(any()) } returns Mono.just(it)
                every { userRepository.findByUsername(any()) } returns Mono.empty()
            }

            When("유저가 회원가입을 시도하면") {
                val result = StepVerifier.create(userService.createUser(createCreateUserRequest()))

                Then("유저가 생성된다.") {
                    result.expectSubscription()
                        .assertNext { it shouldBeEqualToComparingFields createUserResponse() }
                        .verifyComplete()
                }
            }
        }

        Given("해당 아이디를 가진 유저가 이미 존재하는 경우") {
            createUser().also {
                every { userRepository.findByUsername(any()) } returns Mono.just(it)
            }

            When("유저가 회원가입을 시도하면") {
                val result = StepVerifier.create(userService.createUser(createCreateUserRequest()))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<UsernameAlreadyExistException>()
                        .verify()
                }
            }
        }

        Given("권한이 없는 경우") {
            createUser().also {
                every { userRepository.findById(any<String>()) } returns Mono.just(it)
            }

            When("프로필을 수정하면") {
                val result =
                    StepVerifier.create(
                        userService.updateUserById(
                            ID, createJwtAuthentication(id = "invalid_id"), createUpdateUserByIdRequest()
                        )
                    )

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("패스워드를 변경하면") {
                val result = StepVerifier.create(
                    userService.changePassword(
                        ID, createJwtAuthentication(id = "invalid_id"), createChangePasswordRequest()
                    )
                )

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }

            When("회원 탈퇴를 시도하면") {
                val result =
                    StepVerifier.create(userService.deleteUserById(ID, createJwtAuthentication(id = "invalid_id")))

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<PermissionDeniedException>()
                        .verify()
                }
            }
        }
    }
}