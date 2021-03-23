package com.stelmashchuk.remark.data

public class Result<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?,
) {
  public val isSuccess: Boolean get() = value !is Failure
  public val isFailure: Boolean get() = value is Failure

  public fun getOrNull(): T? =
      when {
        isFailure -> null
        else -> value as T
      }

  public fun exceptionOrNull(): Throwable? =
      when (value) {
        is Failure -> value.exception
        else -> null
      }

  public override fun toString(): String =
      when (value) {
        is Failure -> value.toString() // "Failure($exception)"
        else -> "Success($value)"
      }

  public companion object {
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("success")
    public inline fun <T> success(value: T): Result<T> =
        Result(value)

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("failure")
    public inline fun <T> failure(exception: Throwable): Result<T> =
        Result(createFailure(exception))
  }

  internal class Failure(
      @JvmField
      val exception: Throwable,
  ) {
    override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
    override fun hashCode(): Int = exception.hashCode()
    override fun toString(): String = "Failure($exception)"
  }
}

fun createFailure(exception: Throwable): Any =
    Result.Failure(exception)

internal fun Result<*>.throwOnFailure() {
  if (value is Result.Failure) throw value.exception
}

public inline fun <R> runCatching(block: () -> R): Result<R> {
  return try {
    Result.success(block())
  } catch (e: Throwable) {
    Result.failure(e)
  }
}

public inline fun <T, R> T.runCatching(block: T.() -> R): Result<R> {
  return try {
    Result.success(block())
  } catch (e: Throwable) {
    Result.failure(e)
  }
}

public fun <T> Result<T>.getOrThrow(): T {
  throwOnFailure()
  return value as T
}

public inline fun <R, T : R> Result<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
  return when (val exception = exceptionOrNull()) {
    null -> value as T
    else -> onFailure(exception)
  }
}

public fun <R, T : R> Result<T>.getOrDefault(defaultValue: R): R {
  if (isFailure) return defaultValue
  return value as T
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
private inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
): R {
  return when (val exception = exceptionOrNull()) {
    null -> onSuccess(value as T)
    else -> onFailure(exception)
  }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
public inline fun <R, T> Result<T>.map(transform: (value: T) -> R): Result<R> {
  return when {
    isSuccess -> Result.success(transform(value as T))
    else -> Result(value)
  }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
public inline fun <R, T> Result<T>.mapCatching(transform: (value: T) -> R): Result<R> {
  return when {
    isSuccess -> runCatching { transform(value as T) }
    else -> Result(value)
  }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
public inline fun <R, T : R> Result<T>.recover(transform: (exception: Throwable) -> R): Result<R> {
  return when (val exception = exceptionOrNull()) {
    null -> this
    else -> Result.success(transform(exception))
  }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
public inline fun <R, T : R> Result<T>.recoverCatching(transform: (exception: Throwable) -> R): Result<R> {
  return when (val exception = exceptionOrNull()) {
    null -> this
    else -> runCatching { transform(exception) }
  }
}

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Result.isFailure].
 * Returns the original `Result` unchanged.
 */
public inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
  exceptionOrNull()?.let { action(it) }
  return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
public inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
  if (isSuccess) action(value as T)
  return this
}

// -------------------
