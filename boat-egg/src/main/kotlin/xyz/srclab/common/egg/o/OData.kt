package xyz.srclab.common.egg.o

class OData {

    val group1: Group
    val group2: Group
    val tick: OTick

    val viewWidth: Int
    val viewHeight: Int
    val viewWidthBuffer: Int
    val viewHeightBuffer: Int

    constructor() {
        group1 = Group()
        group2 = Group()
        tick = OTick()

        viewWidth = ODefaults.viewWidth
        viewHeight = ODefaults.viewHeight
        viewWidthBuffer = ODefaults.viewWidthBuffer
        viewHeightBuffer = ODefaults.viewHeightBuffer
    }

    constructor(save: OSave) {
        group1 = Group()
        group2 = Group()
        tick = OTick(save.tick)
        group1.players.putAll(save.group1?.players ?: emptyMap())
        group1.soldiers.putAll(save.group1?.soldiers ?: emptyMap())
        group2.players.putAll(save.group2?.players ?: emptyMap())
        group2.soldiers.putAll(save.group2?.soldiers ?: emptyMap())

        viewWidth = save.viewWidth
        viewHeight = save.viewHeight
        viewWidthBuffer = save.viewWidthBuffer
        viewHeightBuffer = save.viewHeightBuffer
    }

    open class Group {
        var players: MutableMap<Long, Player> = HashMap()
        var soldiers: MutableMap<Long, Soldier> = HashMap()
    }
}