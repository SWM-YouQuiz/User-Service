package com.quizit.user.global.config

import com.quizit.user.dto.event.CheckAnswerEvent
import com.quizit.user.dto.event.DeleteQuizEvent
import com.quizit.user.dto.event.MarkQuizEvent
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class ProducerConfiguration(
    private val properties: KafkaProperties
) {
    @Bean
    fun reactiveKafkaProducerTemplate(): ReactiveKafkaProducerTemplate<String, Any> =
        ReactiveKafkaProducerTemplate(
            SenderOptions.create<String, Any>(properties.buildProducerProperties())
                .withValueSerializer(JsonSerializer())
        )
}

@Configuration
class ConsumerConfiguration(
    private val properties: KafkaProperties
) {
    @Bean
    fun deleteQuizConsumer(): ReactiveKafkaConsumerTemplate<String, DeleteQuizEvent> =
        ReactiveKafkaConsumerTemplate(createReceiverOptions("delete-quiz"))

    @Bean
    fun markQuizConsumer(): ReactiveKafkaConsumerTemplate<String, MarkQuizEvent> =
        ReactiveKafkaConsumerTemplate(createReceiverOptions("mark-quiz"))

    @Bean
    fun checkAnswerConsumer(): ReactiveKafkaConsumerTemplate<String, CheckAnswerEvent> =
        ReactiveKafkaConsumerTemplate(createReceiverOptions("check-answer"))

    private inline fun <reified T> createReceiverOptions(topic: String): ReceiverOptions<String, T> =
        ReceiverOptions.create<String, T>(
            properties.run {
                consumer.groupId = "$topic-group"
                buildConsumerProperties()
            })
            .subscription(listOf(topic))
            .withValueDeserializer(JsonDeserializer<T>(T::class.java, false))
}