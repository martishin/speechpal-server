package co.speechpal.server.bot

import arrow.core.Either
import arrow.core.left
import co.speechpal.server.bot.configuration.BotProperties
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.commands.base.AbstractCommandHandler
import co.speechpal.server.bot.handlers.media.base.AbstractMediaHandler
import co.speechpal.server.bot.models.dto.BotError
import co.speechpal.server.bot.models.dto.BotResponse
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.voice
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.logging.LogLevel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class BotServer(
    private val botProperties: BotProperties,
    @Autowired private val commandHandlers: Map<String, AbstractCommandHandler>,
    @Autowired private val mediaHandlers: Map<String, AbstractMediaHandler<*>>,
) {
    companion object {
        private val log = LoggerFactory.getLogger(BotServer::class.java)
    }

    private val voiceExecutor = Executors.newCachedThreadPool().asCoroutineDispatcher()

    fun startBot() {
        val bot = bot {
            token = botProperties.token
            logLevel = LogLevel.Error

            dispatch {
                command("start") {
                    handleCommand(Operation.START, this).map {
                        bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = it.message)
                    }
                }

                voice {
                    GlobalScope.launch(voiceExecutor) {
                        handleMedia(Operation.VOICE, this@voice).fold(
                            { error ->
                                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error.reason)
                            },
                            { success ->
                                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = success.message)
                            },
                        )
                    }
                }
            }
        }

        bot.startPolling()
    }

    private suspend fun handleCommand(command: String, env: CommandHandlerEnvironment): Either<BotError, BotResponse> {
        return commandHandlers[command]?.handle(env) ?: run {
            val error = "No handler found for the command '$command'"
            log.error(error)
            return BotError.HandlerNotFound(error).left()
        }
    }

    private suspend fun <Media> handleMedia(
        command: String,
        env: MediaHandlerEnvironment<Media>,
    ): Either<BotError, BotResponse> {
        return (mediaHandlers[command] as? AbstractMediaHandler<Media>?)?.handle(env) ?: run {
            val error = "No handler found for the command '$command'"
            log.error(error)
            return BotError.HandlerNotFound(error).left()
        }
    }
}
