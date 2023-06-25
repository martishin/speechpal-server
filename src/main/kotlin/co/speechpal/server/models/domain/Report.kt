package co.speechpal.server.models.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "reports")
data class Report(
    @Id val id: String? = null,
    val report: List<Sentence>,
)
