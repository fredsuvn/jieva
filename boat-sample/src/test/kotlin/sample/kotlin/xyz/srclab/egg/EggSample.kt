package sample.kotlin.xyz.srclab.egg

import org.testng.annotations.Test
import xyz.srclab.common.egg.BoatEggManager
import xyz.srclab.common.test.TestLogger

/**
 * @Author: TannerHu
 * @Date: 2021/6/10
 * @Version:
 **/
class EggSample {

    @Test
    fun testEgg() {
        try{
            val egg = BoatEggManager.pick("O Battle")
            egg.hatchOut("Thank you, Taro.")
            //Or
            //egg.hatchOut("谢谢你，泰罗。")
        }catch (e: Exception){
            logger.log(e)
        }

    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }

}