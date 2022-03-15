package org.kock.Matcher

import java.lang.reflect.Method

abstract class Matcher {
    protected val results = ArrayDeque<Any?>()
    abstract fun match(mock: Any, method: Method, args: Array<Any>): Boolean

    fun getValue(): Any? {
        if (results.size <= 1) {
            return results.first()
        }
        return results.removeFirst()
    }

    fun addReturnValue(value: Any?) {
        results.add(value);
    }
}

fun getSignature(method: Method): String {
    val f = method.name;
//    f.isAccessible = true
//    val sigature: String = f.get(method) as String
    return f
}