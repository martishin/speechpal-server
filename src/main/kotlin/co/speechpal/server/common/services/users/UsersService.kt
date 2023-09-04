package co.speechpal.server.common.services.users

import co.speechpal.server.common.models.domain.users.User

interface UsersService {
    suspend fun findByTelegramUserId(telegramUserId: Long): User?
    suspend fun save(user: User): User
}
