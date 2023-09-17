package co.speechpal.server.bot.services.telegram

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.domain.TelegramFile
import co.speechpal.server.bot.models.errors.BotError
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.files.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DefaultTelegramFileService : TelegramFileService {
    override suspend fun downloadFileById(
        context: Context,
        bot: Bot,
        fileId: String,
    ): Either<BotError, TelegramFile> = either {
        val fileInfo = fetchFileInfo(bot, fileId)
            ?: raise(BotError.ErrorProcessingFile("Error getting file info"))
        val downloadedFile = downloadFile(bot, fileInfo.filePath!!)
            ?: raise(BotError.ErrorProcessingFile("Error downloading file"))

        TelegramFile(
            fileInfo.fileUniqueId,
            downloadedFile,
        )
    }

    private suspend fun fetchFileInfo(bot: Bot, fileId: String): File? = withContext(Dispatchers.IO) {
        bot.getFile(fileId).first?.body()?.result
    }

    private suspend fun downloadFile(bot: Bot, filePath: String): ByteArray? = withContext(Dispatchers.IO) {
        bot.downloadFile(filePath).first?.body()?.bytes()
    }
}
