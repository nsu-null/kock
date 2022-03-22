package org.kock

import org.kock.matcher.AnyMatcher

inline fun <reified T> kock(): T {
    val kock = KockCreator()
    return kock.create(T::class.java)
}

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

class VerifyingContext {
    private val list = mutableListOf<Any>()
    val isOK = true
    var times = 0
    var exactOrder: Unit? = null
        get() {
            println("sideeffect")
            return field
        }
        private set
    var anyOrder: Unit? = null
        get() {
            println("sideeffect")
            return field
        }
        private set
}

fun verify(inverse: Boolean = false, block: VerifyingContext.() -> Unit): Boolean {
    val context = VerifyingContext()
    context.block()
    return context.isOK
}

fun verifyTimes(inverse: Boolean = false, block: VerifyingContext.() -> Unit): VerifyingContext {
    val context = VerifyingContext()
    context.block()
    return context
}

infix fun VerifyingContext.isExactly(times: Int) {
    assert(this.times == times)
}

infix fun VerifyingContext.isMoreThan(times: Int) {
    assert(this.times > times)
}

infix fun VerifyingContext.isLessThan(times: Int) {
    assert(this.times < times)
}
