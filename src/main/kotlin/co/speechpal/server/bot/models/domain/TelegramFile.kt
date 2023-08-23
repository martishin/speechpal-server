package co.speechpal.server.bot.models.domain

data class TelegramFile(
    val fileUniqueId: String,
    val downloadedFile: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TelegramFile

        if (fileUniqueId != other.fileUniqueId) return false
        return downloadedFile.contentEquals(other.downloadedFile)
    }

    override fun hashCode(): Int {
        var result = fileUniqueId.hashCode()
        result = 31 * result + downloadedFile.contentHashCode()
        return result
    }
}
