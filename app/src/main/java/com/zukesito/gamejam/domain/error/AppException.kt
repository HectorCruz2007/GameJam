package com.zukesito.gamejam.domain.error

// Errors the UI can react to without knowing HTTP details
sealed class AppException : Exception() {
    class Unauthorized : AppException()
    class Conflict : AppException()
    class NotFound : AppException()
    class InvalidData : AppException()
    class Network : AppException()
    class Unknown : AppException()
}
