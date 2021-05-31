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
        //seq0-06803239610792857600-tail
        val spec0 = "seq0-{Snowflake, 20, 41, 10, 12}-tail"
        val stringIdSpec0 = IdSpec(spec0)
        for (i in 0..9) {
            logger.log(stringIdSpec0.newId())
        }

        //seq1-00001826267315077279180346359808-tail
        val spec1 = "seq1-{Snowflake, 32, 55, 25, 25}-tail"
        val stringIdSpec1 = IdSpec(spec1)
        for (i in 0..9) {
            logger.log(stringIdSpec1.newId())
        }

        //seq2-29921563690270857976266765631488-tail
        val spec2 = "seq2-{Snowflake, 32, 63, 32, 32}-tail"
        val stringIdSpec2 = IdSpec(spec2)
        for (i in 0..9) {
            logger.log(stringIdSpec2.newId())
        }

        //seq3{}-06803240106559590400-tail
        val spec3 = "seq3\\{}-{Snowflake, 20, 41, 10, 12}-tail"
        val stringIdSpec3 = IdSpec(spec3)
        for (i in 0..9) {
            logger.log(stringIdSpec3.newId())
        }

        //seq4{}-06805124180752646144-tail
        val spec4 = "seq4\\{\\}-{Snowflake, 20, 41, 10, 12}-tail"
        val stringIdSpec4 = IdSpec(spec4)
        for (i in 0..9) {
            logger.log(stringIdSpec4.newId())
        }

        val spec5 = "seq5\\{\\}-{Snowflake, 20, 41, 10, 12"
        Assert.expectThrows(
            IllegalArgumentException::class.java
        ) { IdSpec(spec5) }
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