package com.lightningkite.kotlinx.server.base

import com.lightningkite.kotlinx.reflection.allImplements
import com.lightningkite.kotlinx.reflection.kxReflect
import com.lightningkite.kotlinx.server.ServerFunction

fun HttpRequest.serverFunctionJson(transactionMaker: (request: HttpRequest, needsWrite: Boolean) -> Transaction) {
    val serverFunction = inputJson<ServerFunction<*>>() ?: run {
        respond(400, contentType = ContentType.Application.Json, data = byteArrayOf())
        return
    }
    val result = try {
        transactionMaker.invoke(this, serverFunction::class.requiresWrite).use {
            serverFunction.invoke(it)
        }
    } catch (e: Exception) {
        e
    }
    val endTypeRefl = serverFunction::class.kxReflect.allImplements.find { it.base.kclass == ServerFunction::class }!!.typeParameters.first().type
    respondJson(
            value = result,
            typeInfo = endTypeRefl,
            code = 200
    )
}