package export

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import io.github.cdimascio.dotenv.Dotenv
import org.bson.Document

suspend fun main() {
    val dotenv = Dotenv.load()

    val mongoClient =
        MongoClient.create(dotenv["DB_URL"])
    val database = mongoClient.getDatabase(dotenv["DB_NAME"])
    database.drop()
    val collection = database.getCollection<Document>(dotenv["DB_COLLECTION_NAME"])

    val vk = VkApiClient(HttpTransportClient.getInstance())
    val token = dotenv["VK_ACCESS_TOKEN"]
    val userActor = UserActor(dotenv["VK_USER_ID"].toInt(), token)
    val peerId = dotenv["VK_PEER_ID"].toInt()

    val step = 200
    var offset = vk.messages().getHistory(userActor).peerId(peerId).execute().count - 1


    try {
        do {
            println(offset)
            val historyEntry = vk.messages()
                    .getHistory(userActor)
                    .peerId(peerId)
                    .offset(offset.takeIf { it > 0 } ?: 0)
                    .count(step)
                    .execute()

            if (historyEntry.items.isEmpty())
                break

            val documents = historyEntry.items.map {
                val document = Document.parse(it.toString())
                document["_id"] = document["id"].also { document.remove("id") }
                document
            }

            collection.insertMany(documents)
            offset -= step
        } while (true)
    } catch (e: Exception) {
        println("Offset: $offset")
        println(e)
    } finally {
        println("Finished")
        mongoClient.close()
    }
}

