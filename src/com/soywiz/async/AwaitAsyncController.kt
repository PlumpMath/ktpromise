package com.soywiz.async


class AwaitAsyncController<T : Any> {
    private val deferred = Promise.Deferred<T>()
    val promise = deferred.promise

    suspend fun <T : Any> await(promise: Promise<T>, c: Continuation<T>) {
        promise.then { c.resume(it) }.otherwise { c.resumeWithException(it) }
    }

    suspend fun <T : Any> awaitTask(callback: () -> T, c: Continuation<T>) {
        /*
        val deferred = Promise.Deferred<T>()
        Thread {
            try {
                val result = callback()
                deferred.resolve(result)
            } catch (t: Throwable) {
                deferred.reject(t)
            }
        }.start()
        */

        Thread {
            try {
                val result = callback()
                EventLoop.impl.queue { c.resume(result) }
            } catch (t: Throwable) {
                EventLoop.impl.queue { c.resumeWithException(t) }
            }
        }.start()

    }

    operator fun handleResult(v: T, c: Continuation<Nothing>) = deferred.resolve(v)
    operator fun handleException(t: Throwable, c: Continuation<Nothing>) = deferred.reject(t)
}