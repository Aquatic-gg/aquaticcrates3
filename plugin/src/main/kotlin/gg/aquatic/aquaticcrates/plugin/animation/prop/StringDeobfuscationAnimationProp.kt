package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class StringDeobfuscationAnimationProp(
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

    var currentString = "$obfuscatedFormat$obfuscationString"

    init {
        animation.extraPlaceholders += { str ->
            str.replace(obfuscatedFormat, currentString)
        }
    }

    private var tick = 0
    override fun tick() {
        if (tick >= deobfuscateEvery) {
            tick = 0
            deobfuscateNext()
        }
    }

    private fun deobfuscateNext() {
        if (deobfuscated >= length) return
        deobfuscated++

        val obfuscated = obfuscationString.substring(0, (length-1)-deobfuscated)
        val deobfuscated = obfuscationString.substring((length-1)-deobfuscated, length-1)

        currentString = "$obfuscatedFormat$obfuscated$deobfuscatedFormat$deobfuscated"
        deobfuscationActions.executeActions(animation) { animation, s ->
            animation.updatePlaceholders(s)
        }

    }

    override fun onAnimationEnd() {

    }
}