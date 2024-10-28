package gg.aquatic.aquaticcrates.plugin.animation.prop.path

class PathBoundProperties(
    val offset: PathPoint,
    val offsetType: OffsetType,
    val affectYawPitch: Boolean
) {

    enum class OffsetType {
        STATIC,
        DYNAMIC
    }

}