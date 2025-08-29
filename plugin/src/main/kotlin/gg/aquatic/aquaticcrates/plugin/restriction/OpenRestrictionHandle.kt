package gg.aquatic.aquaticcrates.plugin.restriction

import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.requirement.ConfiguredRequirement

class OpenRestrictionHandle(
    val restriction: ConfiguredRequirement<OpenData>,
    val failActions: Collection<ConfiguredExecutableObject<OpenData, Unit>>
) {

    fun check(openData: OpenData, updater: (OpenData, String) -> String): Boolean {
        if (!restriction.execute(openData, updater)) {
            failActions.forEach { it.execute(openData, updater) }
            return false
        }
        return true
    }

}