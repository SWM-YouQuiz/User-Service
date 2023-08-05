package com.youquiz.user.adapter.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.youquiz.user.dto.event.CheckAnswerEvent
import com.youquiz.user.dto.event.LikeQuizEvent
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
    @KafkaListener(id = "check-answer", topics = ["check-answer"])
    fun checkQuiz(
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

    @KafkaListener(id = "like-quiz", topics = ["like-quiz"])
    fun likeQuiz(
        @Payload
        message: String
    ) = GlobalScope.launch {
        objectMapper.readValue(message, LikeQuizEvent::class.java).run {
            userRepository.findById(userId)!!.let {
                if (isLike) {
                    it.likeQuiz(quizId)
                } else {
                    it.unlikeQuiz(quizId)
                }
                userRepository.save(it)
            }
        }
    }
}