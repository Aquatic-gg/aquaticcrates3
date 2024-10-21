package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationManager
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.InteractHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.crate.interact.BasicInteractHandler
import gg.aquatic.aquaticseries.lib.interactable2.AbstractInteractable

class BasicCrate(
    override val identifier: String,
    override val displayName: String,
    override val hologramSettings: HologramSettings,
    override val interactable: AbstractInteractable<*>,
    val animationManager: AnimationManager,
    val skipAnimationWhileSneaking: Boolean,
    val milestoneManager: MilestoneManager,
    rerollManager: (BasicCrate) -> RerollManager,
    key: (BasicCrate) -> Key,
) : Crate(), OpenableCrate {

    val rerollManager = rerollManager(this)

    override val key = key(this)
    override var interactHandler: InteractHandler = BasicInteractHandler(this)

}