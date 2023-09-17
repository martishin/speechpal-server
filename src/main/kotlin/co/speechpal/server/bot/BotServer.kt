package co.speechpal.server.bot

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.configuration.BotProperties
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.commands.base.AbstractCommandHandler
import co.speechpal.server.bot.handlers.media.base.AbstractMediaHandler
import co.speechpal.server.bot.handlers.text.base.AbstractTextHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.common.models.domain.users.NewUser
import co.speechpal.server.common.services.users.UsersService
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.dispatcher.voice
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
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
    private val usersService: UsersService,
    private val errorHandler: ErrorHandler,
    @Autowired private val commandHandlers: Map<String, AbstractCommandHandler>,
    @Autowired private val mediaHandlers: Map<String, AbstractMediaHandler<*>>,
    @Autowired private val textHandler: AbstractTextHandler,
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

                command("start_dialog") {
                    handleCommand(Operation.START_DIALOG, this).fold(
                        { error ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error.reason)
                        },
                        { success ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = success.message)
                        },
                    )
                }

                command("stop_dialog") {
                    handleCommand(Operation.STOP_DIALOG, this).fold(
                        { error ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error.reason)
                        },
                        { success ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = success.message)
                        },
                    )
                }

                text {
                    if (message.text?.startsWith("/") == true) {
                        return@text
                    }

                    handleText(this).fold(
                        { error ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = error.reason)
                        },
                        { success ->
                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = success.message)
                        },
                    )
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

    private suspend fun handleCommand(
        command: String,
        env: CommandHandlerEnvironment,
    ): Either<BotError, BotResponse> = either {
        createUserIfNotExists(env.message).bind()

        val handler = commandHandlers[command] ?: run {
            val error = "No handler found for the command '$command'"
            log.error(error)
            raise(BotError.HandlerNotFound(error))
        }

        handler.handle(env).bind()
    }

    private suspend fun <Media> handleMedia(
        command: String,
        env: MediaHandlerEnvironment<Media>,
    ): Either<BotError, BotResponse> = either {
        createUserIfNotExists(env.message).bind()

        val handler = mediaHandlers[command] ?: run {
            val error = "No handler found for the command '$command'"
            log.error(error)
            raise(BotError.HandlerNotFound(error))
        }

        (handler as AbstractMediaHandler<Media>).handle(env).bind()
    }

    private suspend fun handleText(
        env: TextHandlerEnvironment,
    ): Either<BotError, BotResponse> = either {
        createUserIfNotExists(env.message).bind()

        textHandler.handle(env).bind()
    }

    private suspend fun createUserIfNotExists(message: Message): Either<BotError, Boolean> = either {
        val telegramUser =
            message.from ?: raise(BotError.CannotReadUserData())

        val presentUser = usersService.findByTelegramUserId(telegramUser.id).bind()

        if (presentUser == null) {
            usersService.create(
                NewUser(
                    telegramUserId = telegramUser.id,
                    chatId = message.chat.id,
                    username = telegramUser.username,
                    firstName = telegramUser.firstName,
                    lastName = telegramUser.lastName,
                ),
            ).bind()

            true
        } else {
            false
        }
    }.mapLeft { error ->
        errorHandler.handleGenericError(error)
    }
}
