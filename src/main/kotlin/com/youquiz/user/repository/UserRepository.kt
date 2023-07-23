package com.youquiz.user.repository

import com.youquiz.user.domain.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun findFirstByUsername(username: String): User?
}