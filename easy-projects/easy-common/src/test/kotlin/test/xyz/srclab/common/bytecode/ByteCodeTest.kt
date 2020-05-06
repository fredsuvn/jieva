package test.xyz.srclab.common.bytecode

import org.objectweb.asm.*
import org.testng.annotations.Test
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Function

/**
 * @author sunqian
 */
object ByteCodeTest {

    @Test
    fun testByteCodeTypes() {
        val classReader = ClassReader(Abc::class.java.name)
        classReader.accept(object : ClassVisitor(Opcodes.ASM7){
            override fun visit(
                i: Int,
                i1: Int,
                s: String?,
                s1: String?,
                s2: String?,
                strings: Array<String?>?
            ) {
                println("class: " + s + " : " + s1 + " : " + s2 + " : " + Arrays.toString(strings))
                super.visit(i, i1, s, s1, s2, strings)
            }

            override fun visitField(
                i: Int,
                s: String?,
                s1: String?,
                s2: String?,
                o: Any?
            ): FieldVisitor? {
                println("field: $s : $s1 : $s2 : $o")
                return super.visitField(i, s, s1, s2, o)
            }

            override fun visitMethod(
                i: Int,
                s: String?,
                s1: String?,
                s2: String?,
                strings: Array<String?>?
            ): MethodVisitor? {
                println("method: " + s + " : " + s1 + " : " + s2 + " : " + Arrays.toString(strings))
                return super.visitMethod(i, s, s1, s2, strings)
            }
        }, ClassReader.SKIP_CODE)
    }

    open class G<T>

    class Abc<T, U : MutableSet<in String>, O : T> : G<U>(), Function<U, String>
            where T : List<O>, T : Runnable, T : Callable<O> {

        private val u: U? = null

        override fun apply(strings: U): String {
            return "null"
        }

        fun <S> ss(
            string: String,
            s: S,
            list: MutableList<in S>,
            list2: MutableList<out String>,
            list3: MutableList<String>,
            list4: MutableList<S>,
            list5: MutableList<Array<S>>
        ): Array<List<S>> where S : Number, S : Runnable {
            return arrayOf()
        }
    }
}