package org.kock

import org.kock.VerifyingContext.Mode.*
import java.io.Closeable

class VerifyingContext : Closeable {
    lateinit var groups: MutableList<InvocationGroup>
    private var currentMode = ANY_ORDER
    var exactOrder: Nothing? = null
        get() {
            currentMode = if (currentMode != EXACT_ORDER)
                run {
                    flushInterceptorState()
                    ANY_ORDER
                } else EXACT_ORDER
            return field
        }
        private set
    var anyOrder: Nothing? = null
        get() {
            currentMode = if (currentMode != ANY_ORDER)
                run {
                    flushInterceptorState()
                    EXACT_ORDER
                } else ANY_ORDER
            return field
        }
        private set

    init {
        check(!InterceptState.isVerifyQuery) { "Verification has already started" }
        InterceptState.isVerifyQuery = true
    }

    override fun close() {
        flushInterceptorState()
        InterceptState.isVerifyQuery = false
        InterceptState.verifyAnswer = emptyList()
        InterceptState.verifyQueries = emptyList()
    }

    private fun flushInterceptorState() {
        groups += InvocationGroup(currentMode,
            InterceptState.verifyQueries, InterceptState.verifyAnswer)
        InterceptState.verifyAnswer = emptyList()
        InterceptState.verifyQueries = emptyList()
    }

    enum class Mode {
        EXACT_ORDER, ANY_ORDER
    }

    data class InvocationGroup(val mode: Mode,
                          val queries: List<InvocationDetails>,
                          val actualInvocations: List<InvocationDetails>)
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
    context.groups.forEach { group ->
        val (mode, queries, actualInvocations) = group
        when (mode) {
            EXACT_ORDER -> queries.forEach { query ->
                TODO()
            }
            ANY_ORDER -> queries.forEach {

            }
        }
//        verifyAssert(context.groups.any {
//            it.obj === query.obj
//            it.arguments.contentEquals(query.arguments)
//            it.methodName == query.methodName
//        })
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

    return verificationInfo.groups.size
}

infix fun Int.isExactly(times: Int) = verifyAssert(this == times)

infix fun Int.isMoreThan(times: Int) = verifyAssert(this > times)

infix fun Int.isLessThan(times: Int) = verifyAssert(this < times)
