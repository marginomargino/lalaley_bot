package export

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import core.DB.client
import core.DB.database
import core.Static.env
import core.Static.timeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.bson.Document

@Suppress("UNREACHABLE_CODE")
suspend fun main() {
    return // Already done

    val collection = database.getCollection<Document>(env["DB_COLLECTION_NAME"])
    collection.drop()

    val vk = VkApiClient(HttpTransportClient.getInstance())
    val token = env["VK_ACCESS_TOKEN"]
    val userActor = UserActor(env["VK_USER_ID"].toInt(), token)
    val peerId = env["VK_PEER_ID"].toInt()

    val step = 200
    var offset = vk.messages().getHistory(userActor).peerId(peerId).execute().count - 1


    try {
        do {
            println(offset)
            val historyEntry = vk.messages()
                .getHistory(userActor)
                .peerId(peerId)
                .offset(offset.takeIf { it > 0 } ?: 0)
                .count(if (offset > 0) step else step + offset)
                .execute()

            if (historyEntry.items.isEmpty())
                break

            val documents = historyEntry.items.map {
                val document = Document.parse(it.toString())
                document["_id"] = document["id"].also { document.remove("id") }
                val timestamp = Instant.fromEpochSeconds((document["date"] as Int).toLong())
                val localDateTime = timestamp.toLocalDateTime(timeZone)
                document["day_of_month"] = localDateTime.dayOfMonth
                document["month_number"] = localDateTime.monthNumber
                document["year"] = localDateTime.year
                document["timestamp"] = timestamp
                document
            }

            collection.insertMany(documents)
            offset -= step
        } while (offset > 0)
        collection.createIndex(Document(mapOf("day_of_month" to 1, "month_number" to 1, "year" to 1)))
    } catch (e: Exception) {
        println("Offset: $offset")
        println(e)
    } finally {
        println("Finished")
        client.close()
    }
}

