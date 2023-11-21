package core

import com.mongodb.kotlin.client.coroutine.MongoClient
import core.Static.env
import message.VkMessage

object DB {
    val client = MongoClient.create(env["DB_URL"])
    val database = client.getDatabase(env["DB_NAME"])
    val collection = database.getCollection(env["DB_COLLECTION_NAME"], VkMessage::class.java)
}
