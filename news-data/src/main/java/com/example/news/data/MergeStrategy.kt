package com.example.news.data

interface MergeStrategy<E> {
    fun merge(right: E, left: E): E
}

internal class RequestResponseMergeStrategy<T> : MergeStrategy<RequestResult<T>> {

    @Suppress("CyclomaticComplexMethod")
    override fun merge(right: RequestResult<T>, left: RequestResult<T>): RequestResult<T> {
        return when {
            right is RequestResult.InProgress && left is RequestResult.InProgress -> merge(
                right,
                left
            )

            right is RequestResult.InProgress && left is RequestResult.Success -> merge(right, left)
            right is RequestResult.InProgress && left is RequestResult.Error -> merge(right, left)

            right is RequestResult.Success && left is RequestResult.InProgress -> merge(right, left)
            right is RequestResult.Success && left is RequestResult.Success -> merge(right, left)
            right is RequestResult.Success && left is RequestResult.Error -> merge(right, left)

            right is RequestResult.Error && left is RequestResult.InProgress -> merge(right, left)
            right is RequestResult.Error && left is RequestResult.Success -> merge(right, left)
            right is RequestResult.Error && left is RequestResult.Error -> merge(right, left)
            else -> error("Unimplemented branch")
        }
    }

    private fun merge(
        right: RequestResult.InProgress<T>,
        left: RequestResult.InProgress<T>
    ): RequestResult.InProgress<T> {
        return when {
            left.data != null -> RequestResult.InProgress(left.data)
            else -> RequestResult.InProgress(right.data)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        right: RequestResult.InProgress<T>,
        left: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(left.data)
    }

    private fun merge(
        right: RequestResult.InProgress<T>,
        left: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(
            data = right.data,
            error = left.error
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        right: RequestResult.Success<T>,
        left: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(right.data)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        right: RequestResult.Success<T>,
        left: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.Success(right.data)
    }

    private fun merge(
        right: RequestResult.Success<T>,
        left: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(right.data, left.error)
    }

    private fun merge(
        right: RequestResult.Error<T>,
        left: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.Error(
            data = left.data,
            error = right.error
        )
    }

    private fun merge(
        right: RequestResult.Error<T>,
        left: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.Error(
            data = left.data,
            error = right.error
        )
    }

    private fun merge(
        right: RequestResult.Error<T>,
        left: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(
            data = left.data,
            error = right.error
        )
    }
}
