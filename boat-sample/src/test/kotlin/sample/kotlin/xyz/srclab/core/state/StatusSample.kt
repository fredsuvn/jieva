package sample.kotlin.xyz.srclab.core.state

import org.testng.annotations.Test
import xyz.srclab.common.status.Status
import xyz.srclab.common.status.StringStatus.Companion.joinStateDescriptions
import xyz.srclab.common.status.StringStatus.Companion.moreDescriptions

class StatusSample {

    @Test
    fun testState() {
        val myState = MyState(1, "description")
        val newState = myState.withMoreMessage("cause")
        //description[cause]
        logger.log(newState.message)
    }

    class MyState(
        override val code: Int, override val descriptions: List<String>
    ) : Status<Int, String, MyState> {

        constructor(code: Int, description: String?) : this(code, StringStatus.newDescriptions(description))

        override val message: String? = descriptions.joinStateDescriptions()

        override fun withNewMessage(newDescription: String?): MyState {
            return MyState(code, StringStatus.newDescriptions(newDescription))
        }

        override fun withMoreMessage(moreDescription: String): MyState {
            return MyState(code, descriptions.moreDescriptions(moreDescription))
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}