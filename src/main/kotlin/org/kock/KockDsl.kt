package org.kock

import org.kock.matcher.ArgumentMatcher
import org.objenesis.ObjenesisStd


inline fun <reified T> kock(): T = KockCreator().create(T::class.java, null)

inline fun <reified T> any(): T {
    InterceptState.newMatcher.addArgumentMatcher { true }
    val objenesis = ObjenesisStd()
    val result: T = objenesis.newInstance(T::class.java)
    return result
}

inline fun <reified T> any(crossinline f: (T) -> Boolean): T = anyUnchecked { f(it!!) }

inline fun <reified T> anyUnchecked(matcher: ArgumentMatcher<T>): T {
    InterceptState.newMatcher.addArgumentMatcher { matcher.matches(it as T?) }
    val objenesis = ObjenesisStd()
    val result: T = objenesis.newInstance(T::class.java)
    return result
}


fun <T> fixed(arg: T): T {
    InterceptState.newMatcher.addArgumentMatcher { arg!!.equals(it) }
    return arg
}

fun fixedNull(): Any? = null


inline fun <reified T : Any> spy(obj: T): T {
    return KockCreator().create(T::class.java, obj)
}

