package org.blipay.exception

import org.springframework.http.HttpStatus

class ValidationException(
    override val message: String?,
    override val cause: Throwable? = null,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException()