package com.lightningkite.kotlinx.server.xodus

import com.lightningkite.kotlinx.server.base.Context
import jetbrains.exodus.entitystore.PersistentEntityStore

var Context.xodus: PersistentEntityStore
    get() = this["xodus"] as PersistentEntityStore
    set(value) {
        this["xodus"] = value
    }