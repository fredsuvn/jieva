package test.kotlin.xyz.srclab.common.io

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import xyz.srclab.common.io.readObject
import xyz.srclab.common.io.writeObject
import java.io.File
import java.io.Serializable

class IOTest {

    @Test
    fun testSerialize() {
        val a = A()
        a.a = "123"
        val temp = File.createTempFile("ttt", ".txt")
        a.writeObject(temp, true)
        val ar = temp.readObject<A>(true)
        Assert.assertEquals(ar.a, a.a)
        temp.delete()
    }

    open class A : Serializable {

        lateinit var a: String

        companion object {
            private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
        }
    }
}