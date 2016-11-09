package com.soywiz.async

import java.io.Closeable

open class EventLoop {
    companion object {
        var impl = EventLoop()

        inline fun tempImpl(impl: EventLoop, callback: () -> Unit) {
            val old = this.impl
            this.impl = impl
            try {
                callback()
            } finally {
                this.impl = old
            }
        }

        fun queue(callback: () -> Unit) = impl.queue(callback)
        fun setTimeout(ms: Int, callback: () -> Unit): Closeable = impl.setTimeout(ms, callback)
        fun setInterval(ms: Int, callback: () -> Unit): Closeable = impl.setInterval(ms, callback)
    }

    open fun queue(callback: () -> Unit) {
        callback()
    }

    open fun step() {
        //step()
    }

    open fun setTimeout(ms: Int, callback: () -> Unit): Closeable {
        var closed = false
        val closeable = Closeable { closed = true }
        Thread {
            Thread.sleep(ms.toLong())
            if (!closed) queue(callback)
        }.start()
        return closeable
    }

    open fun setInterval(ms: Int, callback: () -> Unit): Closeable {
        var closed = false
        val closeable = Closeable { closed = true }
        Thread {
            while (!closed) {
                Thread.sleep(ms.toLong())
                if (!closed) queue(callback)
            }
        }.start()
        return closeable
    }
}