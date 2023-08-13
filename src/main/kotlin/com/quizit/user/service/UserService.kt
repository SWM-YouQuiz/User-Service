package com.quizit.user.service

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.ChangePasswordRequest
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.request.UpdateUserByIdRequest
import com.quizit.user.dto.response.GetPasswordByUsernameResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.PasswordNotMatchException
import com.quizit.user.exception.PermissionDeniedException
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
import com.quizit.user.global.config.isAdmin
import com.quizit.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getUsers(): Flow<UserResponse> =
        userRepository.findAll()
            .map { UserResponse(it) }

    suspend fun getUserById(id: String): UserResponse =
        userRepository.findById(id)?.let { UserResponse(it) } ?: throw UserNotFoundException()

    suspend fun getUserByUsername(username: String): UserResponse =
        userRepository.findByUsername(username)?.let { UserResponse(it) } ?: throw UserNotFoundException()

    suspend fun getPasswordByUsername(username: String): GetPasswordByUsernameResponse =
        userRepository.findByUsername(username)?.let { GetPasswordByUsernameResponse(it) }
            ?: throw UserNotFoundException()

    suspend fun createUser(request: CreateUserRequest): UserResponse =
        with(request) {
            userRepository.findByUsername(username)?.run { throw UsernameAlreadyExistException() }
            userRepository.save(
                User(
                    username = username,
                    password = passwordEncoder.encode(password),
                    nickname = nickname,
                    role = Role.USER,
                    allowPush = allowPush,
                    answerRate = 0.0,
                    correctQuizIds = mutableSetOf(),
                    incorrectQuizIds = mutableSetOf(),
                    likedQuizIds = mutableSetOf()
                )
            ).let { UserResponse(it) }
        }

    suspend fun updateUserById(
        id: String, authentication: DefaultJwtAuthentication, request: UpdateUserByIdRequest
    ): UserResponse =
        with(request) {
            userRepository.findById(id)?.let {
                if ((authentication.id == it.id) || authentication.isAdmin()) {
                    userRepository.save(
                        User(
                            username = it.username,
                            password = it.password,
                            nickname = nickname,
                            role = it.role,
                            allowPush = allowPush,
                            answerRate = it.answerRate,
                            correctQuizIds = it.correctQuizIds,
                            incorrectQuizIds = it.incorrectQuizIds,
                            likedQuizIds = it.likedQuizIds
                        )
                    )
                } else throw PermissionDeniedException()
            }?.let { UserResponse(it) } ?: throw UserNotFoundException()
        }

    suspend fun changePassword(
        id: String, authentication: DefaultJwtAuthentication, request: ChangePasswordRequest
    ) {
        with(request) {
            userRepository.findById(id)?.let {
                if ((authentication.id == it.id) || authentication.isAdmin()) {
                    if (passwordEncoder.matches(password, it.password)) {
                        userRepository.save(
                            User(
                                username = it.username,
                                password = passwordEncoder.encode(newPassword),
                                nickname = it.nickname,
                                role = it.role,
                                allowPush = it.allowPush,
                                answerRate = it.answerRate,
                                correctQuizIds = it.correctQuizIds,
                                incorrectQuizIds = it.incorrectQuizIds,
                                likedQuizIds = it.likedQuizIds
                            )
                        )
                    } else throw PasswordNotMatchException()
                } else throw PermissionDeniedException()
            }?.let { UserResponse(it) } ?: throw UserNotFoundException()
        }
    }

    suspend fun deleteUserById(id: String, authentication: DefaultJwtAuthentication) {
        userRepository.findById(id)?.let {
            if ((authentication.id == it.id) || authentication.isAdmin()) {
                userRepository.deleteById(id)
            } else throw PermissionDeniedException()
        } ?: throw UserNotFoundException()
    }
}