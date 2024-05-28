package scheduling

import com.github.kotlintelegrambot.entities.ChatId
import core.Static
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.todayIn
import org.quartz.Job
import org.quartz.JobExecutionContext
import telegram.Bot
import telegram.Task

class SendMessageJob : Job {
    override fun execute(context: JobExecutionContext) {
        val today = Static.clock.todayIn(Static.timeZone)
        runBlocking {
            Bot.process(
                chatId = ChatId.fromId(Static.env["TELEGRAM_CHAT_ID"].toLong()),
                task = Task.ForDate(today.dayOfMonth, today.monthNumber),
                skipIfEmpty = true
            )
        }

        context.scheduler.rescheduleJob(context.trigger.key, triggerForRandomTime())
    }
}


