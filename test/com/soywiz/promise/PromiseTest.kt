package com.soywiz.promise

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
                Thread.sleep(100L)
                results += "c"
            }
            results += "d"
            10
        }.syncWait()
        results += "$res"

        Assert.assertEquals("a:b:c:d:10", results.joinToString(":"))
    }
}

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
    while (!done) Thread.sleep(20L)
    if (error != null) throw error!!
    return result!!
}