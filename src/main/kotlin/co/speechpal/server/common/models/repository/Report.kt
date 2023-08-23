package co.speechpal.server.common.models.repository

import co.speechpal.server.common.models.domain.SentenceCheckResult
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "reports")
data class Report(
    @Id val id: String? = null,
    val report: List<SentenceCheckResult>,
    @Field("has_errors")
    val hasErrors: Boolean,
)
