package sample.kotlin.xyz.srclab.common.state

import org.testng.annotations.Test
import xyz.srclab.common.state.State
import xyz.srclab.common.state.State.Companion.stateMoreDescription
import xyz.srclab.common.test.TestLogger

class StateSample {

    @Test
    fun testState() {
        val myState = MyState(1, "description")
        val newState = myState.withMoreDescription("cause")
        //description[cause]
        logger.log(newState.description)
    }

    class MyState(override val code: Int, override val description: String?) :
        State<Int, String, MyState> {

        override fun withNewDescription(newDescription: String?): MyState {
            return MyState(code, newDescription)
        }

        override fun withMoreDescription(moreDescription: String?): MyState {
            return MyState(code, description.stateMoreDescription(moreDescription))
        }
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}