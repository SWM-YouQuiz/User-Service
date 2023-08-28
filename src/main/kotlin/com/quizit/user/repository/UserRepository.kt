package com.quizit.user.repository

import com.quizit.user.domain.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByUsername(username: String): User?

    @Aggregation(
        pipeline = [
            "{ \$addFields: { quizCount: { \$size: '\$correctQuizIds' } }}",
            "{ \$sort: { quizCount: -1 } }"
        ]
    )
    fun findAllOrderByCorrectQuizIdsSize(): Flow<User>
}