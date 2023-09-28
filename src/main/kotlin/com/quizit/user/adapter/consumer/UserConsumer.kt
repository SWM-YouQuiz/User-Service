package com.quizit.user.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.MarkQuizEvent
import com.quizit.user.repository.UserRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@OptIn(DelicateCoroutinesApi::class)
@Component
class UserConsumer(
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {
    @KafkaListener(id = "check-answer", topics = ["check-answer"])
    fun checkAnswer(
        @Payload
        message: String
    ) = with(objectMapper.readValue(message, CheckAnswerEvent::class.java)) {
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

    @KafkaListener(id = "mark-quiz", topics = ["mark-quiz"])
    fun markQuiz(
        @Payload
        message: String
    ) =
        with(objectMapper.readValue(message, MarkQuizEvent::class.java)) {
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