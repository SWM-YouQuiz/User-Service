package com.quizit.user.repository

import com.quizit.user.domain.User
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByUsername(username: String): Mono<User>

    @Aggregation(
        pipeline = [
            "{ \$addFields: { quizCount: { \$size: '\$correctQuizIds' } }}",
            "{ \$sort: { quizCount: -1 } }"
        ]
    )
    fun findAllOrderByCorrectQuizIdsSize(): Flux<User>
}