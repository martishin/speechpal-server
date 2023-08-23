package co.speechpal.server

import co.speechpal.server.bot.BotServer
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    val context = runApplication<ApplicationServer>(*args)
    val botServer = context.getBean(BotServer::class.java)
    botServer.startBot()
}
