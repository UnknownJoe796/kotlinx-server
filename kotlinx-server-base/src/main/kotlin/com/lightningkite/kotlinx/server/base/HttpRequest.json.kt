package com.lightningkite.kotlinx.server.base

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.iterator
import com.lightningkite.kotlinx.serialization.json.JsonParsingException
import com.lightningkite.kotlinx.serialization.json.JsonSerializer
import com.lightningkite.kotlinx.serialization.json.KlaxonException
import kotlin.reflect.KClass

@Throws(KlaxonException::class)
inline fun <reified T : Any> HttpRequest.inputJson(): T? {
    this.input.bufferedReader().use { reader ->
        val iter = reader.iterator()
        return JsonSerializer.read(T::class, null, iter)
    }
}

@Throws(KlaxonException::class)
inline fun <reified T : Any> HttpRequest.respondJson(
        value: T,
        code: Int = 200,
        headers: Map<String, List<String>> = mapOf(),
        addCookies: List<HttpCookie> = listOf()
) {
    val data = JsonSerializer.write(T::class, value = value).toString().toByteArray()
    this.respond(
            code = code,
            headers = headers,
            addCookies = addCookies,
            contentType = ContentType.Application.Json,
            data = data
    )
}

@Throws(KlaxonException::class)
fun <T : Any> HttpRequest.inputJson(type: KClass<T>): T? {
    this.input.bufferedReader().use { reader ->
        val iter = reader.iterator()
        return JsonSerializer.read(type, null, iter)
    }
}

@Throws(KlaxonException::class)
fun <T : Any> HttpRequest.respondJson(
        value: T?,
        type: KClass<T>,
        code: Int = 200,
        headers: Map<String, List<String>> = mapOf(),
        addCookies: List<HttpCookie> = listOf()
) {
    val data = JsonSerializer.write(type, null, value).toString().toByteArray()
    this.respond(
            code = code,
            headers = headers,
            addCookies = addCookies,
            contentType = ContentType.Application.Json,
            data = data
    )
}

@Throws(KlaxonException::class)
fun <T : Any> HttpRequest.inputJson(typeInfo: KxType): T? {
    this.input.bufferedReader().use { reader ->
        val iter = reader.iterator()
        @Suppress("UNCHECKED_CAST")
        return JsonSerializer.read(typeInfo.base.kclass as KClass<T>, typeInfo, iter)
    }
}

@Throws(KlaxonException::class)
fun <T : Any> HttpRequest.respondJson(
        value: T?,
        typeInfo: KxType,
        code: Int = 200,
        headers: Map<String, List<String>> = mapOf(),
        addCookies: List<HttpCookie> = listOf()
) {
    @Suppress("UNCHECKED_CAST")
    val data = JsonSerializer.write(typeInfo.base.kclass as KClass<T>, typeInfo, value).toString().toByteArray()
    this.respond(
            code = code,
            headers = headers,
            addCookies = addCookies,
            contentType = ContentType.Application.Json,
            data = data
    )
}