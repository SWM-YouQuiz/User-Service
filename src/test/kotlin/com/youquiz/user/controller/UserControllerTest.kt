package com.youquiz.user.controller

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.ninjasquad.springmockk.MockkBean
import com.youquiz.user.config.SecurityTestConfiguration
import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.dto.UserResponse
import com.youquiz.user.exception.UsernameAlreadyExistException
import com.youquiz.user.fixture.*
import com.youquiz.user.global.dto.ErrorResponse
import com.youquiz.user.handler.UserHandler
import com.youquiz.user.router.UserRouter
import com.youquiz.user.service.UserService
import com.youquiz.user.util.BaseControllerTest
import com.youquiz.user.util.desc
import com.youquiz.user.util.errorResponseFields
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SecurityTestConfiguration::class])
@WebFluxTest(UserRouter::class, UserHandler::class)
class UserControllerTest : BaseControllerTest() {
    @MockkBean
    private lateinit var userService: UserService

    private val createUserRequest = CreateUserRequest(
        username = USERNAME,
        password = PASSWORD,
        nickname = NICKNAME,
        allowPush = ALLOW_PUSH
    )

    private val userResponse = UserResponse(createUser())

    private val createUserRequestFields = listOf(
        "username" desc "아이디",
        "password" desc "패스워드",
        "nickname" desc "닉네임",
        "allowPush" desc "알림 여부"
    )

    private val userResponseFields = listOf(
        "id" desc "식별자",
        "username" desc "아이디",
        "password" desc "패스워드",
        "nickname" desc "닉네임",
        "role" desc "권한",
        "allowPush" desc "알림 여부",
        "createdDate" desc "가입 날짜"
    )

    private val userResponsesFields = userResponseFields.map { "[].${it.path}" desc it.description as String }

    init {
        describe("createUser()는") {
            context("존재하지 않는 아이디가 주어지면") {
                coEvery { userService.createUser(any()) } returns userResponse

                it("상태 코드 200과 userResponse를 반환한다.") {
                    webClient
                        .post()
                        .uri("/user")
                        .bodyValue(createUserRequest)
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
                        .bodyValue(createUserRequest)
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