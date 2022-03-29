package org.kock.matchers

import org.kock.matcher.ArgumentMatcher

class Matcher {
    private val argumentMatchers = ArrayDeque<ArgumentMatcher<Any>>()
    private val results = ArrayDeque<Any?>()
    fun getValue(): Any? {
        if (results.size <= 1) {
            return results.first()
        }
        return results.removeFirst()
    }

    fun addReturnValue(value: Any?) {
        results.add(value)
    }

    fun addArgumentMatcher(argumentMatcher: ArgumentMatcher<Any>) {
        argumentMatchers.add(argumentMatcher)
    }

    fun matches(args: Array<Any?>): Boolean {
        if (args.size != argumentMatchers.size) {
            return false
        }
        for (i in 0..argumentMatchers.size - 1) {
            if (!argumentMatchers[i].matches(args[i])) {
                return false
            }
        }
        return true
    }

    fun getSize(): Int {
        return argumentMatchers.size
    }
}

fun simpleArgumentMatcher(args: Array<Any?>): Matcher {
    val matcher = Matcher()
    for (arg in args) {
        matcher.addArgumentMatcher { (it == null && arg == null) || it?.equals(arg) ?: false }
    }
    return matcher
}