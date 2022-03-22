package org.kock.matcher

import org.kock.InvocationDetails
import java.lang.reflect.Method
import java.util.*


class SimpleArgumentMatcher(
    var className: String,
    var methodSignature: String,
    var arguments: Array<Any>
) : Matcher() {


    override fun matches(mock: Any, method: Method, args: Array<Any>): Boolean {
        return mock::class.qualifiedName == className
                && getSignature(method) == methodSignature
                && Arrays.equals(args, arguments)
    }



    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val behaviour = o as InvocationDetails<*>
        return className == behaviour.className &&
                methodSignature == behaviour.methodName &&
                Arrays.equals(arguments, behaviour.arguments)
    }

    override fun hashCode(): Int {
        var result = Objects.hash(className, methodSignature)
        result = 31 * result + Arrays.hashCode(arguments)
        return result
    }

}