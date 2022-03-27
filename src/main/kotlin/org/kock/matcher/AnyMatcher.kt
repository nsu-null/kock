package org.kock.matcher

import java.lang.reflect.Method

class AnyMatcher(
    private val className: String,
    private val methodSignature: String,
    private val arguments: Array<Any>
) : Matcher() {
    override fun matches(mock: Any, method: Method, args: Array<Any>): Boolean {
        if (getSignature(method) == methodSignature
            && className == mock::class.qualifiedName
            && args.size == arguments.size
        ) {
            for (i in args.indices) {
                if (args[i]::class != arguments[i]::class) {
                    return false
                }
            }
            return true
        }
        return false
    }
}
