package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.CinematicAnimationImpl
import gg.aquatic.aquaticcrates.plugin.animation.prop.CameraAnimationProp
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.location.AquaticLocation
import gg.aquatic.waves.util.task.AsyncCtx
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CinematicAnimationSettings(
    override val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val finalAnimationTasks: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    override val skippable: Boolean,
    val cinematicLocation: AquaticLocation,
    val cameraLocation: Pair<Vector, Pair<Float,Float>>,
    override val variables: Map<String, String>
) : CrateAnimationSettings() {

    override suspend fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CrateAnimation = withContext(AsyncCtx) {
        val cinematicLocation = this@CinematicAnimationSettings.cinematicLocation.toLocation()!!
        val futureValue = CompletableFuture<CrateAnimation>()
        val animation = CinematicAnimationImpl(
            player,
            animationManager,
            cinematicLocation,
            rolledRewards,
            FilterAudience {
                if (it == player) {
                    if (futureValue.get().phase !is CrateAnimation.FinalPhase) {
                        return@FilterAudience true
                    } else {
                        for (prop in futureValue.get().props.values) {
                            prop.onEnd()
                        }
                        futureValue.get().props.clear()
                    }
                }
                false
            },
            CompletableFuture()
        )
        futureValue.complete(animation)

        for (entry in CrateAnimation.EquipmentSlot.entries) {
            animation.playerEquipment[entry] = ItemStack(Material.AIR)
        }

        BukkitCtx {
            player.updateInventory()
        }

        val cameraLocation = cinematicLocation.clone().apply {
            x = cameraLocation.first.x
            y = cameraLocation.first.y
            z = cameraLocation.first.z
            yaw = cameraLocation.second.first
            pitch = cameraLocation.second.second
        }
        val cameraProp = CameraAnimationProp(animation, cameraLocation, Vector(), ConcurrentHashMap(), 0.0f to 0.0f)
        animation.props[Key.key("camera")] = cameraProp

        animationManager.playAnimation(animation)
        animation.completionFuture.join()
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

            val duration = loadAnimationLength(section)
            val delay = loadPreAnimationDelay(section)
            val postDelay = loadPostAnimationDelay(section)
            val variables = HashMap<String, String>()
            section.getConfigurationSection("variables")?.let {
                it.getKeys(false).forEach { key ->
                    variables[key] = it.getString(key) ?: ""
                }
            }
            return CinematicAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks"),duration),
                duration,
                delay,
                loadPreAnimationTasks(section,delay),
                postDelay,
                loadPostAnimationTasks(section,postDelay),
                loadFinalActions(section),
                loadSkippable(section),
                cinematicLocation,
                cameraLocation,
                variables
            )
        }

    }
}