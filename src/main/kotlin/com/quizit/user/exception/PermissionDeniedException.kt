package com.quizit.user.exception

import com.quizit.user.global.exception.ServerException

class PermissionDeniedException(
    override val message: String = "권한이 없습니다."
) : ServerException(code = 403, message)