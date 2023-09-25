package com.quizit.user.global.config

import com.github.jwt.authentication.DefaultJwtAuthentication
import com.github.jwt.authentication.JwtAuthenticationFilter
import com.github.jwt.core.JwtProvider
import com.quizit.user.domain.enum.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: ServerHttpSecurity, jwtProvider: JwtProvider): SecurityWebFilterChain =
        with(http) {
            csrf { it.disable() }
            formLogin { it.disable() }
            logout { it.disable() }
            httpBasic { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            authorizeExchange {
                it.pathMatchers("/user/admin/**")
                    .hasAuthority(Role.ADMIN.name)
                    .pathMatchers(
                        "/actuator/health/**",
                        "/user",
                        "/user/username/{username}",
                        "/user/username/{username}/match-password"
                    )
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            addFilterAt(JwtAuthenticationFilter(jwtProvider), SecurityWebFiltersOrder.AUTHORIZATION)
            build()
        }
}

fun ServerRequest.authentication(): Mono<DefaultJwtAuthentication> =
    principal()
        .cast(DefaultJwtAuthentication::class.java)

fun DefaultJwtAuthentication.isAdmin(): Boolean =
    isAuthenticated && (authorities[0] == SimpleGrantedAuthority(Role.ADMIN.name))