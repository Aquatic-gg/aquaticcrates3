package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.PlayerScenarioProp
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toPlain
import net.kyori.adventure.key.Key

class StringDeobfuscationAnimationProp(
    val id: String,
    override val scenario: PlayerScenario,
    val deobfuscateEvery: Int,
    deobfuscationString: String,
    val obfuscatedFormat: String,
    val deobfuscatedFormat: String,
    val deobfuscationActions: Collection<ConfiguredExecutableObject<PlayerScenario, Unit>>,
    stripColors: Boolean
) : PlayerScenarioProp {

    val obfuscationString = if (stripColors) scenario.updatePlaceholders(deobfuscationString).toMMComponent()
        .toPlain() else scenario.updatePlaceholders(deobfuscationString)
    val length = obfuscationString.length
    var deobfuscated = 0

    @Volatile
    private var currentString = "$obfuscatedFormat$obfuscationString"

    init {
        scenario.extraPlaceholders += Key.key("stringdeobfuscation:$id") to { str ->
            str.replace("%stringdeobfuscation:$id%", currentString)
        }
    }

    private var tick = 0
    override fun tick() {
        tick++
        if (tick >= deobfuscateEvery) {
            tick = 0
            deobfuscateNext()
        }
    }

    private fun deobfuscateNext() {
        if (deobfuscated > length) return

        val obfuscated = obfuscationString.take((length) - deobfuscated)
        val deobfuscated = if (deobfuscated == this.length) obfuscationString else obfuscationString.substring((length) - deobfuscated, length)
        currentString = "$obfuscatedFormat$obfuscated$deobfuscatedFormat$deobfuscated"

        deobfuscationActions.executeActions(scenario) { a, str ->
            a.updatePlaceholders(str)
        }
        this.deobfuscated++
    }

    override fun onEnd() {

    }
}