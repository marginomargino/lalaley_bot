package scheduling

import core.Static.env
import core.Static.timeZone
import kotlinx.datetime.*
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import java.util.*
import kotlin.random.Random


fun triggerForRandomTime(): Trigger {
    val now = Clock.System.now().toLocalDateTime(timeZone)

    val baseDays = env["SCHEDULE_BASE_DAYS"].toLong()
    val randomDays = Random.nextLong(env["SCHEDULE_RANDOM_DAYS"].toLong())

    val triggerTime = now.date.plus(baseDays + randomDays, DateTimeUnit.DAY)
        .atTime(Random.nextInt(10, 19), Random.nextInt(0, 60))

    val triggerDate = Date.from(triggerTime.toJavaLocalDateTime().atZone(timeZone.toJavaZoneId()).toInstant())

    return TriggerBuilder.newTrigger()
        .startAt(triggerDate)
        .build()
}
