package org.kock

import java.io.Closeable

//TODO Pass information about ret type

class StubbingContext(resultToEmit: Any?) : Closeable {

    init {
        InterceptState.lock = true
        InterceptState.builder = this
        InterceptState.returnValue = resultToEmit
    }

    override fun close() {
        InterceptState.builder = null
        InterceptState.lock = false
    }
}

fun every(block: () -> Unit): () -> Unit {
    return block
}

infix fun (() -> Unit).returns(value: Any?) {
    StubbingContext(value).use {
        this()
    }
}

infix fun (() -> Unit).returnsMany(valuesToEmit: List<Any?>) {
    valuesToEmit.forEach { valueToEmit -> this.returns(valueToEmit) }
}

infix fun <T> T?.then(next: T?) : List<T?> = listOf(this, next)

infix fun <T> List<T?>.then(next: T?) : List<T?> = this + next
