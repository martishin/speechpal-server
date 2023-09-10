package co.speechpal.server.common.repositories.dialogs

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.speechpal.server.common.errorhandling.ExceptionHandler
import co.speechpal.server.common.models.domain.dialogs.Dialog
import co.speechpal.server.common.models.domain.dialogs.NewDialog
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.jooq.speechpal.Tables.DIALOGS
import co.speechpal.server.jooq.speechpal.Tables.USERS
import co.speechpal.server.jooq.speechpal.tables.records.DialogsRecord
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class DefaultDialogsRepository(
    private val dslContext: DSLContext,
    private val exceptionHandler: ExceptionHandler,
) : DialogsRepository {
    companion object {
        private val objectMapper = jacksonObjectMapper()
    }

    override suspend fun findById(dialogId: Int): Either<DomainError, Dialog?> {
        return try {
            val sql = dslContext.selectFrom(DIALOGS)
                .where(DIALOGS.ID.eq(dialogId))

            val foundRecord = sql
                .awaitFirstOrNull()

            foundRecord?.toDialog().right()
        } catch (e: Exception) {
            exceptionHandler
                .handleDbException(e, "Error when fetching dialog from db")
                .left()
        }
    }

    override suspend fun create(newDialog: NewDialog): Either<DomainError, Dialog> {
        return try {
            dslContext.transactionCoroutine {
                val trxDSLContext = it.dsl()
                val sql = trxDSLContext
                    .insertInto(DIALOGS)
                    .columns(
                        DIALOGS.MODEL,
                        DIALOGS.USER_ID,
                    )
                    .values(
                        newDialog.model,
                        newDialog.userId,
                    )

                val createdRecord = sql
                    .returning()
                    .awaitFirst()

                trxDSLContext
                    .update(USERS)
                    .set(USERS.CURRENT_DIALOG_ID, createdRecord.id)
                    .where(USERS.ID.eq(newDialog.userId))
                    .awaitFirst()

                createdRecord.toDialog().right()
            }
        } catch (e: Exception) {
            exceptionHandler
                .handleDbException(e, "Error when creating dialog in db")
                .left()
        }
    }

    override suspend fun save(dialog: Dialog): Either<DomainError, Dialog> {
        return try {
            val sql = dslContext
                .update(DIALOGS)
                .set(DIALOGS.MODEL, dialog.model)
                .set(DIALOGS.MESSAGES, JSON.valueOf(objectMapper.writeValueAsString(dialog.messages)))
                .where(DIALOGS.ID.eq(dialog.id))

            val updatedRecord = sql
                .returning()
                .awaitFirst()

            updatedRecord.toDialog().right()
        } catch (e: Exception) {
            exceptionHandler
                .handleDbException(e, "Error when saving dialog in db")
                .left()
        }
    }

    private fun DialogsRecord.toDialog(): Dialog {
        return Dialog(
            this.get("id", Int::class.java),
            objectMapper.readValue(this.get("messages", String::class.java) ?: "[]"),
            this.get("model", String::class.java),
            this.get("created_at", Instant::class.java),
            this.get("updated_at", Instant::class.java),
            this.get("user_id", Int::class.java),
        )
    }
}
