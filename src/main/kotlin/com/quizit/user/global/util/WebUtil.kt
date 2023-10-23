package com.quizit.user.global.util

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.quizit.user.domain.enum.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast

fun ServerRequest.authentication(): Mono<DefaultJwtAuthentication> =
    principal()
        .cast<DefaultJwtAuthentication>()

fun DefaultJwtAuthentication.isAdmin(): Boolean =
    isAuthenticated && (authorities[0] == SimpleGrantedAuthority(Role.ADMIN.name))