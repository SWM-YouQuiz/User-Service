package com.quizit.user.adapter.client

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.dto.response.QuizResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
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
                    .uri("$url/api/quiz/quiz/course/{id}", courseId)
                    .header(
                        HttpHeaders.AUTHORIZATION, "Bearer ${(it.authentication as DefaultJwtAuthentication).token}"
                    )
                    .retrieve()
                    .bodyToFlux<QuizResponse>()
            }
}