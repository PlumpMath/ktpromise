package com.soywiz.async

import java.util.*

class Promise<T : Any> {
    internal var resolved = false
    internal var value: T? = null
    internal var error: Throwable? = null
    internal val valueCallbacks = LinkedList<(T) -> Unit>()
    internal val errorCallbacks = LinkedList<(Throwable) -> Unit>()

    fun then(callback: (T) -> Unit): Promise<T> {
        valueCallbacks += callback
        _check()
        return this
    }

    fun otherwise(callback: (Throwable) -> Unit): Promise<T> {
        errorCallbacks += callback
        _check()
        return this
    }

    private fun _check() {
        if (resolved) {
            if (error != null) {
                while (errorCallbacks.isNotEmpty()) {
                    val callback = errorCallbacks.remove()
                    EventLoop.impl.queue { callback(error!!) }
                }
            } else if (value != null) {
                while (valueCallbacks.isNotEmpty()) {
                    val callback = valueCallbacks.remove()
                    EventLoop.impl.queue { callback(value!!) }
                }
            }
            errorCallbacks.clear()
            valueCallbacks.clear()
        }
    }

    class Deferred<T : Any> {
        val promise: Promise<T> = Promise()
        fun resolve(value: T) {
            promise.resolved = true
            promise.value = value
            promise._check()
        }

        fun reject(error: Throwable) {
            promise.resolved = true
            promise.error = error
            if (promise.errorCallbacks.isEmpty()) {
                error.printStackTrace()
            }
            promise._check()
        }
    }
}
