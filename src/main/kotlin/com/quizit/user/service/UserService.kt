package com.quizit.user.service

import com.quizit.user.domain.User
import com.quizit.user.domain.enum.Role
import com.quizit.user.dto.request.CreateUserRequest
import com.quizit.user.dto.response.GetPasswordByUsernameResponse
import com.quizit.user.dto.response.UserResponse
import com.quizit.user.exception.UserNotFoundException
import com.quizit.user.exception.UsernameAlreadyExistException
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

    suspend fun getUserById(id: String): UserResponse =
        userRepository.findById(id)?.let { UserResponse(it) } ?: throw UserNotFoundException()

    suspend fun getUserByUsername(username: String): UserResponse =
        userRepository.findByUsername(username)?.let { UserResponse(it) } ?: throw UserNotFoundException()

    suspend fun getPasswordByUsername(username: String): GetPasswordByUsernameResponse =
        userRepository.findByUsername(username)?.let { GetPasswordByUsernameResponse(it) }
            ?: throw UserNotFoundException()
}