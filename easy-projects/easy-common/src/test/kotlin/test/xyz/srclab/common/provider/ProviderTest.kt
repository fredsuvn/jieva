package test.xyz.srclab.common.provider

import org.testng.annotations.Test
import xyz.srclab.common.pattern.provider.AbstractProviderManager
import xyz.srclab.common.pattern.provider.ProviderNotFoundException
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable

object ProviderTest {

    @Test
    fun testProvider() {
        val p1 = TestProvider()
        val p2 = TestProvider()
        val testProviderManager = TestProviderManager()
        testProviderManager.registerProvider(p1)
        testProviderManager.registerProvider(TestProvider2::class.java.name)
        testProviderManager.registerProvider("ss", p2)
        doAssertEquals(testProviderManager.getProvider(TestProvider::class.java.name), p1)
        doAssertEquals(testProviderManager.getProvider(TestProvider2::class.java.name), TestProvider2())
        doAssertEquals(testProviderManager.getProvider("ss"), p2)

        testProviderManager.removeProvider(TestProvider::class.java.name)
        testProviderManager.removeProvider(TestProvider2::class.java.name)
        testProviderManager.removeProvider("ss")

        doExpectThrowable(ProviderNotFoundException::class.java) {
            testProviderManager.getProvider("ss")
        }

        doAssertEquals(testProviderManager.provider, TestProvider0())
        testProviderManager.removeProvider(TestProvider0::class.java.name)
        doExpectThrowable(ProviderNotFoundException::class.java) {
            testProviderManager.getProvider(TestProvider0::class.java.name)
        }

        testProviderManager.registerProvider("ss", p2, true)
        doAssertEquals(testProviderManager.provider, p2)
        testProviderManager.removeProvider("ss")
        doExpectThrowable(ProviderNotFoundException::class.java) {
            testProviderManager.getProvider("ss")
        }.catch {
            doAssertEquals(it.providerName, "ss")
        }

        testProviderManager.registerProvider("ss", p2)
        doExpectThrowable(IllegalArgumentException::class.java) {
            testProviderManager.registerProvider("ss", p1)
        }
        doExpectThrowable(IllegalArgumentException::class.java) {
            testProviderManager.registerProvider(testProviderManager.provider.javaClass.name + "8")
        }

        testProviderManager.registerProvider(TestProvider3::class.java.name, true)
        doAssertEquals(testProviderManager.provider, TestProvider3())
        doExpectThrowable(IllegalArgumentException::class.java) {
            testProviderManager.registerProvider(TestProvider3::class.java.name, true)
        }
    }

    open class TestProvider

    class TestProvider0 : TestProvider() {
        override fun equals(other: Any?): Boolean {
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class TestProvider2 : TestProvider() {
        override fun equals(other: Any?): Boolean {
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class TestProvider3 : TestProvider() {
        override fun equals(other: Any?): Boolean {
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class TestProviderManager : AbstractProviderManager<TestProvider>() {
        override fun createDefaultProvider(): TestProvider {
            return TestProvider0()
        }
    }
}