package scheduling

import com.github.kotlintelegrambot.entities.ChatId
import core.Static
import kotlinx.coroutines.runBlocking
import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.TriggerBuilder
import telegram.Bot
import telegram.Task

class SendMessageJob : Job {
    override fun execute(context: JobExecutionContext) {
        runBlocking {
            Bot.process(ChatId.fromId(Static.env["TELEGRAM_CHAT_ID"].toLong()), Task.Random)
        }

        val newTrigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(generateRandomCronExpression()))
            .build()

        context.scheduler.rescheduleJob(context.trigger.key, newTrigger)
    }
}


