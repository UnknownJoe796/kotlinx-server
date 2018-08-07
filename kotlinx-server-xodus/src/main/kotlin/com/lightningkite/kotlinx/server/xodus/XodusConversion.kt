package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.locale.Date
import com.lightningkite.kotlinx.locale.DateTime
import com.lightningkite.kotlinx.locale.Time
import com.lightningkite.kotlinx.locale.TimeStamp
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

        Date::class -> (value as Date).daysSinceEpoch
        Time::class -> (value as Time).millisecondsSinceMidnight
        TimeStamp::class -> (value as TimeStamp).millisecondsSinceEpoch

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

        Date::class -> Date(value as Int)
        Time::class -> Time(value as Int)
        TimeStamp::class -> TimeStamp(value as Long)

        String::class -> value as String

        else -> JsonSerializer.read(type.base.kclass as KClass<Any>, type, value as String)!!
    }
}