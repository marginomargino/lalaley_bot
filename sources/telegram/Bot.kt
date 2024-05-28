package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.logging.LogLevel
import core.Static.clock
import core.Static.dateFormatter
import core.Static.env
import core.Static.timeFormatter
import core.Static.timeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import message.MessageProvider
import message.VkMessage
import org.quartz.JobBuilder
import org.quartz.impl.StdSchedulerFactory
import scheduling.SendMessageJob
import scheduling.triggerForRandomTime
import kotlin.random.Random


object Bot {

    private val bot: Bot = bot {
        token = env["BOT_TOKEN"]
        logLevel = LogLevel.Error
        dispatch {
            command("today") {
                val today = clock.todayIn(timeZone)
                process(Task.ForDate(today.dayOfMonth, today.monthNumber))
            }
            command("random") {
                process(Task.Random)
            }
            telegramError {
                println(error.getErrorMessage())
            }
        }
    }


    private fun String.escape(): String {
        val escapeChars =
            listOf("_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!")
        var escapedText = this
        for (char in escapeChars) {
            escapedText = escapedText.replace(char, "\\$char")
        }
        return escapedText
    }


    private suspend fun messagesForRandomDate(): Map<Int, List<VkMessage>> {
        do {
            val monthNumber = Random.nextInt(1, 12)
            val dayOfMonth = Random.nextInt(
                1, when (monthNumber) {
                    2 -> 28
                    1, 3, 5, 7, 8, 10, 12 -> 31
                    else -> 30
                }
            )
            MessageProvider.get(dayOfMonth, monthNumber).takeIf { it.isNotEmpty() }?.let { return it }
        } while (true)
    }


    private suspend fun CommandHandlerEnvironment.process(task: Task) {
        val chatId = ChatId.fromId(message.chat.id)
        process(chatId, task)
    }


    suspend fun process(chatId: ChatId, task: Task, skipIfEmpty: Boolean = false) {
        try {
            bot.sendChatAction(
                chatId = chatId,
                action = ChatAction.TYPING
            )

            val messagesByYear = when (task) {
                is Task.ForDate -> MessageProvider.get(task.dayOfMonth, task.monthNumber)
                Task.Random -> messagesForRandomDate()
            }

            if (task is Task.ForDate && messagesByYear.isEmpty()) {
                if (!skipIfEmpty) {
                    bot.sendMessage(
                        chatId = chatId,
                        parseMode = ParseMode.MARKDOWN_V2,
                        text = "*А в этот день ничего не произошло*"
                    )
                }
                return
            }

            val messages = messagesByYear.values.random().sortedBy { it.timestamp }.takeAround()
            val firstMessageSentAt = messages.first().timestamp.toLocalDateTime(timeZone)
            bot.sendMessage(
                chatId = chatId,
                parseMode = ParseMode.MARKDOWN_V2,
                text = "*А в это время ${dateFormatter.format(firstMessageSentAt.toJavaLocalDateTime())}\\-го\\.\\.\\.* \n\n"
            )

            for (vkMessage in messages) {
                val messageSentAt = vkMessage.timestamp.toLocalDateTime(timeZone)
                bot.sendMessage(
                    chatId = chatId,
                    parseMode = ParseMode.MARKDOWN_V2,
                    text = buildString {
                        append("__${VkMessage.idToName.getValue(vkMessage.fromId)}__, ")
                        append(timeFormatter.format(messageSentAt.toJavaLocalDateTime()))
                        append(":")
                    }
                )

                vkMessage.attachments.mapNotNull { it.photo }.forEach { photo ->
                    bot.sendPhoto(
                        chatId = chatId,
                        photo = TelegramFile.ByUrl(photo.size(VkMessage.Photo.Size.Type.r).url)
                    )
                }
                vkMessage.text.takeIf { it.isNotEmpty() }?.let { text ->
                    bot.sendMessage(
                        chatId = chatId,
                        parseMode = ParseMode.MARKDOWN_V2,
                        text = "_${text.escape()}_",
                    ).also { println("https://vk.com/im?msgid=${vkMessage.id}&sel=c6") }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    fun start() {
        bot.startPolling()

        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val job = JobBuilder.newJob(SendMessageJob::class.java).build()
        scheduler.scheduleJob(job, triggerForRandomTime())
    }


    private fun List<VkMessage>.takeAround(count: Int = 7): List<VkMessage> {
        if (size <= count)
            return this

        val index = when (Random.nextInt(1, 10)) {
            in 1..3 -> Random.nextInt(0, size - 1)
            else -> indexOf(min())
        }

        return when {
            index <= 1 -> take(count)
            index >= size - count / 2 -> takeLast(count)
            else -> subList(maxOf(0, index - count / 2), minOf(size, index + count / 2))
        }
    }
}
