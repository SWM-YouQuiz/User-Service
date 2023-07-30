package com.youquiz.user.handler

import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService
) {
    suspend fun findAll(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyAndAwait(userService.findAll())

    suspend fun createUser(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateUserRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(userService.createUser(it))
        }

    suspend fun findById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyValueAndAwait(userService.findById(it))
        }

    suspend fun findByUsername(request: ServerRequest): ServerResponse =
        request.pathVariable("username").let {
            ServerResponse.ok().bodyValueAndAwait(userService.findByUsername(it))
        }
}