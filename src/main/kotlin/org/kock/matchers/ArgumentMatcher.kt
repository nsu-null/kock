package org.kock.matcher

import java.lang.reflect.Method

fun interface ArgumentMatcher<T> {
    fun matches(argument: T?): Boolean
}

fun getSignature(method: Method): String {
    return method.name
}


