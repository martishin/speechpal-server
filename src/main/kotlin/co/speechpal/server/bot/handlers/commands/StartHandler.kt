package co.speechpal.server.bot.handlers.commands

import arrow.core.Either
import arrow.core.right
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.commands.base.AbstractCommandHandler
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import org.springframework.stereotype.Component

@Component(Operation.START)
class StartHandler(
    errorHandler: ErrorHandler,
) : AbstractCommandHandler(errorHandler) {
    private companion object {
        const val START_TEXT = """
Enjoy using the SpeechPal Telegram bot to analyze your speech in audio and video formats, and improve your speaking skills with personalized recommendations.

To analyze your speech, you have multiple options:

🔵Audio message: simply upload it to the bot by sending the file or recording a new voice message within the chat.
🔵Audio on your phone: send the file to the bot by tapping on the paperclip icon in the chat and selecting the file.
🔵YouTube video: type /youtube and the video's link right afterward.

After you've provided the audio or video to the bot, it will process the content and analyze your speech. Once the analysis is complete, the bot will send you a detailed message with recommendations to help you improve your speech.
        """
    }

    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        args: List<String>,
    ): Either<BotError, BotResponse> {
        return BotResponse(START_TEXT).right()
    }
}
