package com.lightningkite.kotlinx.server.base


data class HttpError(
        var code: Int,
        var message: String
)

