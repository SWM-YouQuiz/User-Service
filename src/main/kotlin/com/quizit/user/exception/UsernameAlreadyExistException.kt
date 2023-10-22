package com.quizit.user.exception

import com.quizit.user.global.exception.ServerException

data class UsernameAlreadyExistException(
    override val message: String = "이미 존재하는 아이디입니다."
) : ServerException(code = 409, message)