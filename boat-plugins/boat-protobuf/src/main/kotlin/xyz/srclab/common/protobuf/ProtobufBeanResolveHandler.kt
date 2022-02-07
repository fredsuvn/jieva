package xyz.srclab.common.protobuf

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import com.google.protobuf.MessageLiteOrBuilder
import xyz.srclab.common.base.capitalize
import xyz.srclab.common.bean.AbstractBeanResolveHandler
import xyz.srclab.common.bean.BeanResolveContext
import xyz.srclab.common.bean.BeanResolveHandler
import xyz.srclab.common.bean.BeanTypeBuilder
import xyz.srclab.common.func.InstFunc
import xyz.srclab.common.func.InstFunc.Companion.toInstFunc
import xyz.srclab.common.func.StaticFunc.Companion.toStaticFunc
import xyz.srclab.common.reflect.getTypeSignature
import xyz.srclab.common.reflect.method
import xyz.srclab.common.reflect.methodOrNull
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Method

/**
 * Bean resolve handler supports Protobuf types.
 *
 * @see BeanResolveHandler
 * @see AbstractBeanResolveHandler
 */
object ProtobufBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        context: BeanResolveContext,
        builder: BeanTypeBuilder,
        getters: MutableMap<String, GetterInfo>,
        setters: MutableMap<String, SetterInfo>
    ) {
        val rawClass = builder.type.rawClass

        //Check whether it is a protobuf object
        if (!MessageLiteOrBuilder::class.java.isAssignableFrom(rawClass)) {
            return
        }

        fun addProperty(field: Descriptors.FieldDescriptor, isBuilder: Boolean) {

            fun createSetter(clearMethod: Method, addAllMethod: Method): InstFunc {
                return object : InstFunc {
                    override fun invoke(inst: Any, vararg args: Any?): Any? {
                        clearMethod.invoke(inst)
                        addAllMethod.invoke(inst, *args)
                        return null
                    }
                }
            }

            val rawName = field.name

            //map
            if (field.isMapField) {
                val name = rawName + "Map"
                val getterMethod = rawClass.methodOrNull("get${name.capitalize()}")
                if (getterMethod === null) {
                    return
                }
                val type = getterMethod.genericReturnType.getTypeSignature(Map::class.java)
                val getter = getterMethod.toInstFunc()
                getters[name] = GetterInfo(name, type, getter, null, getterMethod)

                if (isBuilder) {
                    val clearMethod = rawClass.methodOrNull("clear${rawName.capitalize()}")
                    if (clearMethod === null) {
                        throw IllegalStateException("Cannot find clear method of field: $name")
                    }
                    val putAllMethod =
                        rawClass.methodOrNull("putAll${rawName.capitalize()}", Map::class.java)
                    if (putAllMethod === null) {
                        throw IllegalStateException("Cannot find put-all method of field: $name")
                    }
                    val setter = createSetter(clearMethod, putAllMethod)
                    setters[name] = SetterInfo(name, type, setter, null, null)
                }

                return
            }

            //repeated
            if (field.isRepeated) {
                val name = rawName + "List"
                val getterMethod = rawClass.methodOrNull("get${name.capitalize()}")
                if (getterMethod === null) {
                    return
                }
                val type = getterMethod.genericReturnType.getTypeSignature(List::class.java)
                val getter = getterMethod.toInstFunc()
                getters[name] = GetterInfo(name, type, getter, null, getterMethod)

                if (isBuilder) {
                    val clearMethod = rawClass.methodOrNull("clear${rawName.capitalize()}")
                    if (clearMethod === null) {
                        throw IllegalStateException("Cannot find clear method of field: $name")
                    }
                    val addAllMethod =
                        rawClass.methodOrNull("addAll${rawName.capitalize()}", Iterable::class.java)
                    if (addAllMethod === null) {
                        throw IllegalStateException("Cannot find add-all method of field: $name")
                    }
                    val setter = createSetter(clearMethod, addAllMethod)
                    setters[name] = SetterInfo(name, type, setter, null, null)
                }

                return
            }

            // Simple object
            val getterMethod = rawClass.methodOrNull("get${rawName.capitalize()}")
            if (getterMethod === null) {
                return
            }
            val type = getterMethod.genericReturnType
            val getter = getterMethod.toInstFunc()
            getters[rawName] = GetterInfo(rawName, type, getter, null, getterMethod)

            if (isBuilder) {
                val setterMethod = rawClass.methodOrNull("set${rawName.capitalize()}", type.rawClass)
                if (setterMethod === null) {
                    throw IllegalStateException("Cannot find setter method of field: $rawName")
                }
                val setter = setterMethod.toInstFunc()
                setters[rawName] = SetterInfo(rawName, type, setter, null, setterMethod)
            }
        }

        //com.google.protobuf.Descriptors.Descriptor getDescriptor()
        val getDescriptorMethod = rawClass.getMethod("getDescriptor")
        val descriptor: Descriptors.Descriptor = getDescriptorMethod.toStaticFunc().invokeTyped()
        val isBuilder = Message.Builder::class.java.isAssignableFrom(rawClass)
        for (field in descriptor.fields) {
            addProperty(field, isBuilder)
        }

        //Add class property
        val getClassMethod = rawClass.method("getClass")
        getters["class"] = GetterInfo("class", Class::class.java, getClassMethod.toInstFunc(), null, getClassMethod)

        //break
        context.complete()
    }
}