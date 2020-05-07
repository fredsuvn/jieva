package test.xyz.srclab.test.perform

import org.testng.annotations.Test
import xyz.srclab.test.perform.PerformInfo
import xyz.srclab.test.perform.doPerforms
import java.util.function.Function
import kotlin.random.Random

/**
 * @author sunqian
 */
object PerformerTest {

    @Test
    fun testPerformer() {
        val invoker = Invoker()
        val function = Function<Invoker, Any?> { ivk ->
            ivk.doSomething()
            null
        }
        val method = Invoker::class.java.getMethod("doSomething")
        val times = 10000000L
        doPerforms(
            listOf(
                PerformInfo("Directly call 1") { invoker.doSomething() },
                PerformInfo("Directly call 2") { invoker.doSomething() },
                PerformInfo("Directly call 3") { invoker.doSomething() },
                PerformInfo("Function call 1") { function.apply(invoker) },
                PerformInfo("Function call 2") { function.apply(invoker) },
                PerformInfo("Function call 3") { function.apply(invoker) },
                PerformInfo("reflect call 1") { method.invoke(invoker) },
                PerformInfo("reflect call 2") { method.invoke(invoker) },
                PerformInfo("reflect call 3") { method.invoke(invoker) }
            ),
            times
        )
    }

    class Invoker {
        fun doSomething() {
            factorial(100)
        }

        private fun factorial(num: Long): Long {
            if (num == 2L) {
                return 2L
            }
            return num * factorial(num - 1)
        }
    }
}