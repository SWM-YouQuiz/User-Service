package com.quizit.user.global.config

import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.MarkQuizEvent
import com.quizit.user.global.util.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class KafkaConfiguration(
    private val properties: KafkaProperties,
) {
    private inline fun <reified T> receiverOptions(topic: String): ReceiverOptions<String, T> =
        ReceiverOptions.create<String, T>(
            properties.run {
                consumer.groupId = "$topic-group"
                buildConsumerProperties()
            })
            .subscription(listOf(topic))
            .withValueDeserializer(JsonDeserializer<T>(T::class.java, false))

    @Bean
    fun markQuizConsumer(): ReactiveKafkaConsumerTemplate<String, MarkQuizEvent> =
        ReactiveKafkaConsumerTemplate(receiverOptions("mark-quiz"))

    @Bean
    fun checkAnswerConsumer(): ReactiveKafkaConsumerTemplate<String, CheckAnswerEvent> =
        ReactiveKafkaConsumerTemplate(receiverOptions("check-answer"))
}

fun consumerLogging(consumerRecord: ConsumerRecord<String, out Any>) {
    with(consumerRecord.value()) {
        logger.info { "Successfully consumed ${this::class.simpleName}: $this" }
    }
}