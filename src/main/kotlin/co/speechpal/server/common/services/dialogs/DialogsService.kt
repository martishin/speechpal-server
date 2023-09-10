package co.speechpal.server.common.services.dialogs

import arrow.core.Either
import co.speechpal.server.common.models.domain.dialogs.Dialog
import co.speechpal.server.common.models.domain.dialogs.NewDialog
import co.speechpal.server.common.models.errors.DomainError

interface DialogsService {
    suspend fun findById(dialogId: Int): Either<DomainError, Dialog?>
    suspend fun create(newDialog: NewDialog): Either<DomainError, Dialog>
    suspend fun save(dialog: Dialog): Either<DomainError, Dialog>
}
