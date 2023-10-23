package com.quizit.user.controller

import com.ninjasquad.springmockk.MockkBean
import com.quizit.user.dto.response.MatchPasswordResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.*
import com.quizit.user.fixture.*
import com.quizit.user.global.dto.ErrorResponse
import com.quizit.user.handler.UserHandler
import com.quizit.user.router.UserRouter
import com.quizit.user.service.UserService
import com.quizit.user.util.*
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(UserRouter::class, UserHandler::class)
class UserControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var userService: UserService

    private val createUserRequestFields = listOf(
        "username" desc "아이디",
        "password" desc "패스워드",
        "nickname" desc "닉네임",
        "image" desc "프로필 사진",
        "allowPush" desc "알림 여부",
        "dailyTarget" desc "하루 목표",
        "provider" desc "OAuth Provider"
    )

    private val updateUserByIdRequestFields = listOf(
        "nickname" desc "닉네임",
        "image" desc "프로필 사진",
        "allowPush" desc "알림 여부",
        "dailyTarget" desc "하루 목표",
    )

    private val userResponseFields = listOf(
        "id" desc "식별자",
        "username" desc "아이디",
        "nickname" desc "닉네임",
        "image" desc "프로필 사진",
        "level" desc "레벨",
        "role" desc "권한",
        "allowPush" desc "알림 여부",
        "dailyTarget" desc "하루 목표",
        "answerRate" desc "정답률",
        "provider" desc "OAuth Provider",
        "correctQuizIds" desc "맞은 퀴즈 리스트",
        "incorrectQuizIds" desc "틀린 퀴즈 리스트",
        "markedQuizIds" desc "저장한 퀴즈 리스트",
        "createdDate" desc "가입 날짜"
    )

    private val matchPasswordResponseFields = listOf(
        "isMatched" desc "패스워드 일치 여부",
    )

    init {
        describe("getRanking()은") {
            context("요청이 주어지면") {
                every { userService.getRanking() } returns listOf(createUserResponse())
                withMockUser()

                it("상태 코드 200과 랭킹 순서에 맞게 userResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/ranking")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<UserResponse>>()
                        .document(
                            "유저 랭킹 조회 성공(200)",
                            responseFields(userResponseFields.toListFields())
                        )
                }
            }
        }

        describe("getRankingByCourseId()는") {
            context("요청이 주어지면") {
                every { userService.getRankingByCourseId(any()) } returns listOf(createUserResponse())
                withMockUser()

                it("상태 코드 200과 코스 랭킹 순서에 맞게 userResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/ranking/course/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<UserResponse>>()
                        .document(
                            "유저 코스 랭킹 조회 성공(200)",
                            responseFields(userResponseFields.toListFields())
                        )
                }
            }
        }

        describe("getUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.getUserById(any()) } returns createUserResponse()
                withMockUser()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<UserResponse>()
                        .document(
                            "식별자를 통한 유저 단일 조회 성공(200)",
                            pathParameters("id" paramDesc "식별자"),
                            responseFields(userResponseFields)
                        )
                }
            }

            context("존재하지 않는 유저에 대한 식별자가 주어지면") {
                every { userService.getUserById(any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody<ErrorResponse>()
                        .document(
                            "식별자를 통한 유저 단일 조회 실패(404)",
                            pathParameters("id" paramDesc "식별자"),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }

        describe("getUserByUsername()은") {
            context("존재하는 유저에 대한 아이디가 주어지면") {
                every { userService.getUserByUsername(any()) } returns createUserResponse()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<UserResponse>()
                        .document(
                            "아이디를 통한 유저 단일 조회 성공(200)",
                            pathParameters("username" paramDesc "아이디"),
                            responseFields(userResponseFields)
                        )
                }
            }

            context("존재하지 않는 유저에 대한 아이디가 주어지면") {
                every { userService.getUserByUsername(any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody<ErrorResponse>()
                        .document(
                            "아이디를 통한 유저 단일 조회 실패(404)",
                            pathParameters("username" paramDesc "아이디"),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }

        describe("createUser()는") {
            context("존재하지 않는 아이디가 주어지면") {
                every { userService.createUser(any()) } returns createUserResponse()
                withMockUser()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user")
                        .bodyValue(createCreateUserRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<UserResponse>()
                        .document(
                            "회원가입 성공(200)",
                            requestFields(createUserRequestFields),
                            responseFields(userResponseFields)
                        )
                }
            }

            context("이미 존재하는 아이디가 주어지면") {
                every { userService.createUser(any()) } throws UsernameAlreadyExistException()
                withMockUser()

                it("상태 코드 409와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user")
                        .bodyValue(createCreateUserRequest())
                        .exchange()
                        .expectStatus()
                        .is4xxClientError
                        .expectBody<ErrorResponse>()
                        .document(
                            "회원가입 실패(409)",
                            requestFields(createUserRequestFields),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }

        describe("updateUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.updateUserById(any(), any(), any()) } returns createUserResponse()
                withMockUser()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}", ID)
                        .bodyValue(createUpdateUserByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<UserResponse>()
                        .document(
                            "유저 수정 성공(200)",
                            requestFields(updateUserByIdRequestFields),
                            responseFields(userResponseFields)
                        )
                }
            }

            context("존재하지 않는 유저에 대한 식별자가 주어지면") {
                every { userService.updateUserById(any(), any(), any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}", ID)
                        .bodyValue(createUpdateUserByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody<ErrorResponse>()
                        .document(
                            "유저 수정 실패(404)",
                            pathParameters("id" paramDesc "식별자"),
                            requestFields(updateUserByIdRequestFields),
                            responseFields(errorResponseFields)
                        )
                }
            }

            context("본인이 아닌 다른 유저의 식별자가 주어지면") {
                every { userService.updateUserById(any(), any(), any()) } throws PermissionDeniedException()
                withMockUser()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}", ID)
                        .bodyValue(createUpdateUserByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody<ErrorResponse>()
                        .document(
                            "유저 수정 실패(403)",
                            pathParameters("id" paramDesc "식별자"),
                            requestFields(updateUserByIdRequestFields),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }

        describe("deleteUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.deleteUserById(any(), any()) } returns empty()
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient.delete()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .document(
                            "유저 삭제 성공(200)",
                            pathParameters("id" paramDesc "식별자"),
                        )
                }
            }

            context("존재하지 않는 유저에 대한 식별자가 주어지면") {
                every { userService.deleteUserById(any(), any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient.delete()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody<ErrorResponse>()
                        .document(
                            "유저 삭제 실패(404)",
                            pathParameters("id" paramDesc "식별자"),
                            responseFields(errorResponseFields)
                        )
                }
            }

            context("본인이 아닌 다른 유저의 식별자가 주어지면") {
                every { userService.deleteUserById(any(), any()) } throws PermissionDeniedException()
                withMockUser()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient.delete()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody<ErrorResponse>()
                        .document(
                            "유저 삭제 실패(403)",
                            pathParameters("id" paramDesc "식별자"),
                            responseFields(errorResponseFields)
                        )
                }
            }
        }
    }
}