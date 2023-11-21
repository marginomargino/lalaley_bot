package message

import core.DB.collection
import kotlinx.coroutines.flow.toList
import org.bson.Document

object MessageProvider {

    suspend fun get(dayOfMonth: Int, monthNumber: Int): Map<Int, List<VkMessage>> =
        collection.find().filter(Document(mapOf("day_of_month" to dayOfMonth, "month_number" to monthNumber)))
            .toList()
            .groupBy { it.year }

}
