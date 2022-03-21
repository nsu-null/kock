import dev.kock.*
import org.kock.*

private open class ExampleClass {
    var prop1: String? = "kock"
    var prop2: Int = 2

    fun stringRepr(): String = "example"
    fun otherMethod(): String? = "different string"

    fun thirdMethod() = 3
}


fun main() {
    println("Here the kock framework will be used")

    val x = ExampleClass()

    val mock = kock<ExampleClass>()

    println("here is ${mock.prop1}")

    val spy = spy<ExampleClass>()

    every {
        mock.stringRepr()
        mock.otherMethod()
        spy.stringRepr()
    } returns "something else"

    val smth = mock.stringRepr()
    println(smth) // doesnt really work for now whatever

    verify { mock.stringRepr() }

    verifyTimes { mock.stringRepr() } isExactly 1
    verifyTimes { mock.stringRepr() } isMoreThan 0
    verifyTimes { mock.stringRepr() } isLessThan 10

    verify {
        exactOrder
        mock.stringRepr()
        mock.otherMethod()

        anyOrder
        mock.thirdMethod()
    }

    every { mock.otherMethod() } returnsMany listOf(1, 3, 4, 5)
}
