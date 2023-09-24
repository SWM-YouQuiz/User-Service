package com.quizit.user.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.MatchPasswordRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.MatchPasswordResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.PasswordNotMatchException
import com.quizit.user.exception.PermissionDeniedException
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.global.config.isAdmin
import com.quizit.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getRanking(): Flux<UserResponse> =
        userRepository.findAllOrderByCorrectQuizIdsSize()
            .map { UserResponse(it) }

    fun getUserById(id: String): Mono<UserResponse> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun getUserByUsername(username: String): Mono<UserResponse> =
        userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun matchPassword(username: String, request: MatchPasswordRequest): Mono<MatchPasswordResponse> =
        with(request) {
            userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(UserNotFoundException()))
                .map { MatchPasswordResponse(passwordEncoder.matches(password, it.password)) }
        }

    fun createUser(request: CreateUserRequest): Mono<UserResponse> =
        with(request) {
            userRepository.findByUsername(username)
                .flatMap { Mono.error<User>(UsernameAlreadyExistException()) }
                .defaultIfEmpty(
                    User(
                        username = username,
                        password = passwordEncoder.encode(password),
                        nickname = nickname,
                        image = image,
                        level = 1,
                        role = Role.USER,
                        allowPush = allowPush,
                        dailyTarget = dailyTarget,
                        answerRate = 0.0,
                        correctQuizIds = hashSetOf(),
                        incorrectQuizIds = hashSetOf(),
                        markedQuizIds = hashSetOf(),
                    )
                )
                .flatMap { userRepository.save(it) }
                .map { UserResponse(it) }
        }

    fun updateUserById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateUserByIdRequest
    ): Mono<UserResponse> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .filter { (authentication.id == it.id) || authentication.isAdmin() }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .map { request.run { it.update(nickname, image, allowPush, dailyTarget) } }
            .flatMap { userRepository.save(it) }
            .map { UserResponse(it) }

    fun changePassword(
        id: String, authentication: DefaultJwtAuthentication, request: ChangePasswordRequest
    ): Mono<Void> =
        with(request) {
            userRepository.findById(id)
                .switchIfEmpty(Mono.error(UserNotFoundException()))
                .filter { (authentication.id == it.id) || authentication.isAdmin() }
                .switchIfEmpty(Mono.error(PermissionDeniedException()))
                .filter { passwordEncoder.matches(password, it.password) }
                .switchIfEmpty(Mono.error(PasswordNotMatchException()))
                .map { it.updatePassword(passwordEncoder.encode(newPassword)) }
                .flatMap { userRepository.save(it) }
                .then()
        }

    fun deleteUserById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .filter { (authentication.id == it.id) || authentication.isAdmin() }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { userRepository.deleteById(id) }
}