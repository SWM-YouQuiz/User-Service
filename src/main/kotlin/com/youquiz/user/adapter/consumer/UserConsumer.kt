package com.youquiz.user.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.youquiz.user.event.CorrectAnswerEvent
import com.youquiz.user.event.IncorrectAnswerEvent
import com.youquiz.user.repository.UserRepository
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
    @KafkaListener(id = "correct-answer", topics = ["correct-answer"])
    fun correctAnswer(
        @Payload
        message: String
    ) = GlobalScope.launch {
        objectMapper.readValue(message, CorrectAnswerEvent::class.java).run {
            userRepository.findById(userId)!!.let {
                if ((quizId !in it.correctQuizIds) and (quizId !in it.incorrectQuizIds)) {
                    it.correctAnswer(quizId)
                    userRepository.save(it)
                }
            }
        }
    }

    @KafkaListener(id = "incorrect-answer", topics = ["incorrect-answer"])
    fun incorrectAnswer(
        @Payload
        message: String
    ) = GlobalScope.launch {
        objectMapper.readValue(message, IncorrectAnswerEvent::class.java).run {
            userRepository.findById(userId)!!.let {
                if ((quizId !in it.correctQuizIds) and (quizId !in it.incorrectQuizIds)) {
                    it.incorrectAnswer(quizId)
                    userRepository.save(it)
                }
            }
        }
    }
}