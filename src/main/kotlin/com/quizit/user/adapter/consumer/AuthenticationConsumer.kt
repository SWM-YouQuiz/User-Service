package com.quizit.user.adapter.consumer

import com.quizit.user.adapter.producer.UserProducer
import com.quizit.user.dto.event.DeleteUserEvent
import com.quizit.user.dto.event.RevokeOAuthEvent
import com.quizit.user.global.annotation.Consumer
import com.quizit.user.repository.UserRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Consumer
class AuthenticationConsumer(
    private val revokeOAuthConsumer: ReactiveKafkaConsumerTemplate<String, RevokeOAuthEvent>,
    private val userProducer: UserProducer,
    private val userRepository: UserRepository
) {
    @EventListener(ApplicationStartedEvent::class)
    fun revokeOAuth(): Flux<ConsumerRecord<String, RevokeOAuthEvent>> =
        revokeOAuthConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findByEmailAndProvider(email, provider)
                        .flatMap {
                            Mono.zip(
                                userRepository.deleteById(it.id!!)
                                    .subscribeOn(Schedulers.boundedElastic()),
                                userProducer.deleteUser(DeleteUserEvent(it.id!!))
                                    .subscribeOn(Schedulers.boundedElastic())
                            )
                        }
                        .subscribe()
                }
            }
}