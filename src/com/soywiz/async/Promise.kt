package com.soywiz.async

import java.util.*
import java.util.concurrent.CancellationException

class Promise<T : Any> {
	private var resolved = false
	private var cancelled = false
	private var value: T? = null
	private var error: Throwable? = null
	private val valueCallbacks = LinkedList<(T) -> Unit>()
	private val errorCallbacks = LinkedList<(Throwable) -> Unit>()
	private val cancelledHandlers = LinkedList<() -> Unit>()
	private val linkedPromises = arrayListOf<Promise<*>>()

	fun then(callback: (T) -> Unit) = this.apply {
		valueCallbacks += callback
		_check()
	}

	fun otherwise(callback: (Throwable) -> Unit) = this.apply {
		errorCallbacks += callback
		_check()
	}

	fun always(callback: () -> Unit) = this.apply {
		valueCallbacks += { callback() }
		errorCallbacks += { callback() }
		_check()
	}

	fun cancel() {
		if (resolved || cancelled) return
		cancelled = true

		while (cancelledHandlers.isNotEmpty()) {
			val callback = cancelledHandlers.remove()
			EventLoop.impl.queue { callback() }
		}

		for (child in linkedPromises.toList()) child.cancel()

		reject(CancellationException())
	}

	private fun resolve(value: T) {
		if (resolved) return
		this.resolved = true
		this.value = value
		this._check()
	}

	private fun reject(error: Throwable) {
		if (resolved) return
		this.resolved = true
		this.error = error
		//if (this.errorCallbacks.isEmpty() && linkedPromises.isEmpty()) {
		if (this.errorCallbacks.isEmpty() && error !is CancellationException) {
			System.err.println("Unhandled Promise exception:")
			error.printStackTrace()
		}
		this._check()
	}

	private fun linkTo(child: Promise<*>) {
		linkedPromises += child
	}

	private fun _check() {
		if (!resolved) return
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
		linkedPromises.clear()
	}

	fun cancelled(callback: () -> Unit) = this.apply { cancelledHandlers += callback }

	class Deferred<T : Any> {
		val promise: Promise<T> = Promise()

		fun resolve(value: T) = promise.resolve(value)
		fun reject(error: Throwable) = promise.reject(error)

		fun cancelled(callback: () -> Unit) = this.apply { promise.cancelledHandlers += callback }

		fun linkTo(child: Promise<*>) = promise.linkTo(child)
	}
}
