package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import org.bukkit.Bukkit

class StringDeobfuscationAnimationProp(
    val id: String,
    override val animation: Animation,
    val deobfuscateEvery: Int,
    deobfuscationString: String,
    val obfuscatedFormat: String,
    val deobfuscatedFormat: String,
    val deobfuscationActions: List<ConfiguredExecutableObject<Animation,Unit>>
) : AnimationProp() {

    val obfuscationString = animation.updatePlaceholders(deobfuscationString)
    val length = obfuscationString.length
    var deobfuscated = 0

    @Volatile
    private var currentString = "$obfuscatedFormat$obfuscationString"

    init {
        animation.extraPlaceholders += "stringdeobfuscation:$id" to { str ->
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
        deobfuscated++
        if (deobfuscated >= length) return

        val obfuscated = obfuscationString.substring(0, (length-1)-deobfuscated)
        val deobfuscated = obfuscationString.substring((length-1)-deobfuscated, length)

        currentString = "$obfuscatedFormat$obfuscated$deobfuscatedFormat$deobfuscated"
        deobfuscationActions.executeActions(animation) { animation, s ->
            animation.updatePlaceholders(s)
        }
    }

    override fun onAnimationEnd() {

    }
}