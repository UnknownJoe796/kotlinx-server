package com.lightningkite.kotlinx.server.base

import com.lightningkite.kotlinx.server.ServerFunction
import java.util.*
import kotlin.reflect.KClass

private val KClassServerFunction_RequiresWrite = HashMap<KClass<*>, Boolean>()
var KClass<out ServerFunction<*>>.requiresWrite: Boolean
    set(value) {
        KClassServerFunction_RequiresWrite[this] = value
    }
    get() {
        return KClassServerFunction_RequiresWrite[this] ?: false
    }

private val KClassServerFunction_Invocation = HashMap<KClass<*>, (Any, Transaction) -> Any?>()
@Suppress("UNCHECKED_CAST")
var <SF : ServerFunction<R>, R> KClass<SF>.invocation: (SF, Transaction) -> R
    set(value) {
        KClassServerFunction_Invocation[this] = value as (Any, Transaction) -> Any?
    }
    get() {
        return KClassServerFunction_Invocation[this] as (SF, Transaction) -> R
    }

@Suppress("UNCHECKED_CAST")
operator fun <R> ServerFunction<R>.invoke(transaction: Transaction): R
    = KClassServerFunction_Invocation[this::class]!!.invoke(this, transaction) as R