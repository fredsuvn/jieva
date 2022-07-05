/**
 * JVM utilities.
 */
@file:JvmName("BtJvm")

package xyz.srclab.common.jvm

import xyz.srclab.common.base.dotMatcher
import java.lang.reflect.Method

/**
 * Returns JVM signature of this class.
 */
val Class<*>.typeSignature: String
    get() {
        return when (this) {
            Boolean::class.javaPrimitiveType -> "Z"
            Byte::class.javaPrimitiveType -> "B"
            Short::class.javaPrimitiveType -> "S"
            Char::class.javaPrimitiveType -> "C"
            Int::class.javaPrimitiveType -> "I"
            Long::class.javaPrimitiveType -> "J"
            Float::class.javaPrimitiveType -> "F"
            Double::class.javaPrimitiveType -> "D"
            Void::class.javaPrimitiveType -> "V"
            else -> if (this.isArray) {
                "[${this.componentType.typeSignature}"
            } else {
                "L${dotMatcher().replaceFrom(this.name, '/')};"
            }
        }
    }

/**
 * Returns JVM signature of this method.
 */
val Method.typeSignature: String
    get() {
        val parameterTypesDescriptors = this.parameterTypes.parametersTypeSignature
        val returnDescriptor = this.returnType.typeSignature
        return "($parameterTypesDescriptors)$returnDescriptor"
    }

/**
 * Returns JVM signature of these parameters.
 */
val Array<out Class<*>>.parametersTypeSignature: String
    get() {
        return this.joinToString("") { it.typeSignature }
    }

//const val CONSTRUCTOR_METHOD_NAME = "<init>"
//
//private const val WRONG_TYPE_PREFIX = "Wrong type"

//val Type.jvmJavaTypeSignature: String
//    @JvmName("jvmJavaTypeSignature") get() {
//        return when (this) {
//            is Class<*> -> this.jvmDescriptor
//            is GenericArrayType -> "[${this.genericComponentType.jvmJavaTypeSignature}"
//            is TypeVariable<*> -> "T${this.name};"
//            is WildcardType -> {
//                if (this.upperBounds)
//            }
//            is ParameterizedType -> {
//
//            }
//            else -> throw IllegalArgumentException("$WRONG_TYPE_PREFIX: $this")
//        }
//    }
//
//val Field.jvmJavaTypeSignature: String
//    @JvmName("jvmJavaTypeSignature") get() {
//        return this.genericType.jvmJavaTypeSignature
//    }
//
//val Type.jvmClassSignature: String
//    @JvmName("jvmClassSignature") get() {
//        return when (this) {
//            is Class<*> -> {
//
//                val typeParameters = this.typeParameters
//                if (typeParameters.isNullOrEmpty()) {
//                    this.jvmDescriptor
//                }
//                ""
//            }
//            else -> throw IllegalArgumentException("$WRONG_TYPE_PREFIX: $this")
//        }
//    }
//
//val Field.jvmClassSignature: String
//    @JvmName("jvmClassSignature") get() {
//        return this.genericType.jvmClassSignature
//    }
//
//val Method.jvmSignature: String
//    @JvmName("jvmSignature") get() {
//        val buf = StringBuilder()
//        val typeParameters = this.typeParameters
//        if (!typeParameters.isNullOrEmpty()) {
//            buf.append(typeParameters.joinToString(separator = "") { it.jvmTypeParameterSignature })
//        }
//        buf.append('{')
//            .append(this.genericParameterTypes.joinToString(separator = "") { it.jvmJavaTypeSignature })
//            .append('}')
//            .append(this.genericReturnType.jvmJavaTypeSignature)
//        val exceptionTypes = this.exceptionTypes
//        if (!exceptionTypes.isNullOrEmpty()) {
//            buf.append(exceptionTypes.joinToString(separator = "") { it.jvmJavaTypeSignature })
//        }
//        return buf.toString()
//    }
//
//val TypeVariable<*>.jvmTypeArgumentSignature: String
//    @JvmName("jvmTypeArgumentSignature") get() {
//        return ""
//    }
//
//val TypeVariable<*>.jvmTypeParameterSignature: String
//    @JvmName("jvmTypeParameterSignature") get() {
//        return ""
//    }
//