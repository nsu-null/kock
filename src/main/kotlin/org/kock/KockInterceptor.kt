package org.kock

import org.kock.matcher.AnyMatcher
import org.kock.matcher.Matcher
import org.kock.matcher.SimpleArgumentMatcher
import org.kock.matcher.getSignature
import java.lang.reflect.Method

object InterceptState {
    var returnValue: Any? = null
    var builder: StubbingContext? = null
    var newMatcher: Class<out Matcher>? = null
    var lock = false

    var isVerifyQuery = false
    var verifyAnswer: List<InvocationDetails> = emptyList()
}

class KockInterceptor {
    private val recordedInvocations: MutableList<InvocationDetails> = mutableListOf()
    private val matchers = mutableListOf<Matcher>()
    private var lastCalledBuilder: Any? = null

    operator fun invoke(mock: Any, method: Method, args: Array<Any>): Any? {
        if (InterceptState.isVerifyQuery) {
            InterceptState.verifyAnswer = recordedInvocations
                .filter { it.obj === mock && it.methodName == method.name && it.arguments.contentEquals(args) }
            return getDefaultValue(method.returnType)
        } else {
            if (InterceptState.lock) {
                grabNewCallData(mock, method, args)

                return getDefaultValue(method.returnType)
            }

            for (matcher in matchers.reversed()) {
                if (matcher.matches(mock, method, args)) {
                    recordedInvocations += InvocationDetails(mock, method.name, args)
                    return matcher.getValue()
                }
            }
            recordedInvocations += InvocationDetails(mock, method.name, args)
            return getDefaultValue(method.returnType)
        }
    }

    private fun grabNewCallData(mock: Any, method: Method, args: Array<Any>) {
        if (InterceptState.builder != lastCalledBuilder) {
            lateinit var matcher: Matcher
            when (InterceptState.newMatcher) {
                SimpleArgumentMatcher::class.java -> matcher =
                    SimpleArgumentMatcher(mock::class.qualifiedName!!, getSignature(method), args)
                AnyMatcher::class.java -> matcher = AnyMatcher(mock::class.qualifiedName!!, getSignature(method), args)
                null -> matcher = SimpleArgumentMatcher(mock::class.qualifiedName!!, getSignature(method), args)
            }
            matchers.add(matcher)
        }

        val matcher = matchers.last()
        matcher.addReturnValue(InterceptState.returnValue)
        lastCalledBuilder = InterceptState.builder
        InterceptState.returnValue = null
        InterceptState.newMatcher = null
        InterceptState.lock = false
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