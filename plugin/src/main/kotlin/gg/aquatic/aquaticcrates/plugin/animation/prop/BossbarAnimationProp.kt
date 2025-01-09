package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.PlayerBoundAnimationProp
import gg.aquatic.waves.util.bossbar.AquaticBossBar
import gg.aquatic.waves.util.toMMComponent
import net.kyori.adventure.bossbar.BossBar

class BossbarAnimationProp(
    override val animation: PlayerBoundAnimation,
    @Volatile var text: String,
    color: BossBar.Color,
    style: BossBar.Overlay,
    progress: Float,
    //var textUpdater: (Player, String) -> String = { _, str -> str }
) : PlayerBoundAnimationProp() {

    val bossBar = AquaticBossBar(
        animation.updatePlaceholders(text).toMMComponent(), color, style, mutableSetOf(), progress)

    init {
        bossBar.addViewer(animation.player)
    }

    override fun tick() {
        val newMsg = animation.updatePlaceholders(text).toMMComponent()
        bossBar.message = newMsg
    }

    override fun onAnimationEnd() {
        bossBar.removeViewer(animation.player)
    }
}