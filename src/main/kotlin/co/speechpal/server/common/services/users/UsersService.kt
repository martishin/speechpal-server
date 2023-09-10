package co.speechpal.server.common.services.users

import arrow.core.Either
import co.speechpal.server.common.models.domain.users.NewUser
import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.models.errors.DomainError

interface UsersService {
    suspend fun findByTelegramUserId(telegramUserId: Long): Either<DomainError, User?>
    suspend fun create(newUser: NewUser): Either<DomainError, User>
    suspend fun save(user: User): Either<DomainError, User>
}
