package org.kock

import org.kock.VerifyingContext.Mode.*
import java.io.Closeable
import java.util.*

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

        fun List<InvocationDetails>.sortedByTime(): List<InvocationDetails>
                = this.sortedBy { it.time }

        groups.map { group ->
            with(group) {
                InvocationGroup(
                    mode,
                    queries.sortedByTime(),
                    actualInvocations.sortedByTime()
                )
            }
        }
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
fun verify(block: VerifyingContext.() -> Unit) {
    val verifyingContext = getVerificationInfo(block)

    verifyAssert(verifyingContext.groups.all { group ->
        val (mode, queries, actualInvocations) = group
        when (mode) {
            EXACT_ORDER ->
                Collections.indexOfSubList(actualInvocations, queries) != -1
            ANY_ORDER -> queries.all { query ->
                actualInvocations.any { it isLike query }
            }
        }
    })
}

fun verifyNot(block: VerifyingContext.() -> Unit) {
    try {
        verify(block = block)
    } catch (_: VerificationException) {
        return
    }
    throw VerificationException()
}

fun verifyTimes(block: VerifyingContext.() -> Unit): Int {
    val verifyingContext = getVerificationInfo(block)
    require(verifyingContext.groups.size == 1) { "State switching is not allowed for verifyTimes" }
    val queries = verifyingContext.groups[0].queries
    require(queries.size == 1) {
        "Bad queries for verifytimes. Should only be 1 query"
    }
    return queries.size
}

infix fun Int.isExactly(times: Int) = verifyAssert(this == times)

infix fun Int.isMoreThan(times: Int) = verifyAssert(this > times)

infix fun Int.isLessThan(times: Int) = verifyAssert(this < times)
