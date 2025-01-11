package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.StringDeobfuscationAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class StringDeobfuscationAction: AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("deobfuscate-every",1,false),
        PrimitiveObjectArgument("id","example",false),
        PrimitiveObjectArgument("deobfuscation-string","example",true),
        PrimitiveObjectArgument("obfuscated-format","<obfuscated><gray>",true),
        PrimitiveObjectArgument("deobfuscated-format","<white>",true),
        ActionsArgument("deobfuscation-actions", CrateAnimationActions(mutableListOf(), mutableListOf()),false)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val deobfuscateEvery = args["deobfuscate-every"] as Int
        val deobfuscationString = args["deobfuscation-string"] as String
        val obfuscatedFormat = args["obfuscated-format"] as String
        val deobfuscatedFormat = args["deobfuscated-format"] as String
        val deobfuscationActions = args["deobfuscation-actions"] as CrateAnimationActions
        val id = args["id"] as String

        val prop = StringDeobfuscationAnimationProp(
            id,
            binder,
            deobfuscateEvery,
            deobfuscationString,
            obfuscatedFormat,
            deobfuscatedFormat,
            deobfuscationActions
        )
        binder.props["deobfuscation:$id"] = prop
    }
}