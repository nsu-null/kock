package dsl

import org.kock.InterceptState


fun arrEq(array: ByteArray): ByteArray {
    InterceptState.newMatcher.addArgumentMatcher { array.contentEquals(it as ByteArray?) }
    return array
}

fun arrEq(array: IntArray): IntArray {
    InterceptState.newMatcher.addArgumentMatcher { array.contentEquals(it as IntArray?) }
    return array
}