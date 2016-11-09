package com.soywiz.async

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
        fun setTimeout(ms: Int, callback: () -> Unit) = impl.setTimeout(ms, callback)
        fun setInterval(ms: Int, callback: () -> Unit) = impl.setInterval(ms, callback)
    }

    open fun queue(callback: () -> Unit) {
        callback()
    }

    open fun step() {
        //step()
    }

    open fun setTimeout(ms: Int, callback: () -> Unit) {
        Thread {
            Thread.sleep(ms.toLong())
            queue(callback)
        }.start()
    }

    open fun setInterval(ms: Int, callback: () -> Unit) {
        Thread {
            while (true) {
                Thread.sleep(ms.toLong())
                queue(callback)
            }
        }.start()
    }
}