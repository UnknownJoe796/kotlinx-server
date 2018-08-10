package com.lightningkite.kotlinx.server.mongo

import com.lightningkite.kotlinx.reflection.kxType
import com.lightningkite.kotlinx.serialization.externalName
import com.lightningkite.kotlinx.server.ConditionOnItem
import com.lightningkite.kotlinx.server.ModificationOnItem
import com.lightningkite.kotlinx.server.base.Context
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.*
import kotlin.reflect.KClass
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId


var Context.mongo: MongoClient
    get() = this["mongo"] as MongoClient
    set(value){
        this["mongo"] = value
    }

val Context.mongoDb get() = mongo.getDatabase("primary")


inline fun <reified T: Any> MongoDatabase.get() = get(T::class)
operator fun MongoDatabase.get(type: KClass<*>):MongoCollection<BsonDocument> = try{
    this.getCollection(type.externalName!!, BsonDocument::class.java)
} catch(e:IllegalAccessException){
    //not created yet
    this.createCollection(type.externalName!!)
    this.getCollection(type.externalName!!, BsonDocument::class.java)
}

inline fun <reified T: Any> MongoCollection<BsonDocument>.insertOne(value: T)
        = this.insertOne(BsonValueSerializer.write(T::class.kxType, value, Unit) as BsonDocument)

inline fun <reified T: Any> MongoCollection<BsonDocument>.insertMany(values: List<T>)
        = this.insertMany(values.map { BsonValueSerializer.write(T::class.kxType, it, Unit) as BsonDocument })

inline fun <reified T: Any> MongoCollection<BsonDocument>.replace(id:String, value: T)
        = this.updateOne(BasicDBObject("_id", ObjectId(id)), BsonValueSerializer.write(T::class.kxType, value, Unit) as BsonDocument)

inline fun <reified T: Any> MongoCollection<BsonDocument>.updateOne(id:String, modifications:List<ModificationOnItem<T>>)
        = this.updateOne(BasicDBObject("_id", ObjectId(id)), modifications.toMongo(BsonValueSerializer))

inline fun <reified T: Any> MongoCollection<BsonDocument>.updateMany(filter: ConditionOnItem<T>, modifications:List<ModificationOnItem<T>>)
        = this.updateMany(filter.toMongo(BsonValueSerializer), modifications.toMongo(BsonValueSerializer))