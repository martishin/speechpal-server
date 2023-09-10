package co.speechpal.server.bot

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
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

    private suspend fun handleCommand(command: String, env: CommandHandlerEnvironment): Either<BotError, BotResponse> {
        return createUserIfNotExists(env.message).flatMap {
            commandHandlers[command]?.handle(env) ?: run {
                val error = "No handler found for the command '$command'"
                log.error(error)
                return BotError.HandlerNotFound(error).left()
            }
        }
    }

    private suspend fun <Media> handleMedia(
        command: String,
        env: MediaHandlerEnvironment<Media>,
    ): Either<BotError, BotResponse> {
        return createUserIfNotExists(env.message).flatMap {
            (mediaHandlers[command] as? AbstractMediaHandler<Media>?)?.handle(env) ?: run {
                val error = "No handler found for the command '$command'"
                log.error(error)
                return BotError.HandlerNotFound(error).left()
            }
        }
    }

    private suspend fun handleText(
        env: TextHandlerEnvironment,
    ): Either<BotError, BotResponse> {
        return createUserIfNotExists(env.message).flatMap {
            textHandler.handle(env)
        }
    }

    private suspend fun createUserIfNotExists(message: Message): Either<BotError, Boolean> {
        val telegramUser =
            message.from ?: return BotError.CannotReadUserData().left()

        return usersService.findByTelegramUserId(telegramUser.id).flatMap { user ->
            if (user == null) {
                usersService.create(
                    NewUser(
                        telegramUserId = telegramUser.id,
                        chatId = message.chat.id,
                        username = telegramUser.username,
                        firstName = telegramUser.firstName,
                        lastName = telegramUser.lastName,
                    ),
                ).map {
                    true
                }
            } else {
                false.right()
            }
        }.mapLeft { error ->
            errorHandler.handleGenericError(error)
        }
    }
}
