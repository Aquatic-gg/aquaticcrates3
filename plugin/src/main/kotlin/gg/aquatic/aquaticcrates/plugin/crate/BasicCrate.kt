package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationManager
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.InteractHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.crate.key.KeyImpl
import gg.aquatic.aquaticseries.lib.interactable2.AbstractInteractable
import gg.aquatic.aquaticseries.lib.item2.AquaticItem

class BasicCrate(
    override val identifier: String,
    override val displayName: String,
    override val hologramSettings: HologramSettings,
    override val interactHandler: InteractHandler,
    override val interactable: AbstractInteractable<*>,
    val animationManager: AnimationManager,
    val skipAnimationWhileSneaking: Boolean,
    val milestoneManager: MilestoneManager,
    val rerollManager: RerollManager,
    keyItem: AquaticItem,
    keyMustBeHeld: Boolean,
    keyRequiresCrateToOpen: Boolean,
) : Crate(), OpenableCrate {

    override val key = KeyImpl(this, keyItem, keyRequiresCrateToOpen, keyMustBeHeld)

}