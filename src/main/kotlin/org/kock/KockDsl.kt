package org.kock

import org.kock.matcher.AnyMatcher

inline fun <reified T> kock(): T = KockCreator().create(T::class.java)

fun any(): Any {
    InterceptState.newMatcher = AnyMatcher::class.java
    return Any()
}

fun anyInt(): Int {
    any()
    return 0
}

inline fun <reified T: Any> spy(): T {
    return kock()
}
