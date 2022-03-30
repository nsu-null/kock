package org.kock

import org.kock.matchers.Matcher
import org.kock.matchers.simpleArgumentMatcher
import java.lang.reflect.Method

private val threadLocalState = ThreadLocal<InterceptState>().apply { set(InterceptState()) }
val CurrentInterceptState: InterceptState
    get() = threadLocalState.get()

class InterceptState {
    var returnValue: Any? = null
    var builder: StubbingContext? = null
    var newMatcher: Matcher = Matcher()
    var isEveryRequest = false

    var isVerifyQuery = false
    var verifyQueries = listOf<InvocationDetails>()
    var verifyAnswer = listOf<InvocationDetails>()
}

class KockInterceptor(val spy: Any?) {
    private val recordedInvocations: MutableList<InvocationDetails> = mutableListOf()
    private val methodToMatcher = mutableMapOf<Method, ArrayList<Matcher>>().withDefault { ArrayList() }
    private var lastCalledBuilder: Any? = null

    operator fun invoke(mock: Any?, method: Method, args: Array<Any?>): Any? {
        when {
            CurrentInterceptState.isVerifyQuery -> {
                CurrentInterceptState.verifyQueries += InvocationDetails(mock, method.name, args)
                CurrentInterceptState.verifyAnswer = recordedInvocations
                return getDefaultValue(method.returnType)
            }
            CurrentInterceptState.isEveryRequest -> {
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
        if (CurrentInterceptState.builder != lastCalledBuilder) {
            var matcher = CurrentInterceptState.newMatcher
            CurrentInterceptState.newMatcher = Matcher()
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
        matcher.addReturnValue(CurrentInterceptState.returnValue)
        lastCalledBuilder = CurrentInterceptState.builder
        CurrentInterceptState.returnValue = null
        CurrentInterceptState.isEveryRequest = false
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
