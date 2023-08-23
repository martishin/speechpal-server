package co.speechpal.server.bot.services.telegram

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.speechpal.server.bot.models.domain.Context
import co.speechpal.server.bot.models.domain.TelegramFile
import co.speechpal.server.bot.models.dto.BotError
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.files.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultTelegramFileService : TelegramFileService {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultTelegramFileService::class.java)
    }

    override suspend fun downloadFileById(context: Context, bot: Bot, fileId: String): Either<BotError, TelegramFile> {
        val fileInfo = fetchFileInfo(bot, fileId)
            ?: return BotError.ErrorProcessingFile("Error getting file info").left()
        val downloadedFile = downloadFile(bot, fileInfo.filePath!!)
            ?: return BotError.ErrorProcessingFile("Error downloading file").left()

        return TelegramFile(
            fileInfo.fileUniqueId,
            downloadedFile,
        ).right()
    }

    private suspend fun fetchFileInfo(bot: Bot, fileId: String): File? = withContext(Dispatchers.IO) {
        bot.getFile(fileId).first?.body()?.result
    }

    private suspend fun downloadFile(bot: Bot, filePath: String): ByteArray? = withContext(Dispatchers.IO) {
        bot.downloadFile(filePath).first?.body()?.bytes()
    }
}
