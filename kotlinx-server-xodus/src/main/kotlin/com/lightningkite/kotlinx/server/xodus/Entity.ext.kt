package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxReflect
import com.lightningkite.kotlinx.reflection.untyped
import jetbrains.exodus.entitystore.Entity
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

inline fun <reified T : Any> Entity.read(): T = read(T::class)
fun <T : Any> Entity.read(type: KClass<T>): T {
    val item = type.createInstance()
    readInto(type, item)
    return item
}

inline fun <reified T : Any> Entity.readInto(item: T) = readInto(T::class, item)
fun <T : Any> Entity.readInto(type: KClass<T>, item: T) {
    for (field in type.kxReflect.variables.values) {
        val valueRead = this.get(field.name, field.type)
        if (valueRead == null && !field.type.nullable) {
            //Skip.  We'll let the default value stand.
            continue
        }
        field.set.untyped(item, valueRead)
    }
}

inline fun <reified T : Any> Entity.write(item: T) = write(T::class, item)
fun <T : Any> Entity.write(type: KClass<T>, item: T) {
    for (field in type.kxReflect.variables.values) {
        this.set(field.name, field.type, field.get(item))
    }
}

val KxType_estimatedLength = WeakHashMap<KxType, Int?>()
val KxType.estimatedLength: Int?
    get() = KxType_estimatedLength.getOrPut(this) {
        annotations.find { it.name == "EstimatedLength" }?.arguments?.firstOrNull() as? Int
    }

fun Entity.get(name: String, typeInformation: KxType): Any? {
    if (name == "id") return toIdString()
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    return if ((typeInformation.estimatedLength ?: 255) > 255)
        getBlobString(name)?.let { XodusConversion.fromXodus(it, typeInformation) }
    else
        getProperty(name)?.let { XodusConversion.fromXodus(it, typeInformation) }
}

fun Entity.set(name: String, typeInformation: KxType, value: Any?) {
    if (name == "id") return
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    if (value == null)
        deleteProperty(name)
    else if ((typeInformation.estimatedLength ?: 255) > 255)
        setBlobString(name, XodusConversion.toXodus(value, typeInformation) as String)
    else
        setProperty(name, XodusConversion.toXodus(value, typeInformation))
}
