package com.quizit.user.global.aspect

import com.quizit.user.global.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

@Aspect
@Component
class ProducerAspect {
    @Around("@within(com.quizit.user.global.annotation.Producer)")
    fun around(joinPoint: ProceedingJoinPoint): Mono<Void> =
        (joinPoint.proceed() as Mono<*>)
            .cast<Void>()
            .doOnNext {
                joinPoint.args[0]
                    .let { event -> logger.info { "Successfully produced ${event::class.simpleName}: $event" } }
            }
}