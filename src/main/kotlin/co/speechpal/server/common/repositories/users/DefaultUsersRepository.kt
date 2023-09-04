package co.speechpal.server.common.repositories.users

import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.toUser
import co.speechpal.server.jooq.speechpal.Tables.USERS
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class DefaultUsersRepository(private val dslContext: DSLContext) : UsersRepository {
    override suspend fun findByTelegramUserId(telegramUserId: Long): User? {
        val sql = dslContext.selectFrom(USERS)
            .where(USERS.TELEGRAM_USER_ID.eq(telegramUserId))

        return Mono.from(sql)
            .map { it.toUser() }
            .awaitSingleOrNull()
    }

    override suspend fun save(user: User): User {
        val sql = dslContext
            .insertInto(USERS)
            .columns(
                USERS.TELEGRAM_USER_ID,
                USERS.CHAT_ID,
                USERS.USERNAME,
                USERS.FIRST_NAME,
                USERS.LAST_NAME,
                USERS.CURRENT_DIALOG_ID,
            )
            .values(
                user.telegramUserId,
                user.chatId,
                user.username,
                user.firstName,
                user.lastName,
                user.currentDialogId,
            )

        val userRecord = sql.returning().awaitFirst()

        return userRecord.toUser()
    }
}
