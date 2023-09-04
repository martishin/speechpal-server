package co.speechpal.server.common

import co.speechpal.server.common.models.domain.reports.TextCheckResult
import co.speechpal.server.common.models.domain.users.User
import co.speechpal.server.common.models.persistence.Report
import co.speechpal.server.jooq.speechpal.tables.records.UsersRecord
import java.time.Instant

fun TextCheckResult.toReport(id: String) =
    Report(
        id = id,
        report = this.report,
        hasErrors = this.hasErrors,
    )

fun UsersRecord.toUser(): User {
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
