package com.youquiz.user.dto

import com.youquiz.user.domain.User

data class GetPasswordByUsernameResponse(
    val password: String
) {
    companion object {
        operator fun invoke(user: User): GetPasswordByUsernameResponse =
            with(user) { GetPasswordByUsernameResponse(password) }
    }
}