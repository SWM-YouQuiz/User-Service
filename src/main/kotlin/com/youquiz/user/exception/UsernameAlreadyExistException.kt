package com.youquiz.user.exception

import com.youquiz.user.global.exception.ServerException

class UsernameAlreadyExistException(
    override val message: String = "이미 존재하는 아이디입니다."
) : ServerException(code = 409, message)