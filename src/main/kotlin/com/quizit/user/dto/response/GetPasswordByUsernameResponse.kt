package com.quizit.user.dto.response

import com.quizit.user.domain.User

data class GetPasswordByUsernameResponse(
    val password: String
) {
    companion object {
        operator fun invoke(user: User): GetPasswordByUsernameResponse =
            with(user) { GetPasswordByUsernameResponse(password) }
    }
}