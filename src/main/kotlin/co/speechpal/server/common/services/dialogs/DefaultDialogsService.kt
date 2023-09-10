package co.speechpal.server.common.services.dialogs

import arrow.core.Either
import co.speechpal.server.common.models.domain.dialogs.Dialog
import co.speechpal.server.common.models.domain.dialogs.NewDialog
import co.speechpal.server.common.models.errors.DomainError
import co.speechpal.server.common.repositories.dialogs.DialogsRepository
import org.springframework.stereotype.Service

@Service
class DefaultDialogsService(
    private val dialogsRepository: DialogsRepository,
) : DialogsService {
    override suspend fun findById(dialogId: Int): Either<DomainError, Dialog?> {
        return dialogsRepository.findById(dialogId)
    }

    override suspend fun create(newDialog: NewDialog): Either<DomainError, Dialog> {
        return dialogsRepository.create(newDialog)
    }

    override suspend fun save(dialog: Dialog): Either<DomainError, Dialog> {
        return dialogsRepository.save(dialog)
    }
}
