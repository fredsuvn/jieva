package sample.kotlin.xyz.srclab.id

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.id.IdComponent
import xyz.srclab.common.id.IdContext
import xyz.srclab.common.id.IdSpec
import xyz.srclab.common.id.IdSpec.ComponentSupplier
import xyz.srclab.common.test.TestLogger

class IdSample {
    private val logger = TestLogger.DEFAULT

    @Test
    fun testId() {
        //seq-06803239610792857600-tail
        val spec = "seq-{Snowflake, 20, 41, 10, 12}-tail"
        val stringIdSpec = IdSpec(spec)
        for (i in 0..9) {
            logger.log(stringIdSpec.newId())
        }

        //seq{}-06803240106559590400-tail
        val spec2 = "seq\\{}-{Snowflake, 20, 41, 10, 12}-tail"
        val stringIdSpec2 = IdSpec(spec2)
        for (i in 0..9) {
            logger.log(stringIdSpec2.newId())
        }
        val spec3 = "seq\\{\\}-{Snowflake, 20, 41, 10, 12"
        Assert.expectThrows(
            IllegalArgumentException::class.java
        ) { IdSpec(spec3) }
        //new StringIdSpec(spec3);
    }

    @Test
    fun testCustomId() {
        val spec = "seq-{Snowflake, 20, 41, 10, 12}-{My, 88888}"
        val stringIdSpec = IdSpec(spec, object : ComponentSupplier {
            override fun get(type: String): IdComponent<*> {
                if (type == MyIdComponent.TYPE) {
                    return MyIdComponent()
                }
                return IdSpec.DEFAULT_COMPONENT_SUPPLIER.get(type)
            }
        })
        //seq-06803242693339123712-88888
        for (i in 0..9) {
            logger.log(stringIdSpec.newId())
        }
    }
}

class MyIdComponent : IdComponent<String?> {

    private var value: String? = null
    override val type: String = TYPE

    override fun init(args: List<Any>) {
        value = args[0].toString()
    }

    override fun newValue(context: IdContext): String? {
        return value
    }

    companion object {
        const val TYPE = "My"
    }
}