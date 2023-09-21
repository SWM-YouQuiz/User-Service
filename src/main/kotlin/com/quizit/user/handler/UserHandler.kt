package com.quizit.user.handler

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.MatchPasswordRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Component
class UserHandler(
    private val userService: UserService
) {
    fun getRanking(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(userService.getRanking())

    fun getUserById(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(userService.getUserById(request.pathVariable("id")))

    fun getUserByUsername(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .body(userService.getUserByUsername(request.pathVariable("username")))

    fun matchPassword(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<MatchPasswordRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(userService.matchPassword(pathVariable("username"), it))
                }
        }

    fun createUser(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            bodyToMono<CreateUserRequest>()
                .flatMap {
                    ServerResponse.ok()
                        .body(userService.createUser(it))
                }
        }

    fun updateUserById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(principal(), bodyToMono<UpdateUserByIdRequest>())
                .flatMap {
                    ServerResponse.ok()
                        .body(userService.updateUserById(pathVariable("id"), it.t1 as DefaultJwtAuthentication, it.t2))
                }
        }

    fun changePassword(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            Mono.zip(principal(), bodyToMono<ChangePasswordRequest>())
                .flatMap {
                    ServerResponse.ok()
                        .body(userService.changePassword(pathVariable("id"), it.t1 as DefaultJwtAuthentication, it.t2))
                }
        }

    fun deleteUserById(request: ServerRequest): Mono<ServerResponse> =
        with(request) {
            principal()
                .flatMap {
                    ServerResponse.ok()
                        .body(userService.deleteUserById(pathVariable("id"), it as DefaultJwtAuthentication))
                }
        }
}