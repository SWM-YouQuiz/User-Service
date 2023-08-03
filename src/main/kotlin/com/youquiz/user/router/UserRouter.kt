package com.youquiz.user.router

import com.youquiz.user.handler.UserHandler
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
            "/api/user".nest {
                GET("", handler::getUsers)
                GET("/{id}", handler::getUserById)
                GET("/username/{username}", handler::getUserByUsername)
                GET("/username/{username}/password", handler::getPasswordByUsername)
                POST("", handler::createUser)
            }
        }
}