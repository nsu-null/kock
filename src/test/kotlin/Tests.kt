import org.kock.any
import org.kock.anyInt
import org.kock.every
import org.kock.kock
import kotlin.test.Test
import kotlin.collections.Collection
import kotlin.test.assertEquals

class Tests {
    @Test
    fun testThen() {
        val list = kock<Collection<Int>>()
        every {
            list.size
        }
            .then(0)
            .then(1)
            .then(4)
        assertEquals(0, list.size)
        assertEquals(1, list.size)
        assertEquals(4, list.size)
        assertEquals(4, list.size)
        every {
            list.size
        }.then(100).then(200)

        assertEquals(list.size, 100)
        assertEquals(list.size, 200)

        every {
            list.isEmpty()
        }.then(false).then(true)

        assertEquals(list.isEmpty(), false)
        assertEquals(list.isEmpty(), true)
        assertEquals(list.size, 200)
    }

    @Test
    fun testAny() {
        val list = kock<Collection<Int>>()
        every {
            list.contains(anyInt())
        }.then(true)
        every {
            list.contains(3)
        }.then(false)

        assertEquals(true, list.contains(0))
        assertEquals(true, list.contains(1))
        assertEquals(true, list.contains(2))
        assertEquals(false, list.contains(3))
        assertEquals(true, list.contains(4))
        every { list.size }.then(3).then(5)
        assertEquals( list.size, 3)
        assertEquals( list.size, 5)
        assertEquals( list.size, 5)
    }
}