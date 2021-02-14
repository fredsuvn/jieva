package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.checkArgument
import xyz.srclab.common.codec.Codec.Companion.decodeBase64String
import xyz.srclab.common.egg.Egg

/**
 * @author sunqian
 */
class OSpaceBattle : Egg {

    override val readmeMd: String = "eggs/v0/readme.md"
    override val readMeAdoc: String = "eggs/v0/readme.adoc"

    override fun hatchOut(spell: CharSequence) {
        val config = parseSpell(spell)

        println("Start O Space Battle with config: $config")
        val engine = createEngine(config)
        val scenario = createScenario(config)
        val view = createView(config)
        view.start(config.fps, engine, scenario)
    }

    private fun parseSpell(spell: CharSequence): OSpaceConfig {
        checkArgument(spell.decodeBase64String() == "快跑！这里没有加班费！", "Wrong Spell!")
        return OSpaceConfig(
            1.0,
            20,
            60
        )
    }

    private fun createEngine(config: OSpaceConfig): OSpaceEngine {
        return object : OSpaceEngine {
            override fun start(scenario: OSpaceScenario): OSpacePlaying {
                TODO("Not yet implemented")
            }
        }
    }

    private fun createScenario(config: OSpaceConfig): OSpaceScenario {
        return object : OSpaceScenario {
            override val ammos: List<Ammo>
                get() = TODO("Not yet implemented")
            override val livings: List<Living>
                get() = TODO("Not yet implemented")
            override val player1: Player
                get() = TODO("Not yet implemented")
            override val player2: Player
                get() = TODO("Not yet implemented")

            override fun refreshEnemies(difficulty: Int) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun createView(config: OSpaceConfig): OSpaceView {
        return object : OSpaceView {
            override fun start(fps: Int, engine: OSpaceEngine, scenario: OSpaceScenario) {
                TODO("Not yet implemented")
            }
        }
    }
}