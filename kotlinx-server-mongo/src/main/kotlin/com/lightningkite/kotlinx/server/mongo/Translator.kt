package com.lightningkite.kotlinx.server.mongo

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.externalName
import com.lightningkite.kotlinx.serialization.json.JsonSerializer
import com.lightningkite.kotlinx.server.base.Context
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.*
import kotlin.reflect.KClass

var Context.mongo: MongoClient
    get() = this["mongo"] as MongoClient
    set(value){
        this["mongo"] = value
    }

val Context.mongoDb get() = mongo.getDatabase("primary")


operator fun MongoDatabase.get(type: KClass<*>):MongoCollection<BsonDocument> = try{
    this.getCollection(type.externalName!!, BsonDocument::class.java)
} catch(e:IllegalAccessException){
    //not created yet
    this.createCollection(type.externalName!!)
    this.getCollection(type.externalName!!, BsonDocument::class.java)
}

inline fun <reified T: Any> MongoDatabase.get() = get(T::class)

fun Any?.toBson(type: KxType): BsonValue{
    return when(this){
        null -> BsonNull.VALUE
        is Boolean -> BsonBoolean.valueOf(this)
        is Int -> BsonInt32(this)
        is Long -> BsonInt64(this)
        is Float -> BsonDouble(this.toDouble())
        is Double -> BsonDouble(this)
        is Byte -> BsonInt32(this.toInt())
        is Short -> BsonInt32(this.toInt())
        is Char -> BsonString(this.toString())
        is String -> BsonString(this)
        is List<*> -> BsonArray(this.map { it.toBson(type.typeParameters.first().type) })
        is Map<*, *> -> BsonDocument().apply {
            val keyType = type.typeParameters[0].type
            val valueType = type.typeParameters[1].type
            if(keyType.base.kclass == String::class){
                for((key, value) in this){
                    append(key, value.toBson(valueType))
                }
            } else {
                for((key, value) in this){
                    append(
                            JsonSerializer.write(keyType.base.kclass as KClass<Any>, keyType, key).toString(),
                            value.toBson(valueType)
                    )
                }
            }
        }
        else -> {
            for()
            BsonString(JsonSerializer.write(type.base.kclass as KClass<Any>, type, this).toString())
        }
    }
}