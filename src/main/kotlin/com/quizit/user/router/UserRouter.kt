package com.quizit.user.router

import com.quizit.user.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter {
    @Bean
    fun userRoutes(handler: UserHandler): RouterFunction<ServerResponse> =
        coRouter {
            "/user".nest {
                GET("", handler::getUsers)
                GET("/{id}", handler::getUserById)
                GET("/username/{username}", handler::getUserByUsername)
                GET("/username/{username}/password", handler::getPasswordByUsername)
                POST("", handler::createUser)
                PUT("/{id}", handler::updateUserById)
                PUT("/{id}/password", handler::changePassword)
                DELETE("/{id}", handler::deleteUserById)
            }
        }
}