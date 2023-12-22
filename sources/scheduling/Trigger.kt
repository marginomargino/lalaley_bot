package scheduling

import core.Static.timeZone
import kotlinx.datetime.*
import org.quartz.CronScheduleBuilder
import org.quartz.CronTrigger
import org.quartz.TriggerBuilder
import java.util.TimeZone
import kotlin.random.Random

private fun generateRandomCronExpression(): String {
    val now = Clock.System.now().toLocalDateTime(timeZone)
    val futureDate = now.date.plus(3 + Random.nextLong(4), DateTimeUnit.DAY)

    var dayOfWeek = futureDate.dayOfWeek.isoDayNumber
    val hour = Random.nextInt(10, 19)
    val minute = Random.nextInt(0, 60)

    if (futureDate.dayOfWeek == now.dayOfWeek &&
        (hour < now.hour || (hour == now.hour && minute <= now.minute))
    ) {
        dayOfWeek = (dayOfWeek % 7) + 1
    }

    return "0 $minute $hour ? * $dayOfWeek *"
}


fun triggerForRandomTime(): CronTrigger =
    TriggerBuilder.newTrigger()
        .withSchedule(
            CronScheduleBuilder.cronSchedule(generateRandomCronExpression())
                .inTimeZone(TimeZone.getTimeZone(timeZone.toJavaZoneId()))
        )
        .build()

