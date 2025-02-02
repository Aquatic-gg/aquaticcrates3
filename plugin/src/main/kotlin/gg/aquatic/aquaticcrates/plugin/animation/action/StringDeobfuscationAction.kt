package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.StringDeobfuscationAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class StringDeobfuscationAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("deobfuscate-every", 1, false),
        PrimitiveObjectArgument("id", "example", false),
        PrimitiveObjectArgument("deobfuscation-string", "example", true),
        PrimitiveObjectArgument("obfuscated-format", "<obfuscated><gray>", true),
        PrimitiveObjectArgument("deobfuscated-format", "<white>", true),
        ActionsArgument("deobfuscation-actions", CrateAnimationActions(), false),
        PrimitiveObjectArgument("strip-colors", defaultValue = true, required = false),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val deobfuscateEvery = args.int("deobfuscate-every") { textUpdater(binder, it) } ?: return
        val deobfuscationString = args.string("deobfuscation-string") { textUpdater(binder, it) } ?: return
        val obfuscatedFormat = args.string("obfuscated-format") { textUpdater(binder, it) } ?: return
        val deobfuscatedFormat = args.string("deobfuscated-format") { textUpdater(binder, it) } ?: return
        val deobfuscationActions = args.typed<CrateAnimationActions>("deobfuscation-actions") { textUpdater(binder, it) } ?: return
        val id = args.string("id") { textUpdater(binder, it) } ?: "example"
        val stripColors = args.boolean("strip-colors") { textUpdater(binder, it) } ?: false

        val prop = StringDeobfuscationAnimationProp(
            id,
            binder,
            deobfuscateEvery,
            deobfuscationString,
            obfuscatedFormat,
            deobfuscatedFormat,
            deobfuscationActions,
            stripColors,
        )
        binder.props["deobfuscation:$id"] = prop
    }
}