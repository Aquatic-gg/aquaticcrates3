package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.CinematicAnimationImpl
import gg.aquatic.aquaticcrates.plugin.animation.prop.CameraAnimationProp
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.location.AquaticLocation
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CinematicAnimationSettings(
    override val animationTasks: TreeMap<Int, CrateAnimationActions>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, CrateAnimationActions>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, CrateAnimationActions>,
    override val finalAnimationTasks: CrateAnimationActions,
    override val skippable: Boolean,
    val cinematicLocation: AquaticLocation,
    val cameraLocation: Pair<Vector, Pair<Float,Float>>
) : CrateAnimationSettings() {

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<Void> {
        val cinematicLocation = this.cinematicLocation.toLocation()!!
        val animation = CinematicAnimationImpl(
            player,
            animationManager,
            cinematicLocation,
            rolledRewards,
            FilterAudience { it == player },
            CompletableFuture()
        )

        val cameraLocation = cinematicLocation.clone().apply {
            x = cameraLocation.first.x
            y = cameraLocation.first.y
            z = cameraLocation.first.z
            yaw = cameraLocation.second.first
            pitch = cameraLocation.second.second
        }
        val cameraProp = CameraAnimationProp(animation, cameraLocation, Vector(), ConcurrentHashMap(), 0.0f to 0.0f)
        animation.props["camera"] = cameraProp

        animationManager.playAnimation(animation)
        return animation.completionFuture
    }

    override fun canBeOpened(player: Player, animationManager: CrateAnimationManager, location: Location): AnimationResult {
        if (animationManager.playingAnimations.containsKey(player.uniqueId)) return AnimationResult.ALREADY_BEING_OPENED
        return AnimationResult.SUCCESS
    }

    companion object: AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings? {
            if (section == null) return null

            val locationSection = section.getConfigurationSection("location") ?: return null
            val world = locationSection.getString("world") ?: return null
            val x = locationSection.getDouble("x")
            val y = locationSection.getDouble("y")
            val z = locationSection.getDouble("z")
            val pitch = locationSection.getDouble("pitch").toFloat()
            val yaw = locationSection.getDouble("yaw").toFloat()
            val cinematicLocation = AquaticLocation(world, x, y, z, pitch, yaw)

            val cameraSection = section.getConfigurationSection("camera-location") ?: return null
            val cameraX = cameraSection.getDouble("x")
            val cameraY = cameraSection.getDouble("y")
            val cameraZ = cameraSection.getDouble("z")
            val cameraPitch = cameraSection.getDouble("pitch").toFloat()
            val cameraYaw = cameraSection.getDouble("yaw").toFloat()

            val cameraLocation = Pair(Vector(cameraX, cameraY, cameraZ), Pair(cameraYaw, cameraPitch))

            return CinematicAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks")),
                loadAnimationLength(section),
                loadPreAnimationDelay(section),
                loadPreAnimationTasks(section),
                loadPostAnimationDelay(section),
                loadPostAnimationTasks(section),
                loadFinalActions(section),
                loadSkippable(section),
                cinematicLocation,
                cameraLocation
            )
        }

    }
}