package com.soywiz.async

fun waitAsync(ms: Int): Promise<Unit> {
    val deferred = Promise.Deferred<Unit>()
    val closeable = EventLoop.setTimeout(ms) { deferred.resolve(Unit) }
    return deferred.promise.cancelled { closeable.close() }
}