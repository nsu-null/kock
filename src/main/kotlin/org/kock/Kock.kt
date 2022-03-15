package org.kock

import org.kock.KockCreator
import org.kock.Matcher.AnyMatcher

inline fun <reified T> kock(): T {
    var kock = KockCreator()
    return kock.create(T::class.java)
}

fun every(f: () -> Any): Builder {
    return Builder(f);
}

fun any(): Any {
    InterceptState.newMatcher = AnyMatcher::class.java
    return Any();
}

fun anyInt(): Int {
    any()
    return 0
}