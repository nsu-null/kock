package org.kock

import java.util.*

//TODO Pass information about ret type

class Builder(val f: () -> Any?) {
    fun then(value: Any?): Builder {
        InterceptState.lock = true
        InterceptState.builder = this
        InterceptState.returnValue = value
        f()
        return this
    }
}