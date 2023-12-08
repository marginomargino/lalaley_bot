package scheduling

import com.github.kotlintelegrambot.entities.ChatId
import core.Static
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.TriggerBuilder
import telegram.Bot
import telegram.Task
import kotlin.random.Random

class SendMessageJob : Job {
    private fun generateRandomCronExpression(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val futureDate = now.date.plus(3 + Random.nextLong(4), DateTimeUnit.DAY)

        var dayOfWeek = futureDate.dayOfWeek.isoDayNumber
        val hour = Random.nextInt(10, 19)
        val minute = Random.nextInt(0, 60)

        if (futureDate.dayOfWeek == now.dayOfWeek &&
            (hour < now.hour || (hour == now.hour && minute <= now.minute))) {
            dayOfWeek = (dayOfWeek % 7) + 1
        }

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


