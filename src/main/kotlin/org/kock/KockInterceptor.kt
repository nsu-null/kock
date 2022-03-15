package org.kock

import org.kock.Matcher.AnyMatcher
import org.kock.Matcher.Matcher
import org.kock.Matcher.SimpleArgumentMatcher
import org.kock.Matcher.getSignature
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

object InterceptState {
    var returnValue: Any? = null
    var builder: Builder? = null
    var newMatcher: Class<out Matcher>? = null
    var lock = false
}


class KockInterceptor {
    private val recordedInvocationDetails: List<InvocationDetails<*>> = LinkedList<InvocationDetails<*>>()
    private val matchers = ArrayList<Matcher>();
    private var lastCalledBuilder: Builder? = null

    operator fun invoke(mock: Any, method: Method, args: Array<Any>): Any? {
        if (InterceptState.lock) {
            grabNewCallData(mock, method, args)

            return getDefaultValue(method.returnType);
        }

        for (matcher in matchers.reversed()) {
            if (matcher.match(mock, method, args)) {
                return matcher.getValue();
            }
        }
        return getDefaultValue(method.returnType)
    }

    private fun grabNewCallData(mock: Any, method: Method, args: Array<Any>) {
        if (InterceptState.builder != lastCalledBuilder) {
            var matcher: Matcher? = null;
            when (InterceptState.newMatcher) {
                SimpleArgumentMatcher::class.java -> matcher =
                    SimpleArgumentMatcher(mock::class.qualifiedName!!, getSignature(method), args)
                AnyMatcher::class.java -> matcher = AnyMatcher(mock::class.qualifiedName!!, getSignature(method), args)
                null -> matcher = SimpleArgumentMatcher(mock::class.qualifiedName!!, getSignature(method), args)
            }
            InterceptState.newMatcher = null;
            matchers.add(matcher!!);
        }

        val matcher = matchers.last();
        matcher.addReturnValue(InterceptState.returnValue);
        lastCalledBuilder = InterceptState.builder
        InterceptState.returnValue = null;
        InterceptState.newMatcher = null;
        InterceptState.lock = false;
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