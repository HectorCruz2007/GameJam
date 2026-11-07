package com.zukesito.gamejam.data.remote

import com.zukesito.gamejam.domain.error.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

// Runs a network call and turns failures into AppException
internal suspend fun <T> safeApiCall(
    onUnauthorized: () -> Unit = {},
    block: suspend () -> T
): Result<T> = try {
    Result.success(block())
} catch (exception: CancellationException) {
    throw exception
} catch (exception: HttpException) {
    if (exception.code() == 401) onUnauthorized()
    Result.failure(exception.toAppException())
} catch (exception: IOException) {
    Result.failure(AppException.Network())
} catch (exception: SerializationException) {
    Result.failure(AppException.Unknown())
} catch (exception: Exception) {
    // Any other failure, for example a bad date format, must not crash the app
    Result.failure(AppException.Unknown())
}

private fun HttpException.toAppException(): AppException = when (code()) {
    401 -> AppException.Unauthorized()
    404 -> AppException.NotFound()
    409 -> AppException.Conflict()
    400, 422 -> AppException.InvalidData()
    else -> AppException.Unknown()
}
