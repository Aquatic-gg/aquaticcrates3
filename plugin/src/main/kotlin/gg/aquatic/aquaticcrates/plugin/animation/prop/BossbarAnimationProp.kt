package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.bossbar.AquaticBossBar
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class BossbarAnimationProp(
    override val animation: Animation,
    var text: String,
    color: BossBar.Color,
    style: BossBar.Overlay,
    progress: Float,
    var textUpdater: (Player, String) -> String = { _, str -> str }
) : AnimationProp() {

    val bossBar = AquaticBossBar(textUpdater(animation.player,text).toMMComponent(), color, style, mutableSetOf(), progress)

    init {
        bossBar.addViewer(animation.player)
    }

    override fun tick() {
        val newMsg = textUpdater(animation.player,animation.updatePlaceholders(text)).toMMComponent()
        bossBar.message = newMsg
        val updatedText = animation.updatePlaceholders(text)
        Bukkit.broadcastMessage("Updated text from BossbarAnimationProp: $updatedText")
    }

    override fun onAnimationEnd() {
        bossBar.removeViewer(animation.player)
    }
}