package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.server.base.Transaction
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import java.util.*


val Transaction_xodus = WeakHashMap<PersistentEntityStore, WeakHashMap<Transaction, StoreTransaction>>()
fun Transaction.getXodus(store: PersistentEntityStore): StoreTransaction {
    return Transaction_xodus.getOrPut(store) { java.util.WeakHashMap() }.getOrPut(this) {
        val txn = if (this.readOnly) store.beginReadonlyTransaction()
        else store.beginTransaction()

        this.onCommit += { txn.commit() }
        this.onFail += { txn.abort() }

        txn
    }
}

val Transaction.xodus get() = getXodus(context.xodus)