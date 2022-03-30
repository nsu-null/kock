package dsl

import org.kock.CurrentInterceptState


fun arrEq(array: ByteArray): ByteArray {
    CurrentInterceptState.newMatcher.addArgumentMatcher { array.contentEquals(it as ByteArray?) }
    return array
}

fun arrEq(array: IntArray): IntArray {
    CurrentInterceptState.newMatcher.addArgumentMatcher { array.contentEquals(it as IntArray?) }
    return array
}