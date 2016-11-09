package com.soywiz.promise

import com.soywiz.async.async
import com.soywiz.async.syncWait
import com.soywiz.async.waitAsync
import org.junit.Assert
import org.junit.Test

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
}
