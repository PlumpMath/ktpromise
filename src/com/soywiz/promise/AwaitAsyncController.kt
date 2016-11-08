package com.soywiz.promise

class AwaitAsyncController<T : Any> {
    private val deferred = Promise.Deferred<T>()
    val promise = deferred.promise

    suspend fun <T : Any> await(promise: Promise<T>, c: Continuation<T>) {
        promise.then { c.resume(it) }.otherwise { c.resumeWithException(it) }
    }

    operator fun handleResult(v: T, c: Continuation<Nothing>) {
        deferred.resolve(v)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        deferred.reject(t)
    }
}