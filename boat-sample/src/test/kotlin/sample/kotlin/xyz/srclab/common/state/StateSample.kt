package sample.kotlin.xyz.srclab.common.state

import org.testng.annotations.Test
import xyz.srclab.common.state.CharsState
import xyz.srclab.common.state.CharsState.Companion.joinStateDescriptions
import xyz.srclab.common.state.CharsState.Companion.moreDescriptions
import xyz.srclab.common.state.State
import xyz.srclab.common.test.TestLogger

class StateSample {

    @Test
    fun testState() {
        val myState = MyState(1, "description")
        val newState = myState.withMoreDescription("cause")
        //description[cause]
        logger.log(newState.description)
    }

    class MyState(
        override val code: Int, override val descriptions: List<String>
    ) : State<Int, String, MyState> {

        constructor(code: Int, description: String?) : this(code, CharsState.newDescriptions(description))

        override val description: String? = descriptions.joinStateDescriptions()

        override fun withNewDescription(newDescription: String?): MyState {
            return MyState(code, CharsState.newDescriptions(newDescription))
        }

        override fun withMoreDescription(moreDescription: String): MyState {
            return MyState(code, descriptions.moreDescriptions(moreDescription))
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}