package org.kock

import java.time.LocalDateTime

data class InvocationDetails(
    val obj: Any,
    val methodName: String,
    val arguments: Array<Any>
) {
    val time: LocalDateTime = LocalDateTime.now()!!
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InvocationDetails

        if (obj != other.obj) return false
        if (methodName != other.methodName) return false
        if (!arguments.contentEquals(other.arguments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + arguments.contentHashCode()
        return result
    }
}

infix fun InvocationDetails.isLike(other: InvocationDetails): Boolean
        = arguments.contentEquals(other.arguments)
        && methodName == other.methodName
