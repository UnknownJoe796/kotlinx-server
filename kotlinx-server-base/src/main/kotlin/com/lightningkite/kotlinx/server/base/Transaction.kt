package com.lightningkite.kotlinx.server.base

import com.lightningkite.kotlinx.lambda.invokeAll


/**
 * Represents a database transaction
 * Created by josep on 7/12/2017.
 */
class Transaction(
        val context: Context,
        val user: Any? = null,
        val readOnly: Boolean = false,
        val required: Boolean = false
) {
    val cache = HashMap<Any, Any?>()
    val onCommit = ArrayList<() -> Unit>()
    val onFail = ArrayList<() -> Unit>()
    var finished = false

    fun commit() {
        if (finished) return
        finished = true
        onCommit.invokeAll()
    }

    fun fail() {
        if (finished) return
        finished = true
        onFail.invokeAll()
    }
}

inline fun <T> Transaction.use(action: (Transaction) -> T): T = try {
    val result = action.invoke(this)
    commit()
    result
} finally {
    fail()
}

inline fun <T> Transaction.useWith(action: Transaction.() -> T): T = try {
    val result = action.invoke(this)
    commit()
    result
} finally {
    fail()
}