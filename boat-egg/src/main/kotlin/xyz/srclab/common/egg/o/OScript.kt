package xyz.srclab.common.egg.o

class OScript(
    private val data: OData,
    private val engine: OEngine,
    private val config: OConfig
) {

    init {
        engine.addStartListener(OnStart())
        engine.addHitListener(OnHit())
        engine.addFireListener(OnFire())
        engine.addTickListener(OnTick())
    }

    private inner class OnStart : OEngine.StartListener {
        override fun onStart() {
            if (config.mode == OConfig.Mode.PVE) {

            }
        }
    }

    private inner class OnHit : OEngine.HitListener {
        override fun onHit(attacker: Soldier, attacked: Soldier, weapon: Weapon, bullet: Bullet) {
            TODO("Not yet implemented")
        }
    }

    private inner class OnFire : OEngine.FireListener {
        override fun onFire(holder: Soldier, weapon: Weapon) {
            TODO("Not yet implemented")
        }
    }

    private inner class OnTick : OEngine.TickListener {
        override fun onTick(tick: Long) {
            TODO("Not yet implemented")
        }
    }
}