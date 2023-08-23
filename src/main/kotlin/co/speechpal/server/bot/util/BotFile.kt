package co.speechpal.server.bot.util

import java.nio.file.Files
import java.nio.file.Paths

data class BotFile(
    val path: String,
    val id: String,
    val name: String,
    val extension: String,
) {
    fun fullPath(): String = "$path/$id/$name.$extension"

    fun sizeInBytes(): Long = Files.size(Paths.get(fullPath()))
}
