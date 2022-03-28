import org.kock.*
import kotlin.test.Test
import kotlin.collections.Collection
import kotlin.test.assertEquals

class Tests {
    @Test
    fun testReturns() {
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
        } returnsMany (100 then 200)

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
        } returns true
        every {
            list.contains(3)
        } returns false

        assertEquals(true, list.contains(0))
        assertEquals(true, list.contains(1))
        assertEquals(true, list.contains(2))
        assertEquals(false, list.contains(3))
        assertEquals(true, list.contains(4))
        every { list.size } returnsMany (3 then 5 then 10)
        assertEquals( list.size, 3)
        assertEquals( list.size, 5)
        assertEquals( list.size, 10)
        assertEquals( list.size, 10)
    }

    @Test
    fun verifying() {

        val something = "something"

        val mock = kock<Any>()
        every {
            mock.toString()
        } returns something

        val smth = mock.toString()
        assertEquals(something, smth)

        verify { mock.toString() }

        verifyTimes { mock.toString() } isExactly  1
        verifyTimes { mock.toString() } isMoreThan 0
        verifyTimes { mock.toString() } isLessThan 10

        val otherMock = kock<List<String>>()

        otherMock.size
        otherMock.size
        otherMock.hashCode()
        otherMock.hashCode()
        otherMock.hashCode()
        otherMock.hashCode()

        verify {
            exactOrder
            otherMock.size
            otherMock.hashCode()

            anyOrder
            otherMock.hashCode()
            otherMock.size
        }

        verifyNot {
            anyOrder
            otherMock.toString()
        }

        verify {
            exactOrder
            otherMock.size
            otherMock.size
            otherMock.hashCode()
        }

        verifyNot {
            exactOrder
            otherMock.hashCode()
            otherMock.size
        }

        verify { // 4 times
            exactOrder
            otherMock.hashCode()
            otherMock.hashCode()
            otherMock.hashCode()
            otherMock.hashCode()
        }

        verifyNot { // 5 times
            exactOrder
            otherMock.hashCode()
            otherMock.hashCode()
            otherMock.hashCode()
            otherMock.hashCode()
            otherMock.hashCode()
        }

        val thirdMock = kock<Any>()

        verifyNot {
            thirdMock.hashCode()
        }
    }

    @Test
    fun verifyNot() {
        val mock = kock<List<String>>()

        mock.size
        mock.size
        mock.hashCode()
        mock.hashCode()
        mock.hashCode()
        mock.hashCode()

        verifyNot {
            exactOrder
            mock.hashCode()
            mock.size
        }
    }

}