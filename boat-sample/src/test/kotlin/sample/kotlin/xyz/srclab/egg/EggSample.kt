package sample.kotlin.xyz.srclab.egg

import org.testng.annotations.Test
import xyz.srclab.common.egg.BoatEggManager

/**
 * @Author: TannerHu
 * @Date: 2021/6/10
 * @Version:
 **/
class EggSample {

    @Test
    fun testEgg() {
        val egg = BoatEggManager.pick("O Battle")
        egg.hatchOut("Thank you, Taro.")
        //Or
        //egg.hatchOut("谢谢你，泰罗。")
    }

}