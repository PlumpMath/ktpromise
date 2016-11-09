package com.soywiz.async

fun <T : Any> Promise<T>.syncWait(): T {
    var done = false
    var error: Throwable? = null
    var result: T? = null
    this.then {
        result = it
        done = true
    }.otherwise {
        error = it
        done = true
    }
    EventLoop.impl.step()
    while (!done) {
        EventLoop.impl.step()
        Thread.sleep(20L)
    }
    if (error != null) throw error!!
    return result!!
}