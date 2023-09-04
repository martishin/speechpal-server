package co.speechpal.server.common.repositories.users

import co.speechpal.server.common.models.domain.users.User

interface UsersRepository {
    suspend fun findByTelegramUserId(telegramUserId: Long): User?
    suspend fun save(user: User): User
}
