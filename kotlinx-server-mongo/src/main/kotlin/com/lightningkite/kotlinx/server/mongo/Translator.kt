package com.lightningkite.kotlinx.server.mongo

import com.lightningkite.kotlinx.server.base.Context
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import kotlin.reflect.KClass

var Context.mongo: MongoClient
    get() = this["mongo"] as MongoClient
    set(value){
        this["mongo"] = value
    }

val Context.mongoDb get() = mongo.getDatabase("primary")


fun MongoDatabase.get(type: KClass<*>) = getCollection()