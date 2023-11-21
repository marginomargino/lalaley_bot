package core

import telegram.Bot

suspend fun main() =
    Bot.bot().startPolling()



