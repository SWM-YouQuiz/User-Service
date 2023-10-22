package com.quizit.user.exception

import com.quizit.user.global.exception.ServerException

data class OAuthLoginException(
    override val message: String = "소셜 로그인 유저는 일반 로그인을 이용할 수 없습니다."
) : ServerException(code = 400, message)