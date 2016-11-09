package com.soywiz.async

/*
fun <T : Any> Context.async(coroutine routine: AwaitAsyncController<T>.() -> Continuation<Unit>): Promise<T> {
    val controller = AwaitAsyncController<T>()
    val c = routine(controller)
    c.resume(Unit)
    return controller.promise
}
*/

fun <T : Any> async(coroutine routine: AwaitAsyncController<T>.() -> Continuation<Unit>): Promise<T> {
    val controller = AwaitAsyncController<T>()
    val c = routine(controller)
    c.resume(Unit)
    return controller.promise
}
