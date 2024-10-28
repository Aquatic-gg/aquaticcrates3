package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.util.Vector
import java.util.function.BiFunction

class ShowModelAction: AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val model = args["model"] as String
        val applySkin = args["apply-skin"] as Boolean
        val animation = args["animation"] as String?
        val boundPropertiesFactory = args["bound-paths"] as ((Animation) -> MutableMap<PathProp, PathBoundProperties>)? ?: { _ -> hashMapOf() }

        val boundPaths = boundPropertiesFactory(binder)
        val prop = ModelAnimationProp(
            binder,
            model,
            if (applySkin) binder.player else null,
            animation,
            args["offset"] as Vector,
            boundPaths
        )

        binder.props["model:$id"] = prop
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "example", true),
            PrimitiveObjectArgument("model","",true),
            PrimitiveObjectArgument("apply-skin", true, required = false),
            PrimitiveObjectArgument("animation",null,true),
            VectorArgument("offset",Vector(), false),
            BoundPathObjectArgument(
                "bound-paths",
                { _ -> hashMapOf() },
                false
            )
        )
    }
}