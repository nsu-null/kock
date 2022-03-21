import org.kock.*
import kotlin.test.Test
import kotlin.collections.Collection
import kotlin.test.assertEquals

class Tests {
    @Test
    fun testThen() {
        val list = kock<Collection<Int>>()
        every {
            list.size
        } returnsMany listOf(0, 1, 4)
        assertEquals(0, list.size)
        assertEquals(1, list.size)
        assertEquals(4, list.size)
        assertEquals(4, list.size)
        every {
            list.size
        } returnsMany listOf(100, 200)

        assertEquals(list.size, 100)
        assertEquals(list.size, 200)

        every {
            list.isEmpty()
        } returnsMany listOf(false, true)

        assertEquals(list.isEmpty(), false)
        assertEquals(list.isEmpty(), true)
        assertEquals(list.size, 200)
    }

    @Test
    fun testAny() {
        val list = kock<Collection<Int>>()
        every {
            list.contains(anyInt())
        }.returns(true)
        every {
            list.contains(3)
        }.returns(false)

        assertEquals(true, list.contains(0))
        assertEquals(true, list.contains(1))
        assertEquals(true, list.contains(2))
        assertEquals(false, list.contains(3))
        assertEquals(true, list.contains(4))
        every { list.size } returnsMany listOf(3, 5)
        assertEquals( list.size, 3)
        assertEquals( list.size, 5)
        assertEquals( list.size, 5)
    }
}