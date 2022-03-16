package dev.kock

/*
Given the type, constructs a mock for it by calling a generated no-arg constructor
which does nothing
 */
inline fun <reified T: Any> kock(): T {
    val constructors = T::class.constructors
    val defaultConstructor = constructors.find { it.parameters.isEmpty() }
        ?: throw IllegalArgumentException("Can't find default constructor")
    return defaultConstructor.call()
}

inline fun <reified T: Any> spy(): T {
    return kock()
}

class StubbingContext {

    private val list = mutableListOf<Any>()

}

class VerifyingContext {
    private val list = mutableListOf<Any>()
    val isOK = true
    var times = 0
    var exactOrder: Unit? = null
        get() {
            println("sideeffect")
            return field
        }
        private set
    var anyOrder: Unit? = null
        get() {
            println("sideeffect")
            return field
        }
        private set
}

fun every(block: StubbingContext.() -> Unit): StubbingContext {
    val context = StubbingContext()
    context.block()
    return context
}

infix fun StubbingContext.returns(something: Any?) {
    println("well this is something we need to do")
}

infix fun StubbingContext.returnsMany(stuff: List<Any>) {
    println("this isn't really implemented yet")
}

fun verify(inverse: Boolean = false, block: VerifyingContext.() -> Unit): Boolean {
    val context = VerifyingContext()
    context.block()
    return context.isOK
}

fun verifyTimes(inverse: Boolean = false, block: VerifyingContext.() -> Unit): VerifyingContext {
    val context = VerifyingContext()
    context.block()
    return context
}

infix fun VerifyingContext.isExactly(times: Int) {
    assert(this.times == times)
}

infix fun VerifyingContext.isMoreThan(times: Int) {
    assert(this.times > times)
}

infix fun VerifyingContext.isLessThan(times: Int) {
    assert(this.times < times)
}
