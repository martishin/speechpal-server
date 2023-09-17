package co.speechpal.server.common.repositories.users

import arrow.core.Either
import arrow.core.raise.either
import co.speechpal.server.common.errorhandling.ExceptionHandler
import co.speechpal.server.common.models.domain.users.NewUser
import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.jooq.speechpal.Tables.USERS
import co.speechpal.server.jooq.speechpal.tables.records.UsersRecord
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class DefaultUsersRepository(
    private val dslContext: DSLContext,
    private val exceptionHandler: ExceptionHandler,
) : UsersRepository {
    override suspend fun findByTelegramUserId(telegramUserId: Long): Either<DomainError, User?> = either {
        try {
            val sql = dslContext.selectFrom(USERS)
                .where(USERS.TELEGRAM_USER_ID.eq(telegramUserId))

            val foundRecord = sql
                .awaitFirstOrNull()

            foundRecord?.toUser()
        } catch (e: Exception) {
            raise(exceptionHandler.handleDbException(e, "Error when fetching user from db"))
        }
    }

    override suspend fun create(newUser: NewUser): Either<DomainError, User> = either {
        try {
            val sql = dslContext
                .insertInto(USERS)
                .columns(
                    USERS.TELEGRAM_USER_ID,
                    USERS.CHAT_ID,
                    USERS.USERNAME,
                    USERS.FIRST_NAME,
                    USERS.LAST_NAME,
                )
                .values(
                    newUser.telegramUserId,
                    newUser.chatId,
                    newUser.username,
                    newUser.firstName,
                    newUser.lastName,
                )

            val createdRecord = sql
                .returning()
                .awaitFirst()

            createdRecord.toUser()
        } catch (e: Exception) {
            raise(exceptionHandler.handleDbException(e, "Error when creating user in db"))
        }
    }

    override suspend fun save(user: User): Either<DomainError, User> = either {
        try {
            val sql = dslContext
                .update(USERS)
                .set(USERS.CURRENT_DIALOG_ID, user.currentDialogId)
                .where(USERS.ID.eq(user.id))

            val updatedRecord = sql
                .returning()
                .awaitFirst()

            updatedRecord.toUser()
        } catch (e: Exception) {
            raise(exceptionHandler.handleDbException(e, "Error when saving user in db"))
        }
    }

    private fun UsersRecord.toUser(): User {
        return User(
            this.get("id", Int::class.java),
            this.get("telegram_user_id", Long::class.java),
            this.get("chat_id", Long::class.java),
            this.get("username", String::class.java),
            this.get("first_name", String::class.java),
            this.get("last_name", String::class.java),
            this.get("created_at", Instant::class.java),
            this.get("updated_at", Instant::class.java),
            this.get("current_dialog_id", Int::class.java),
        )
    }
}
