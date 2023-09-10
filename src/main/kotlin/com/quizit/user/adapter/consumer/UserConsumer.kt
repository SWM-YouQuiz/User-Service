package com.quizit.user.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.MarkQuizEvent
import com.quizit.user.repository.UserRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    ) = GlobalScope.launch {
        objectMapper.readValue(message, CheckAnswerEvent::class.java).run {
            userRepository.findById(userId)!!.let {
                if ((quizId !in it.correctQuizIds) and (quizId !in it.incorrectQuizIds)) {
                    it.correctAnswer(quizId)
                    userRepository.save(it)
                }
            }
        }
    }

    @KafkaListener(id = "mark-quiz", topics = ["mark-quiz"])
    fun markQuiz(
        @Payload
        message: String
    ) = GlobalScope.launch {
        objectMapper.readValue(message, MarkQuizEvent::class.java).run {
            userRepository.findById(userId)!!.let {
                if (isMarked) {
                    it.markQuiz(quizId)
                } else {
                    it.unmarkQuiz(quizId)
                }
                userRepository.save(it)
            }
        }
    }
}