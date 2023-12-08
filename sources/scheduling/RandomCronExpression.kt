package scheduling

import kotlinx.datetime.*
import kotlin.random.Random

fun generateRandomCronExpression(): String {
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
