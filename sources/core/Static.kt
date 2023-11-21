package core

import io.github.cdimascio.dotenv.Dotenv
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter
import java.util.*

object Static {
    val clock = Clock.System
    val env: Dotenv = Dotenv.load()
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru-RU"))
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeZone = TimeZone.of("Europe/Samara")
}
