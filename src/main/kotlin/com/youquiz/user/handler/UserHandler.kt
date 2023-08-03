package com.youquiz.user.handler

import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService
) {
    suspend fun getUsers(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyAndAwait(userService.getUsers())

    suspend fun createUser(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateUserRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(userService.createUser(it))
        }

    suspend fun getUserById(request: ServerRequest): ServerResponse =
        request.pathVariable("id").let {
            ServerResponse.ok().bodyValueAndAwait(userService.getUserById(it))
        }

    suspend fun getUserByUsername(request: ServerRequest): ServerResponse =
        request.pathVariable("username").let {
            ServerResponse.ok().bodyValueAndAwait(userService.getUserByUsername(it))
        }

    suspend fun getPasswordByUsername(request: ServerRequest): ServerResponse =
        request.pathVariable("username").let {
            ServerResponse.ok().bodyValueAndAwait(userService.getPasswordByUsername(it))
        }

}