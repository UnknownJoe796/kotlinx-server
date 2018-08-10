package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.locale.*
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.JsonSerializer
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

        Date::class -> (value as Date).iso8601()
        Time::class -> (value as Time).iso8601()
        TimeStamp::class -> (value as TimeStamp).iso8601()

        String::class -> value as String

        else -> JsonSerializer.write(type, value)
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

        Date::class -> Date.iso8601(value as String)
        Time::class -> Time.iso8601(value as String)
        TimeStamp::class -> TimeStamp.iso8601(value as String)

        String::class -> value as String

        else -> JsonSerializer.read(type, value as String)!!
    }
}