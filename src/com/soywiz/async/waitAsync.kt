package com.soywiz.async

fun waitAsync(ms: Int): Promise<Unit> {
    val deferred = Promise.Deferred<Unit>()
    EventLoop.setTimeout(ms) { deferred.resolve(Unit) }
    return deferred.promise
}