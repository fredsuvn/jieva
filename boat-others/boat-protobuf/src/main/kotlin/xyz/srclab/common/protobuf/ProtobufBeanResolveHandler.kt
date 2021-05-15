package xyz.srclab.common.protobuf

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import xyz.srclab.common.bean.AbstractBeanResolveHandler
import xyz.srclab.common.bean.BeanResolveHandler
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.reflect.genericInterface
import xyz.srclab.common.reflect.method
import xyz.srclab.common.reflect.methodOrNull
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Method

/**
 * Bean resolve handler for Protobuf object.
 *
 * @see BeanResolveHandler
 */
object ProtobufBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolve(context: BeanResolveHandler.Context) {
        val rawClass = context.beanType.rawClass
        if (!MessageOrBuilder::class.java.isAssignableFrom(rawClass)) {
            //context.breakResolving()
            return
        }
        super.resolve(context)
    }

    override fun resolveAccessors(
        context: BeanResolveHandler.Context,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>
    ) {
        val rawClass = context.beanType.rawClass

        fun createPropertyInvoker(field: Descriptors.FieldDescriptor, isBuilder: Boolean) {

            fun createSetInvoker(clearMethod: Method, addAllMethod: Method): Invoker {
                return object : Invoker {

                    override fun <T> invoke(`object`: Any?, vararg args: Any?): T {
                        clearMethod.invoke(`object`)
                        addAllMethod.invoke(`object`, *args)
                        return null.asAny()
                    }

                    override fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T {
                        return invoke(`object`, *args)
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
                val type = getterMethod.genericReturnType.genericInterface(null, Map::class.java)
                if (type === null) {
                    throw IllegalStateException("Cannot find type of generic interface of field: $name")
                }
                val invoker = getterMethod.toInvoker()
                getters[name] = PropertyInvoker(type, invoker)

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
                    val setterInvoker = createSetInvoker(clearMethod, putAllMethod)
                    setters[name] = PropertyInvoker(type, setterInvoker)
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
                val type = getterMethod.genericReturnType.genericInterface(null, List::class.java)
                if (type === null) {
                    throw IllegalStateException("Cannot find type of generic interface of field: $name")
                }
                val invoker = getterMethod.toInvoker()
                getters[name] = PropertyInvoker(type, invoker)

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
                    val setterInvoker = createSetInvoker(clearMethod, addAllMethod)
                    setters[name] = PropertyInvoker(type, setterInvoker)
                }

                return
            }

            // Simple object
            val getterMethod = rawClass.methodOrNull("get${rawName.capitalize()}")
            if (getterMethod === null) {
                return
            }
            val type = getterMethod.genericReturnType
            val invoker = getterMethod.toInvoker()
            getters[rawName] = PropertyInvoker(type, invoker)

            if (isBuilder) {
                val setterMethod = rawClass.methodOrNull("set${rawName.capitalize()}", type.rawClass)
                if (setterMethod === null) {
                    throw IllegalStateException("Cannot find setter method of field: $rawName")
                }
                val setterInvoker = setterMethod.toInvoker()
                setters[rawName] = PropertyInvoker(type, setterInvoker)
            }
        }

        //com.google.protobuf.Descriptors.Descriptor getDescriptor()
        val descriptor: Descriptors.Descriptor =
            Invoker.forMethod(rawClass, "getDescriptor").invoke(null)
        val isBuilder = Message.Builder::class.java.isAssignableFrom(rawClass)
        for (field in descriptor.fields) {
            createPropertyInvoker(field, isBuilder)
        }

        //Add class property
        getters["class"] = PropertyInvoker(Class::class.java, rawClass.method("getClass").toInvoker())

        context.breakResolving()
    }
}