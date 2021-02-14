package sample.xyz.srclab.common.egg

import org.testng.annotations.Test
import xyz.srclab.common.egg.BoatEggManager

class EggSampleKt {

    @Test
    fun testEgg() {
        val egg = BoatEggManager.pick("Hello, Boat Egg!")
        egg.hatchOut("出来吧，神龙！")
    }
}