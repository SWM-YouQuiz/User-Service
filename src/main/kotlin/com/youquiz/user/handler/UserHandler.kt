package com.youquiz.user.handler

import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService
) {
    suspend fun createUser(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateUserRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(userService.createUser(it))
        }
}