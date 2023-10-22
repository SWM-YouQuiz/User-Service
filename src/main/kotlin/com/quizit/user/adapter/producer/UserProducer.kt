package com.quizit.user.adapter.producer

import com.quizit.user.dto.event.DeleteUserEvent
import com.quizit.user.global.annotation.Producer
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono

@Producer
class UserProducer(
    private val kafkaTemplate: ReactiveKafkaProducerTemplate<String, Any>,
) {
    fun deleteUser(event: DeleteUserEvent): Mono<Void> =
        kafkaTemplate.send("delete-user", event)
            .then()
}