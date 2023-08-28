package com.quizit.user.handler

import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.global.config.awaitAuthentication
import com.quizit.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService
) {
    suspend fun getUsers(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyAndAwait(userService.getUsers())

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

    suspend fun createUser(request: ServerRequest): ServerResponse =
        request.awaitBody<CreateUserRequest>().let {
            ServerResponse.ok().bodyValueAndAwait(userService.createUser(it))
        }

    suspend fun updateUserById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val authentication = awaitAuthentication()
            val updateUserByIdRequest = awaitBody<UpdateUserByIdRequest>()

            ServerResponse.ok().bodyValueAndAwait(userService.updateUserById(id, authentication, updateUserByIdRequest))
        }

    suspend fun changePassword(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val authentication = awaitAuthentication()
            val changePasswordRequest = awaitBody<ChangePasswordRequest>()

            userService.changePassword(id, authentication, changePasswordRequest)

            ServerResponse.ok().buildAndAwait()
        }

    suspend fun deleteUserById(request: ServerRequest): ServerResponse =
        with(request) {
            val id = pathVariable("id")
            val authentication = awaitAuthentication()

            userService.deleteUserById(id, authentication)

            ServerResponse.ok().buildAndAwait()
        }
}