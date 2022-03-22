package org.kock

import org.kock.VerifyingContext.Mode.*
import java.io.Closeable

class VerifyingContext : Closeable {
    private var mode = ANY_ORDER
    var times = 0
    var exactOrder: Nothing? = null
        get() {
            mode = EXACT_ORDER
            return field
        }
        private set
    var anyOrder: Nothing? = null
        get() {
            mode = ANY_ORDER
            return field
        }
        private set

    init {
        check(!InterceptState.isVerifyQuery) { "Verification has already started" }
        InterceptState.isVerifyQuery = true
    }

    override fun close() {
        InterceptState.isVerifyQuery = false
    }

    enum class Mode {
        EXACT_ORDER, ANY_ORDER
    }
}

class VerificationException : Exception()

private fun getVerificationInfo(block: VerifyingContext.() -> Unit): List<InvocationDetails> {
    VerifyingContext().use {
        it.block()
        return InterceptState.verifyAnswer
    }
}

private fun verifyAssert(value: Boolean) = if (!value) throw VerificationException() else Unit

/**
 * Throws VerificationException if unverified
 */
fun verify(inverse: Boolean = false, block: VerifyingContext.() -> Unit)
        = verifyAssert(getVerificationInfo(block).isNotEmpty())

fun verifyTimes(inverse: Boolean = false, block: VerifyingContext.() -> Unit): Int
        = getVerificationInfo(block).size

infix fun Int.isExactly(times: Int) = verifyAssert(this == times)

infix fun Int.isMoreThan(times: Int) = verifyAssert(this > times)

infix fun Int.isLessThan(times: Int) = verifyAssert(this < times)
