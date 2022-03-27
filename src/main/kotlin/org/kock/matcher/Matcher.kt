package org.kock.matcher

import java.lang.reflect.Method

abstract class Matcher {
    private val results = ArrayDeque<Any?>()
    abstract fun matches(mock: Any, method: Method, args: Array<Any>): Boolean

    fun getValue(): Any? {
        if (results.size <= 1) {
            return results.first()
        }
        return results.removeFirst()
    }

    fun addReturnValue(value: Any?) {
        results.add(value)
    }
}

fun getSignature(method: Method): String {
    return method.name
}
