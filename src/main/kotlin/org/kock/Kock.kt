package org.kock

import org.kock.Matcher.AnyMatcher

inline fun <reified T> kock(): T {
    val kock = KockCreator()
    return kock.create(T::class.java)
}

fun every(f: () -> Any): Builder {
    return Builder(f)
}

fun any(): Any {
    InterceptState.newMatcher = AnyMatcher::class.java
    return Any()
}

fun anyInt(): Int {
    any()
    return 0
}