package test.xyz.srclab.common.bytecode

import org.objectweb.asm.*
import org.testng.annotations.Test
import xyz.srclab.common.bytecode.*
import xyz.srclab.test.doAssertEquals
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Function

/**
 * @author sunqian
 */
object ByteCodeTest {

    @Test
    fun testByteCodeTypes() {
        val abcInfo = getAbcInfo()
        println(abcInfo)

        val newClassName = Abc::class.java.name + "2"
        val typeT = BTypeVariable("T", true)
        val typeU = BTypeVariable("U", true)
        val typeO = BTypeVariable("O", true)
        val runnableType = BType(Runnable::class.java)
        val callableO = BType(Callable::class.java)
        val listO = BType(List::class.java)
        val mutableSetType = BType(MutableSet::class.java)
        val gType = BType(G::class.java)
        val functionType = BType(Function::class.java)
        val stringType = BType(String::class.java)
        val inString = BWildcardType(BWildcardType.LOWER_BOUND, stringType)
        val outO = BWildcardType(BWildcardType.UPPER_BOUND, typeO)

        listO.addGenericTypes(outO)
        callableO.addGenericTypes(typeO)
        typeT.addBounds(listO, runnableType, callableO)

        mutableSetType.addGenericTypes(inString)
        typeU.addBounds(mutableSetType)

        typeO.addBounds(typeT)

        gType.addGenericTypes(typeU)

        functionType.addGenericTypes(typeU, stringType)

        println("------------")

        val newInternalName = ByteCodeHelper.getTypeInternalName(newClassName)
        val newSignature =
            ByteCodeHelper.getTypeDeclarationSignature(arrayOf(typeT, typeU, typeO), arrayOf(gType, functionType))
        doAssertEquals(abcInfo.classInternalName + "2", newInternalName)
        doAssertEquals(abcInfo.classSignature, newSignature)

        println("------------")

        val applyDescriptor = ByteCodeHelper.getMethodDescriptor(arrayOf(typeU), stringType)
        val applySignature = ByteCodeHelper.getMethodSignature(arrayOf(typeU), stringType)
        doAssertEquals(abcInfo.methodApplyDescriptor, applyDescriptor)
        doAssertEquals(abcInfo.methodApplySignature, applySignature)

        println("------------")

        val typeS = BTypeVariable("S", false)
        val inS = BWildcardType(BWildcardType.LOWER_BOUND, typeS)
        val outString = BWildcardType(BWildcardType.UPPER_BOUND, stringType)
        val arrayS = BArrayType(typeS, 1)
        val arrayArrayS = BArrayType(typeS, 2)
        val inArrayS = BWildcardType(BWildcardType.LOWER_BOUND, arrayS)
        val numberType = BType(Number::class.java)
        typeS.addBounds(numberType, runnableType)
        val mutableListInS = BType(MutableList::class.java)
        mutableListInS.addGenericTypes(inS)
        val mutableListOutString = BType(MutableList::class.java)
        mutableListOutString.addGenericTypes(outString)
        val mutableListString = BType(MutableList::class.java)
        mutableListString.addGenericTypes(stringType)
        val mutableListInArrayS = BType(MutableList::class.java)
        mutableListInArrayS.addGenericTypes(inArrayS)
        val ssDescriptor = ByteCodeHelper.getMethodDescriptor(
            arrayOf(stringType, typeS, mutableListInS, mutableListOutString, mutableListString, mutableListInArrayS),
            arrayArrayS
        )
        val ssSignature = ByteCodeHelper.getMethodSignature(
            arrayOf(stringType, typeS, mutableListInS, mutableListOutString, mutableListString, mutableListInArrayS),
            arrayArrayS,
            arrayOf(typeS)
        )
        doAssertEquals(abcInfo.methodSsDescriptor, ssDescriptor)
        doAssertEquals(abcInfo.methodSsSignature, ssSignature)
    }

    private fun getAbcInfo(): AbcInfo {
        val info = AbcInfo()
        val classReader = ClassReader(Abc::class.java.name)
        classReader.accept(object : ClassVisitor(Opcodes.ASM7) {
            override fun visit(
                i: Int,
                i1: Int,
                s: String?,
                s1: String?,
                s2: String?,
                strings: Array<String?>?
            ) {
                println("class: " + s + " : " + s1 + " : " + s2 + " : " + Arrays.toString(strings))
                info.classInternalName = s
                info.classSignature = s1
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
                info.fieldUDescriptor = s1
                info.fieldUSignature = s2
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
                if (s == "apply") {
                    if (i and Opcodes.ACC_BRIDGE == Opcodes.ACC_BRIDGE) {
                        info.methodApplyBridgeDescriptor = s1
                        info.methodApplyBridgeSignature = s2
                    } else {
                        info.methodApplyDescriptor = s1
                        info.methodApplySignature = s2
                    }
                } else if (s == "ss") {
                    info.methodSsDescriptor = s1
                    info.methodSsSignature = s2
                }
                return super.visitMethod(i, s, s1, s2, strings)
            }

            override fun visitSource(source: String?, debug: String?) {
                println("source: $source : $debug")
                super.visitSource(source, debug)
            }

            override fun visitAttribute(attribute: Attribute?) {
                println("attribute: $attribute")
                super.visitAttribute(attribute)
            }
        }, 0)
        return info
    }

    interface I

    open class G<T> : I

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
            list4: MutableList<in Array<S>>
        ): Array<Array<S>> where S : Number, S : Runnable {
            return arrayOf()
        }

        fun invoke(vararg args: Any): String? {
            return null
        }
    }

    class AbcInfo {
        var classInternalName: String? = null
        var classSignature: String? = null
        var fieldUDescriptor: String? = null
        var fieldUSignature: String? = null
        var methodApplyDescriptor: String? = null
        var methodApplySignature: String? = null
        var methodApplyBridgeDescriptor: String? = null
        var methodApplyBridgeSignature: String? = null
        var methodSsDescriptor: String? = null
        var methodSsSignature: String? = null

        override fun toString(): String {
            return "AbcInfo(" +
                    "classInternalName=$classInternalName, " +
                    "classSignature=$classSignature, " +
                    "fieldUDescriptor=$fieldUDescriptor, " +
                    "fieldUSignature=$fieldUSignature, " +
                    "methodApplyDescriptor=$methodApplyDescriptor, " +
                    "methodApplySignature=$methodApplySignature, " +
                    "methodApplyBridgeDescriptor=$methodApplyBridgeDescriptor, " +
                    "methodApplyBridgeSignature=$methodApplyBridgeSignature, " +
                    "methodSsDescriptor=$methodSsDescriptor, " +
                    "methodSsSignature=$methodSsSignature)"
        }
    }
}