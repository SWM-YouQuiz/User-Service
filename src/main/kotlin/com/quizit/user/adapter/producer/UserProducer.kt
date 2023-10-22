package com.quizit.user.adapter.producer

import com.quizit.user.dto.event.DeleteUserEvent
import com.quizit.user.global.config.producerLogging
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Producer
class UserProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
) {
    fun deleteUser(event: DeleteUserEvent): Mono<Void> =
        kafkaTemplate.send("delete-user", event)
            .doOnNext { producerLogging(event) }
            .then()
}