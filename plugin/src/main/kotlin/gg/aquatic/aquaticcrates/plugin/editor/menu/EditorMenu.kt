package gg.aquatic.aquaticcrates.plugin.editor.menu

import com.google.gson.Gson
import gg.aquatic.aquaticcrates.api.util.createItem
import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.data.CrateModel
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

class EditorMenu(
    val crateModel: CrateModel,
    player: Player,
    val category: EditorCategory,
    val previousMenu: EditorMenu? = null
) : PrivateAquaticMenu(Component.text("Editor | ${crateModel.id}"), InventoryType.GENERIC9X6, player, true) {

    init {
        var slot = 0
        for ((id,item) in category.items) {
            player.sendMessage("Adding $id to slot $slot")
            addComponent(item.createButton(id,slot) { str, _ -> str })
            slot++
        }

        addComponent(
            Button(
                "bg",
                createItem(Material.BLACK_STAINED_GLASS_PANE) {
                    displayName = " "
                },
                (45..53).toList(),
                1,
                -1,
                null
            )
        )
        if (previousMenu != null) {
            addComponent(
                Button(
                    "previous",
                    createItem(Material.ARROW) {
                        displayName = "Previous"
                    },
                    listOf(49),
                    2,
                    -1,
                    null,
                    onClick = {
                        val previousCategory = previousMenu.category
                        val menu = EditorMenu(crateModel, player, previousCategory.refresh(), previousMenu.previousMenu)
                        menu.open()
                    }
                )
            )
        }

        player.sendMessage(Gson().toJson(crateModel))
    }
}