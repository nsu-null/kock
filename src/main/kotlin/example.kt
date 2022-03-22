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


    every { mock.otherMethod() } returnsMany listOf(1, 3, 4, 5)
}
