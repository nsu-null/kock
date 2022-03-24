package org.kock

import org.kock.VerifyingContext.Mode.*
import java.io.Closeable

class VerifyingContext : Closeable {
    lateinit var queries: List<InvocationDetails>
    lateinit var invokations: List<InvocationDetails>
    private var mode = ANY_ORDER
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
        invokations = InterceptState.verifyAnswer
        queries = InterceptState.verifyQueries
        InterceptState.isVerifyQuery = false
        InterceptState.verifyAnswer = emptyList()
        InterceptState.verifyQueries = emptyList()
    }

    enum class Mode {
        EXACT_ORDER, ANY_ORDER
    }
}

class VerificationException : Exception()

/**
 * Returns list of invocations
 */
private fun getVerificationInfo(block: VerifyingContext.() -> Unit): VerifyingContext
        = VerifyingContext().apply { use { it.block() } }

private fun verifyAssert(value: Boolean) = if (!value) throw VerificationException() else Unit

/**
 * Throws VerificationException if unverified
 */
fun verify(inverse: Boolean = false, block: VerifyingContext.() -> Unit) {
    val context = getVerificationInfo(block)
    context.queries.forEach { query ->
        verifyAssert(context.invokations.any {
            it.obj === query.obj
            it.arguments.contentEquals(query.arguments)
            it.methodName == query.methodName
        })
    }
}

fun verifyNot(block: VerifyingContext.() -> Unit) {
    try {
        verify(block = block)
    } catch (_: VerificationException) {
        return
    }
    throw VerificationException()
}

fun verifyTimes(inverse: Boolean = false, block: VerifyingContext.() -> Unit): Int {
    val verificationInfo = getVerificationInfo(block)
    require(verificationInfo.queries.size == 1) { "Bad queries for verifytimes. Should only be 1 query" }

    return verificationInfo.invokations.size
}

infix fun Int.isExactly(times: Int) = verifyAssert(this == times)

infix fun Int.isMoreThan(times: Int) = verifyAssert(this > times)

infix fun Int.isLessThan(times: Int) = verifyAssert(this < times)
