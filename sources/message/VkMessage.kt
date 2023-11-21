package message

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VkMessage(
    @SerialName("_id")
    val id: Int,
    val attachments: List<Attachment>,
    @SerialName("conversation_message_id")
    val conversationMessageId: Int,
    @SerialName("from_id")
    val fromId: Int,
    @SerialName("day_of_month")
    val dayOfMonth: Int,
    @SerialName("month_number")
    val monthNumber: Int,
    val text: String,
    val timestamp: Instant,
    val year: Int
) : Comparable<VkMessage> {

    override fun compareTo(other: VkMessage): Int {
        val thisHasPhoto = this.attachments.any { it.photo != null }
        val otherHasPhoto = other.attachments.any { it.photo != null }

        return when {
            thisHasPhoto && !otherHasPhoto -> -1
            !thisHasPhoto && otherHasPhoto -> 1
            else -> this.timestamp.compareTo(other.timestamp)
        }
    }

    fun date() = LocalDate(year, monthNumber, dayOfMonth)


    @Serializable
    data class Attachment(
        val photo: Photo?
    )

    @Serializable
    data class Photo(
        val sizes: Collection<Size>
    ) {

        @Serializable
        data class Size(
            val type: Type,
            val url: String
        ) {
            enum class Type {
                m,
                o,
                p,
                q,
                r,
                s,
                w,
                x,
                y,
                z,
                ;
            }
        }

        fun size(type: Size.Type): Size =
            sizes.single { it.type == type }
    }


    interface ForwardedMessage

    companion object{
        val idToName = mapOf(
            91044092 to "Юля",
            2653975 to "Полина",
            2835913 to "Маша",
            4622703 to "Даша",
            699553 to "Чиж",
            2060859 to "Никита",
            3782000 to "Илья",
            5231405 to "Катя",
            200335706 to "Вадим"
        )
    }


}
