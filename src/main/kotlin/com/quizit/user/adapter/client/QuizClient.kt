package com.quizit.user.adapter.client

import com.quizit.user.dto.response.QuizResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux

@Component
class QuizClient(
    private val webClient: WebClient,
    @Value("\${url.service.quiz}")
    private val url: String
) {
    fun getQuizzesByCourseId(courseId: String): Flux<QuizResponse> =
        ReactiveSecurityContextHolder.getContext()
            .flatMapMany {
                webClient.get()
                    .uri("$url/quiz/course/{id}", courseId)
                    .retrieve()
                    .bodyToFlux<QuizResponse>()
            }
}