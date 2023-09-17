package co.speechpal.server.bot.handlers.media

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.errorhandling.ErrorHandler
import co.speechpal.server.bot.handlers.Operation
import co.speechpal.server.bot.handlers.media.base.AbstractMediaHandler
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.dto.BotResponse
import co.speechpal.server.bot.models.errors.BotError
import co.speechpal.server.bot.services.audio.AudioConverterService
import co.speechpal.server.bot.services.audio.AudioTranscriberService
import co.speechpal.server.bot.services.grammar.GrammarCheckerService
import co.speechpal.server.bot.services.telegram.TelegramFileService
import co.speechpal.server.common.services.reports.ReportsService
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.files.Voice
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Component(Operation.VOICE)
class VoiceHandler(
    private val telegramFileService: TelegramFileService,
    private val reportsService: ReportsService,
    private val audioConverterService: AudioConverterService,
    private val audioTranscriberService: AudioTranscriberService,
    private val grammarCheckerService: GrammarCheckerService,
    private val errorHandler: ErrorHandler,
) : AbstractMediaHandler<Voice>() {
    override suspend fun handle(
        bot: Bot,
        update: Update,
        message: Message,
        media: Voice,
    ): Either<BotError, BotResponse> = either {
        val context = Context(media.fileUniqueId)

        val (fileUniqueId, file) = telegramFileService.downloadFileById(context, bot, media.fileId).bind()

        val dirPath = Paths.get("tmp/$fileUniqueId")
        Files.createDirectories(dirPath)

        val telegramAudioFile = File("tmp/$fileUniqueId/telegram_audio.ogg")
        val audioFile = File("tmp/$fileUniqueId/audio.m4a")

        telegramAudioFile.writeBytes(file)

        audioConverterService.convert(context, telegramAudioFile, audioFile).bind()

        val text = audioTranscriberService.transcribe(context, audioFile).bind()
        val textCheckResult = grammarCheckerService.checkGrammar(context, text).bind()

        reportsService.insertReport(context.requestId, textCheckResult).bind()

        Files.walk(dirPath)
            .sorted(Comparator.reverseOrder()) // This is important, so we delete child files/directories first
            .forEach(Files::delete)

        BotResponse(
            "You can check your results here: \n" +
                "https://speechpal.co/reports/${context.requestId}",
        )
    }.mapLeft { error ->
        errorHandler.handleGenericError(error)
    }
}
