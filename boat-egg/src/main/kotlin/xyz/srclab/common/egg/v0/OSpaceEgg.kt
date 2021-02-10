package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.checkArgument
import xyz.srclab.common.codec.Codec.Companion.decodeBase64String
import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OSpaceEgg : Egg {

    override fun hatchOut(spell: CharSequence) {
        checkSpell(spell)

        println("sssss")
    }

    private fun checkSpell(spell: CharSequence) {
        checkArgument(spell.decodeBase64String() == "快跑！这里没有加班费！", "Wrong Spell!")
    }
}