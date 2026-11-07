package com.zukesito.gamejam.ui.common

import androidx.annotation.StringRes
import com.zukesito.gamejam.R
import com.zukesito.gamejam.domain.error.AppException

// Default user message for each domain error
@StringRes
fun Throwable.toMessageRes(): Int = when (this) {
    is AppException.Network -> R.string.error_network
    is AppException.Unauthorized -> R.string.error_session_expired
    is AppException.NotFound -> R.string.error_not_found
    is AppException.InvalidData -> R.string.error_invalid_data
    else -> R.string.error_unknown
}
