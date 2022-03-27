package org.kock.matcher

import org.kock.InvocationDetails
import java.lang.reflect.Method
import java.util.*


class SimpleArgumentMatcher(
    private val className: String,
    private val methodSignature: String,
    private val arguments: Array<Any>
) : Matcher() {

    override fun matches(mock: Any, method: Method, args: Array<Any>): Boolean {
        return mock::class.qualifiedName == className
                && getSignature(method) == methodSignature
                && args.contentEquals(arguments)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val behaviour = other as InvocationDetails
        return className == behaviour.obj.javaClass.name &&
                methodSignature == behaviour.methodName &&
                arguments.contentEquals(behaviour.arguments)
    }

    override fun hashCode(): Int {
        var result = Objects.hash(className, methodSignature)
        result = 31 * result + arguments.contentHashCode()
        return result
    }
}
