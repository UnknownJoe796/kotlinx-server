package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.JsonSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object XodusConversion {

    fun toXodus(value: Any, type: KxType): Comparable<*> = when (type.base.kclass) {
        Boolean::class,
        Int::class,
        Long::class,
        Float::class,
        Double::class,
        Byte::class,
        Short::class,
        Char::class -> value as Comparable<*>

        Date::class -> (value as Date).time
        ZonedDateTime::class -> (value as ZonedDateTime).toInstant().toEpochMilli()

        String::class -> value as String

        else -> JsonSerializer.write(type.base.kclass as KClass<Any>, type, value).toString()
    }

    fun fromXodus(value: Any, type: KxType): Any = when (type.base.kclass) {
        Boolean::class,
        Int::class,
        Long::class,
        Float::class,
        Double::class,
        Byte::class,
        Short::class,
        Char::class -> value

        Date::class -> Date(value as Long)
        ZonedDateTime::class -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(value as Long), ZoneId.systemDefault())

        String::class -> value as String

        else -> JsonSerializer.read(type.base.kclass as KClass<Any>, type, value as String)!!
    }
}