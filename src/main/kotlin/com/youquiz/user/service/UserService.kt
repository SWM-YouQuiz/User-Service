package com.youquiz.user.service

import com.youquiz.user.domain.User
import com.youquiz.user.domain.enum.Role
import com.youquiz.user.dto.CreateUserRequest
import com.youquiz.user.dto.UserResponse
import com.youquiz.user.exception.UserNotFoundException
import com.youquiz.user.exception.UsernameAlreadyExistException
import com.youquiz.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun createUser(request: CreateUserRequest): UserResponse =
        with(request) {
            userRepository.findFirstByUsername(username)?.run { throw UsernameAlreadyExistException() }

            userRepository.save(
                User(
                    username = username,
                    password = passwordEncoder.encode(password),
                    nickname = nickname,
                    role = Role.USER,
                    allowPush = allowPush
                )
            ).let { UserResponse(it) }
        }

    suspend fun findById(id: Long): UserResponse =
        userRepository.findById(id)?.let { UserResponse(it) } ?: throw UserNotFoundException()

    suspend fun findByUsername(username: String): UserResponse =
        userRepository.findByUsername(username)?.let { UserResponse(it) } ?: throw UserNotFoundException()
}