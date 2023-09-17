package co.speechpal.server.common.services.users

import arrow.core.Either
import co.speechpal.server.common.models.domain.users.NewUser
import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.common.repositories.users.UsersRepository
import org.springframework.stereotype.Service

@Service
class DefaultUsersService(private val usersRepository: UsersRepository) : UsersService {
    override suspend fun findByTelegramUserId(telegramUserId: Long): Either<DomainError, User?> =
        usersRepository.findByTelegramUserId(telegramUserId)

    override suspend fun create(newUser: NewUser): Either<DomainError, User> =
        usersRepository.create(newUser)

    override suspend fun save(user: User): Either<DomainError, User> =
        usersRepository.save(user)
}
