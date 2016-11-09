package com.soywiz.promise

import com.soywiz.async.EventLoop
import com.soywiz.async.async
import com.soywiz.async.syncWait
import com.soywiz.async.waitAsync
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CancellationException

class PromiseTest {
    @Test
    fun testSimple() {
        val results = arrayListOf<String>()

        val res = async<Int> {
            results += "a"
            awaitTask {
                results += "b"
                Thread.sleep(20L)
                results += "c"
            }
            results += "d"
            10
        }.syncWait()
        results += "$res"

        Assert.assertEquals("a:b:c:d:10", results.joinToString(":"))
    }

    @Test
    fun testEventLoop() {
        val results = arrayListOf<String>()

        val res = async<Int> {
            results += "a"
            await(waitAsync(20))
            results += "b"
            10
        }.syncWait()
        results += "$res"

        Assert.assertEquals("a:b:10", results.joinToString(":"))
    }

    @Test
    fun testCancel() {
        val results = arrayListOf<String>()

        try {
            val prom = async<Int> {
                try {
                    results += "a"
                    await(waitAsync(5000))
                    results += "b"
                } catch (e: CancellationException) {
                    results += "CANCEL"
                    throw e
                }
                10
            }
            EventLoop.impl.step()
            Thread.sleep(50)
            prom.cancel()
            val res = prom.syncWait()
            results += "$res"
        } catch (e: Throwable) {
        }

        Assert.assertEquals("a:CANCEL", results.joinToString(":"))
    }
}
