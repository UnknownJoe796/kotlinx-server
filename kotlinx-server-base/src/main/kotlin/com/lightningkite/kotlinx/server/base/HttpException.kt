package com.lightningkite.kotlinx.server.base

class HttpException(val httpError: HttpError, cause: Throwable? = null) : Exception(httpError.message, cause)