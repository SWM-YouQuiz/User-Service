package com.quizit.user.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.adapter.client.QuizClient
import com.quizit.user.adapter.producer.UserProducer
import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.event.DeleteUserEvent
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.MatchPasswordRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.MatchPasswordResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.*
import com.quizit.user.global.util.isAdmin
import com.quizit.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val quizClient: QuizClient,
    private val userProducer: UserProducer,
    private val passwordEncoder: PasswordEncoder
) {
    fun getRanking(): Flux<UserResponse> =
        userRepository.findAllOrderByCorrectQuizIdsSize()
            .map { UserResponse(it) }

    fun getRankingByCourseId(courseId: String): Flux<UserResponse> =
        quizClient.getQuizzesByCourseId(courseId)
            .map { it.id }
            .collectList()
            .flatMapMany { userRepository.findAllOrderByCorrectQuizIdsSizeInQuizIds(it) }
            .map { UserResponse(it) }

    fun getUserById(id: String): Mono<UserResponse> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun getUserByUsername(username: String): Mono<UserResponse> =
        userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun getUserByEmailAndProvider(email: String, provider: Provider): Mono<UserResponse> =
        userRepository.findByEmailAndProvider(email, provider)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map { UserResponse(it) }

    fun createUser(request: CreateUserRequest): Mono<UserResponse> =
        with(request) {
            userRepository.findByUsername(username)
                .flatMap { Mono.error<User>(UsernameAlreadyExistException()) }
                .defaultIfEmpty(
                    User(
                        username = username,
                        password = password?.run(passwordEncoder::encode),
                        nickname = nickname,
                        image = image,
                        level = 1,
                        role = Role.USER,
                        allowPush = allowPush,
                        dailyTarget = dailyTarget,
                        answerRate = 0.0,
                        provider = provider,
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

    fun deleteUserById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .filter { (authentication.id == it.id) || authentication.isAdmin() }
            .switchIfEmpty(Mono.error(PermissionDeniedException()))
            .flatMap { userRepository.deleteById(id) }
            .then(Mono.defer { userProducer.deleteUser(DeleteUserEvent(id)) })
}