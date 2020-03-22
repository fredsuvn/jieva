package test.xyz.srclab.common.bean

import xyz.srclab.common.bean.BeanPropertyDescriptor

class CustomBeanPropertyDescriptor(
    private val _name: String,
    private val _type: Class<*>,
    private val _value: Any
) : BeanPropertyDescriptor {

    override fun getName(): String {
        return _name
    }

    override fun getType(): Class<*> {
        return _type
    }

    override fun isReadable(): Boolean {
        return true
    }

    override fun isWriteable(): Boolean {
        return true
    }

    override fun getValue(bean: Any?): Any {
        return _value
    }

    override fun setValue(bean: Any?, value: Any?) {
    }
}