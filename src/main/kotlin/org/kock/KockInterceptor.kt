package org.kock

import org.kock.matchers.Matcher
import org.kock.matchers.simpleArgumentMatcher
import java.lang.reflect.Method

object InterceptState {
    var returnValue: Any? = null
    var builder: StubbingContext? = null
    var newMatcher: Matcher = Matcher()
    var isEveryRequest = false

    var isVerifyQuery = false
    var verifyQueries = listOf<InvocationDetails>()
    var verifyAnswer = listOf<InvocationDetails>() // stores all invocations for the current
}

class KockInterceptor(val spy: Any?) {
    private val recordedInvocations: MutableList<InvocationDetails> = mutableListOf()
    private val methodToMatcher = mutableMapOf<Method, ArrayList<Matcher>>().withDefault { ArrayList() }
    private var lastCalledBuilder: Any? = null

    operator fun invoke(mock: Any?, method: Method, args: Array<Any?>): Any? {
        when {
            InterceptState.isVerifyQuery -> {
                InterceptState.verifyQueries += InvocationDetails(mock, method.name, args)
                InterceptState.verifyAnswer = recordedInvocations
                return getDefaultValue(method.returnType)
            }
            InterceptState.isEveryRequest -> {
                grabNewCallData(mock, method, args)
                return getDefaultValue(method.returnType)
            }
            // regular invocation
            else -> {
                recordedInvocations += InvocationDetails(mock, method.name, args)
                for (matcher in methodToMatcher.getValue(method).reversed()) {
                    if (matcher.matches(args)) {
                        return matcher.getValue()
                    }
                }
                if (spy != null) {
                    return method.invoke(spy, *args)
                }
                return getDefaultValue(method.returnType)
            }
        }
    }

    private fun grabNewCallData(mock: Any?, method: Method, args: Array<Any?>) {
        if (InterceptState.builder != lastCalledBuilder) {
            var matcher = InterceptState.newMatcher
            InterceptState.newMatcher = Matcher()
            if (matcher.getSize() == 0) {
                matcher = simpleArgumentMatcher(args)
            }
            if (args.size != matcher.getSize()) {
                throw IllegalArgumentException("You must provide arguments matchers with size the same as arguments of mockable function")
            }
            if (!methodToMatcher.containsKey(method)) {
                methodToMatcher.put(method, ArrayList())
            }
            methodToMatcher.getValue(method).add(matcher)
        }

        val matcher = methodToMatcher.getValue(method).last()
        matcher.addReturnValue(InterceptState.returnValue)
        lastCalledBuilder = InterceptState.builder
        InterceptState.returnValue = null
        InterceptState.isEveryRequest = false
    }
}

fun getDefaultValue(clazz: Class<*>): Any? {
    if (!clazz.isPrimitive) {
        return null
    }
    return when (clazz.name) {
        "boolean" -> false
        "byte", "char", "short", "int", "long", "float", "double" -> 0
        else -> null
    }
}
