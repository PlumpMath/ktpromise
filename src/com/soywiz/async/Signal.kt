package com.soywiz.async

import java.io.Closeable
import java.util.*

class Signal<T> {
    private val callbacks = LinkedList<(T) -> Unit>()

    operator fun invoke(value: T) = EventLoop.queue {
        for (callback in synchronized(this) { callbacks.toList() }) {
            callback(value)
        }
    }

    fun once(callback: (T) -> Unit): Closeable {
        var closeable: Closeable? = null
        closeable = add {
            callback(it)
            closeable?.close()
        }
        return closeable
    }

    fun add(callback: (T) -> Unit): Closeable {
        synchronized(this) { callbacks += callback }
        return Closeable { callbacks -= callback }
    }

    operator fun invoke(callback: (T) -> Unit) = add(callback)

    operator fun plusAssign(callback: (T) -> Unit) {
        invoke(callback)
    }
}