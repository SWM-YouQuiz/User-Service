package com.quizit.user.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.quizit.user.dto.response.GetPasswordByUsernameResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.fixture.*
import com.quizit.user.global.dto.ErrorResponse
import com.quizit.user.handler.UserHandler
import com.quizit.user.router.UserRouter
import com.quizit.user.service.UserService
import com.quizit.user.util.BaseControllerTest
import com.quizit.user.util.desc
import com.quizit.user.util.errorResponseFields
import com.quizit.user.util.paramDesc
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters

@WebFluxTest(UserRouter::class, UserHandler::class)
class UserControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var userService: UserService

    private val createUserRequestFields = listOf(
        "username" desc "아이디",
        "password" desc "패스워드",
        "nickname" desc "닉네임",
        "allowPush" desc "알림 여부"
    )

    private val userResponseFields = listOf(
        "id" desc "식별자",
        "username" desc "아이디",
        "nickname" desc "닉네임",
        "role" desc "권한",
        "allowPush" desc "알림 여부",
        "answerRate" desc "정답률",
        "correctQuizIds" desc "맞은 퀴즈 리스트",
        "incorrectQuizIds" desc "틀린 퀴즈 리스트",
        "likedQuizIds" desc "좋아요한 퀴즈 리스트",
        "createdDate" desc "가입 날짜"
    )

    private val userResponsesFields = userResponseFields.map { "[].${it.path}" desc it.description as String }

    private val getPasswordByUsernameResponseFields = listOf(
        "password" desc "패스워드",
    )

    init {
        describe("getUsers()은") {
            context("요청이 주어지면") {
                coEvery { userService.getUsers() } returns flowOf(createUserResponse())

                it("상태 코드 200과 userResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/user")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 전체 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                responseFields(userResponsesFields)
                            )
                        )
                }
            }
        }

        describe("getUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                coEvery { userService.getUserById(any()) } returns createUserResponse()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(UserResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 유저 단일 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(userResponseFields)
                            )
                        )
                }
            }


            context("존재하지 않는 유저에 대한 식별자가 주어지면") {
                coEvery { userService.getUserById(any()) } throws UserNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "식별자를 통한 유저 단일 조회 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("getUserByUsername()은") {
            context("존재하는 유저에 대한 아이디가 주어지면") {
                coEvery { userService.getUserByUsername(any()) } returns createUserResponse()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(UserResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 유저 단일 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                responseFields(userResponseFields)
                            )
                        )
                }
            }

            context("존재하지 않는 유저에 대한 아이디가 주어지면") {
                coEvery { userService.getUserByUsername(any()) } throws UserNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 유저 단일 조회 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("getPasswordByUsername()는") {
            context("존재하는 유저에 대한 아이디가 주어지면") {
                coEvery {
                    userService.getPasswordByUsername(any())
                } returns createGetPasswordByUsernameResponse()

                it("상태 코드 200과 getPasswordByUsernameResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}/password", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(GetPasswordByUsernameResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 패스워드 조회 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                responseFields(getPasswordByUsernameResponseFields)
                            )
                        )
                }
            }

            context("존재하지 않는 유저에 대한 아이디가 주어지면") {
                coEvery { userService.getPasswordByUsername(any()) } throws UserNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/username/{username}/password", USERNAME)
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 패스워드 조회 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }


        describe("createUser()는") {
            context("존재하지 않는 아이디가 주어지면") {
                coEvery { userService.createUser(any()) } returns createUserResponse()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user")
                        .bodyValue(createCreateUserRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(UserResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "회원가입 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createUserRequestFields),
                                responseFields(userResponseFields)
                            )
                        )
                }
            }

            context("이미 존재하는 아이디가 주어지면") {
                coEvery { userService.createUser(any()) } throws UsernameAlreadyExistException()

                it("상태 코드 409와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user")
                        .bodyValue(createCreateUserRequest())
                        .exchange()
                        .expectStatus()
                        .is4xxClientError
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "회원가입 실패(409)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(createUserRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }
    }
}