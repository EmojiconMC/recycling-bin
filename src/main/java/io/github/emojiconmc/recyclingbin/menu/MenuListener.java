package io.github.emojiconmc.recyclingbin.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Menu menu = MenuManager.getInstance().getMenu(event.getWhoClicked().getUniqueId());
        if (menu != null) {
            menu.handleClick(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Menu menu = MenuManager.getInstance().getMenu(event.getWhoClicked().getUniqueId());
        if (menu != null) {
            menu.handleDrag(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Menu menu = MenuManager.getInstance().getMenu(event.getPlayer().getUniqueId());
        if (menu != null) {
            menu.handleClose((Player) event.getPlayer());
        }

        MenuManager.getInstance().unregisterMenu(event.getPlayer().getUniqueId());
        ((Player) event.getPlayer()).updateInventory();
    }
}
