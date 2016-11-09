package com.soywiz.promise

import com.soywiz.async.Signal
import org.junit.Assert
import org.junit.Test

class SignalTest {
    @Test
    fun name1() {
        var result = ""
        val signal = Signal<Int>()
        signal { result += "$it" }
        signal.once { result += "[$it]" }
        signal(1)
        signal(2)
        signal(3)
        Assert.assertEquals("1[1]23", result)
    }

}