package com.stelmashchuk.remark.data

@Suppress("TooGenericExceptionCaught")
suspend fun <T : Any> apiCall(block: suspend () -> T): Result<T> {
  return try {
    Result.success(block())
  } catch (e: Exception) {
    Result.failure(e)
  }
}

fun <T> Result<T>.ifSuccess(block: (T) -> Unit) {
  this.getOrNull()?.let(block)
}

fun <T> Result<T>.ifError(block: (Throwable) -> Unit) {
  this.exceptionOrNull()?.let(block)
}
