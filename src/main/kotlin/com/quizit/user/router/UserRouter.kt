package com.quizit.user.router

import com.quizit.user.global.annotation.Router
import com.quizit.user.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Router
class UserRouter {
    @Bean
    fun userRoutes(handler: UserHandler): RouterFunction<ServerResponse> =
        router {
            "/user".nest {
                GET("/ranking", handler::getRanking)
                GET("/ranking/course/{id}", handler::getRankingByCourseId)
                GET("/{id}", handler::getUserById)
                GET("/username/{username}", handler::getUserByUsername)
                POST("", handler::createUser)
                PUT("/{id}", handler::updateUserById)
                DELETE("/{id}", handler::deleteUserById)
            }
        }
}