package com.quizit.user.exception

import com.quizit.user.global.exception.ServerException

class OAuthNotExistPasswordException(
    override val message: String = "패스워드가 존재하지 않습니다."
) : ServerException(code = 404, message)