package org.kock

import org.kock.matchers.Matcher

class StubbingContext(val stubbingBlock: () -> Unit)

private fun StubbingContext.stubb(resultToEmit: Any?) {
    CurrentInterceptState.isEveryRequest = true
    CurrentInterceptState.builder = this
    CurrentInterceptState.returnValue = resultToEmit
    CurrentInterceptState.newMatcher = Matcher()
    stubbingBlock()
}

fun every(block: () -> Unit): StubbingContext = StubbingContext(block)

infix fun StubbingContext.returns(value: Any?) = this.stubb(value)

infix fun StubbingContext.returnsMany(valuesToEmit: List<Any?>) {
    valuesToEmit.forEach { valueToEmit -> this returns valueToEmit }
}

infix fun <T> T?.then(next: T?): List<T?> = listOf(this, next)

infix fun <T> List<T?>.then(next: T?): List<T?> = this + next
