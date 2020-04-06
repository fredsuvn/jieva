package test.xyz.srclab.common.bean

import xyz.srclab.common.bean.BeanProperty
import java.lang.reflect.Method
import java.lang.reflect.Type

class CustomBeanProperty(
    private val _name: String,
    private val _type: Class<*>
) : BeanProperty {

    override fun getName(): String {
        return _name
    }

    override fun getType(): Class<*> {
        return _type
    }

    override fun getGenericType(): Type {
        return _type
    }

    override fun getReadMethod(): Method? {
        TODO("Not yet implemented")
    }

    override fun isReadable(): Boolean {
        return true
    }

    override fun isWriteable(): Boolean {
        return true
    }

    override fun getWriteMethod(): Method? {
        TODO("Not yet implemented")
    }

    override fun getValue(bean: Any?): Any {
        return _name
    }

    override fun setValue(bean: Any?, value: Any?) {
    }
}