package co.speechpal.server.common.services.users

import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.repositories.users.UsersRepository
import org.springframework.stereotype.Service

@Service
class DefaultUsersService(
    private val usersRepository: UsersRepository,
) : UsersService {
    override suspend fun findByTelegramUserId(telegramUserId: Long): User? {
        return usersRepository.findByTelegramUserId(telegramUserId)
    }

    override suspend fun save(user: User): User {
        return usersRepository.save(user)
    }
}
