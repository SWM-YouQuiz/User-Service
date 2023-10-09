package com.quizit.user.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
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
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(UserRouter::class, UserHandler::class)
class UserControllerTest : BaseControllerTest() {
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

    private val matchPasswordRequestFields = listOf(
        "password" desc "패스워드"
    )

    private val updateUserByIdRequestFields = listOf(
        "nickname" desc "닉네임",
        "image" desc "프로필 사진",
        "allowPush" desc "알림 여부",
        "dailyTarget" desc "하루 목표",
    )

    private val changePasswordRequestFields = listOf(
        "password" desc "현재 패스워드",
        "newPassword" desc "새 패스워드"
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

    private val userResponsesFields = userResponseFields.map { "[].${it.path}" desc it.description as String }

    private val matchPasswordResponseFields = listOf(
        "isMatched" desc "패스워드 일치 여부",
    )

    init {
        describe("getRanking()은") {
            context("요청이 주어지면") {
                every { userService.getRanking() } returns Flux.just(createUserResponse())
                withMockUser()

                it("상태 코드 200과 랭킹 순서에 맞게 userResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/user/ranking")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(List::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 랭킹 조회 성공(200)",
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
                every { userService.getUserById(any()) } returns Mono.just(createUserResponse())
                withMockUser()

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
                every { userService.getUserById(any()) } throws UserNotFoundException()
                withMockUser()

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
                every { userService.getUserByUsername(any()) } returns Mono.just(createUserResponse())

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
                every { userService.getUserByUsername(any()) } throws UserNotFoundException()
                withMockUser()

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

        describe("createUser()는") {
            context("존재하지 않는 아이디가 주어지면") {
                every { userService.createUser(any()) } returns Mono.just(createUserResponse())
                withMockUser()

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

        describe("matchPassword()는") {
            context("존재하는 유저에 대한 아이디가 주어지면") {
                every { userService.matchPassword(any(), any()) } returns Mono.just(createMatchPasswordResponse())

                it("상태 코드 200과 matchPasswordResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user/username/{username}/match-password", USERNAME)
                        .bodyValue(createMatchPasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(MatchPasswordResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 패스워드 일치 확인 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                requestFields(matchPasswordRequestFields),
                                responseFields(matchPasswordResponseFields)
                            )
                        )
                }
            }

            context("존재하지 않는 유저에 대한 아이디가 주어지면") {
                every { userService.matchPassword(any(), any()) } throws UserNotFoundException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user/username/{username}/match-password", USERNAME)
                        .bodyValue(createMatchPasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 패스워드 일치 확인 실패(404 - 1)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                requestFields(matchPasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("소셜 로그인 유저에 대한 아이디가 주어지면") {
                every { userService.matchPassword(any(), any()) } throws OAuthNotExistPasswordException()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user/username/{username}/match-password", USERNAME)
                        .bodyValue(createMatchPasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "아이디를 통한 패스워드 일치 확인 실패(404 - 2)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("username" paramDesc "아이디"),
                                requestFields(matchPasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("updateUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.updateUserById(any(), any(), any()) } returns Mono.just(createUserResponse())
                withMockUser()

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}", ID)
                        .bodyValue(createUpdateUserByIdRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(UserResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 수정 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                requestFields(updateUserByIdRequestFields),
                                responseFields(userResponseFields)
                            )
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
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 수정 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(updateUserByIdRequestFields),
                                responseFields(errorResponseFields)
                            )
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
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 수정 실패(403)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(updateUserByIdRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("changePassword()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.changePassword(any(), any(), any()) } returns Mono.empty()
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}/password", ID)
                        .bodyValue(createChangePasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "패스워드 변경 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(changePasswordRequestFields),
                            )
                        )
                }
            }

            context("존재하지 않는 유저에 대한 식별자가 주어지면") {
                every { userService.changePassword(any(), any(), any()) } throws UserNotFoundException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}/password", ID)
                        .bodyValue(createChangePasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "패스워드 변경 실패(404 - 1)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(changePasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("본인이 아닌 다른 유저의 식별자가 주어지면") {
                every { userService.changePassword(any(), any(), any()) } throws PermissionDeniedException()
                withMockUser()

                it("상태 코드 403과 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}/password", ID)
                        .bodyValue(createChangePasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isForbidden
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "패스워드 변경 실패(403)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(changePasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("소셜 로그인 유저에 대한 식별자가 주어지면") {
                every { userService.changePassword(any(), any(), any()) } throws OAuthNotExistPasswordException()
                withMockUser()

                it("상태 코드 404와 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}/password", ID)
                        .bodyValue(createChangePasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isNotFound
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "패스워드 변경 실패(404 - 2)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(changePasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }

            context("현재 패스워드가 일치하지 않으면") {
                every { userService.changePassword(any(), any(), any()) } throws PasswordNotMatchException()
                withMockUser()

                it("상태 코드 400과 에러를 반환한다.") {
                    webClient.put()
                        .uri("/user/{id}/password", ID)
                        .bodyValue(createChangePasswordRequest())
                        .exchange()
                        .expectStatus()
                        .isBadRequest
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "패스워드 변경 실패(400)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                requestFields(changePasswordRequestFields),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }

        describe("deleteUserById()는") {
            context("존재하는 유저에 대한 식별자가 주어지면") {
                every { userService.deleteUserById(any(), any()) } returns Mono.empty()
                withMockUser()

                it("상태 코드 200을 반환한다.") {
                    webClient.delete()
                        .uri("/user/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody()
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 삭제 성공(200)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                            )
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
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 삭제 실패(404)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(errorResponseFields)
                            )
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
                        .expectBody(ErrorResponse::class.java)
                        .consumeWith(
                            WebTestClientRestDocumentationWrapper.document(
                                "유저 삭제 실패(403)",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                pathParameters("id" paramDesc "식별자"),
                                responseFields(errorResponseFields)
                            )
                        )
                }
            }
        }
    }
}