package com.youquiz.user.global.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@EnableR2dbcAuditing
@EnableR2dbcRepositories
@Configuration
class R2dbcConfiguration{
    @Bean
    fun connectionFactoryInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer()
            .apply {
                setConnectionFactory(connectionFactory)
                setDatabasePopulator(
                    ResourceDatabasePopulator(
                        ClassPathResource("scripts/schema.sql")
                    )
                )
            }

    @Bean
    fun transactionManger(connectionFactory: ConnectionFactory): R2dbcTransactionManager = R2dbcTransactionManager(connectionFactory)
}