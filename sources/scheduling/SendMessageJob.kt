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
import kotlin.random.Random

class SendMessageJob : Job {
    private fun generateRandomCronExpression(): String {
        val random = Random.Default
        val dayOfWeek = random.nextInt(1, 8)
        val hour = random.nextInt(10, 19)
        val minute = random.nextInt(0, 60)

        return "0 $minute $hour ? * $dayOfWeek *"
    }


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


