package com.example.news.data

sealed class RequestResult<E>(open val data: E? = null) {
    class InProgress<E>(data: E? = null) : RequestResult<E>(data)
    class Success<E>(override val data: E) : RequestResult<E>(data)
    class Error<E>(data: E? = null, val error: Throwable? = null) : RequestResult<E>(data)
}

internal fun <T : Any> RequestResult<T?>.requireData(): T = checkNotNull(data)

fun <I, O> RequestResult<I>.map(mapper: (I) -> O): RequestResult<O> {
    return when (this) {
        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
        is RequestResult.Success -> RequestResult.Success(mapper(data))
    }
}

internal fun <T> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}
