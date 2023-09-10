package co.speechpal.server.api.models.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

data class ErrorResponse(
    @JsonIgnore
    val httpStatus: HttpStatus,
    val message: String,
)
