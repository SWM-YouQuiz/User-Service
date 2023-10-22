package com.quizit.user.adapter.consumer

import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.DeleteQuizEvent
import com.quizit.user.dto.event.MarkQuizEvent
import com.quizit.user.global.config.consumerLogging
import com.quizit.user.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Component

@Consumer
class UserConsumer(
    private val deleteQuizConsumer: ReactiveKafkaConsumerTemplate<String, DeleteQuizEvent>,
    private val markQuizConsumer: ReactiveKafkaConsumerTemplate<String, MarkQuizEvent>,
    private val checkAnswerConsumer: ReactiveKafkaConsumerTemplate<String, CheckAnswerEvent>,
    private val userRepository: UserRepository,
) {
    @PostConstruct
    fun deleteQuiz() {
        deleteQuizConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findAll()
                        .map {
                            it.apply {
                                correctQuizIds.remove(quizId)
                                incorrectQuizIds.remove(quizId)
                            }
                        }
                        .let {
                            userRepository.saveAll(it)
                        }
                        .subscribe()
                }
            }
            .doOnNext { consumerLogging(it) }
            .subscribe()
    }

    @PostConstruct
    fun markQuiz() {
        markQuizConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findById(userId)
                        .map {
                            it.apply {
                                if (isMarked) {
                                    markQuiz(quizId)
                                } else {
                                    unmarkQuiz(quizId)
                                }
                            }
                        }
                        .flatMap { userRepository.save(it) }
                        .subscribe()
                }
            }
            .doOnNext { consumerLogging(it) }
            .subscribe()
    }

    @PostConstruct
    fun checkAnswer() {
        checkAnswerConsumer.receiveAutoAck()
            .doOnNext { message ->
                with(message.value()) {
                    userRepository.findById(userId)
                        .filter { (quizId !in it.correctQuizIds) && (quizId !in it.incorrectQuizIds) }
                        .map {
                            it.apply {
                                if (isAnswer) {
                                    correctAnswer(quizId)
                                    checkLevel()
                                } else {
                                    incorrectAnswer(quizId)
                                }
                            }
                        }
                        .flatMap { userRepository.save(it) }
                        .subscribe()
                }
            }
            .doOnNext { consumerLogging(it) }
            .subscribe()
    }
}