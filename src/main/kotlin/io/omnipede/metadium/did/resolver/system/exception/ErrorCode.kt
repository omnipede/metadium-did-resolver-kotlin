package io.omnipede.metadium.did.resolver.system.exception

enum class ErrorCode(// Http status
    val status: Int, // Error code
    val defaultMessage: String
) {
    BAD_REQUEST(400, "Bad request"),
    NOT_FOUND(404,  "404 not found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
}
