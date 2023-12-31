package com.quizit.user.adapter.consumer

import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.DeleteQuizEvent
import com.quizit.user.dto.event.MarkQuizEvent
import com.quizit.user.global.annotation.Consumer
import com.quizit.user.repository.UserRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux

@Consumer
class QuizConsumer(
    private val deleteQuizConsumer: ReactiveKafkaConsumerTemplate<String, DeleteQuizEvent>,
    private val markQuizConsumer: ReactiveKafkaConsumerTemplate<String, MarkQuizEvent>,
    private val checkAnswerConsumer: ReactiveKafkaConsumerTemplate<String, CheckAnswerEvent>,
    private val userRepository: UserRepository,
) {
    @EventListener(ApplicationStartedEvent::class)
    fun deleteQuiz(): Flux<ConsumerRecord<String, DeleteQuizEvent>> =
        deleteQuizConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findAll()
                        .doOnNext {
                            it.correctQuizIds.remove(quizId)
                            it.incorrectQuizIds.remove(quizId)
                        }
                        .let { userRepository.saveAll(it) }
                        .subscribe()
                }
            }

    @EventListener(ApplicationStartedEvent::class)
    fun markQuiz(): Flux<ConsumerRecord<String, MarkQuizEvent>> =
        markQuizConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findById(userId)
                        .doOnNext {
                            if (isMarked) {
                                it.markQuiz(quizId)
                            } else {
                                it.unmarkQuiz(quizId)
                            }
                        }
                        .flatMap { userRepository.save(it) }
                        .subscribe()
                }
            }

    @EventListener(ApplicationStartedEvent::class)
    fun checkAnswer(): Flux<ConsumerRecord<String, CheckAnswerEvent>> =
        checkAnswerConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findById(userId)
                        .filter { (quizId !in it.correctQuizIds) && (quizId !in it.incorrectQuizIds) }
                        .doOnNext {
                            if (isAnswer) {
                                it.correctAnswer(quizId)
                                it.checkLevel()
                            } else {
                                it.incorrectAnswer(quizId)
                            }
                        }
                        .flatMap { userRepository.save(it) }
                        .subscribe()
                }
            }
}