package io.github.emojiconmc.recyclingbin.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Consumer;

import java.util.HashMap;
import java.util.Map;

public class Menu {

    protected Inventory inventory;
    protected Map<Integer, MenuButton> buttons;
    private Consumer<Player> openAction, closeAction;

    public Menu(String title, int rows) {
        if (rows < 1 || rows > 6 || title.length() > 32) {
            throw new IllegalArgumentException("Invalid arguments passed to Menu constructor!");
        }

        inventory = Bukkit.createInventory(null, rows * 9,
                ChatColor.translateAlternateColorCodes('&', title));
        buttons = new HashMap<>();
    }

    public void open(Player player) {
        buttons.forEach((slot, button) -> inventory.setItem(slot, button.getItemStack()));
        MenuManager.getInstance().registerMenu(player.getUniqueId(), this);
        player.openInventory(inventory);
        handleOpen(player);
    }

    public void registerButton(int slot, MenuButton button) {
        buttons.put(slot, button);
    }

    public void handleOpen(Player player) {
        if (openAction != null) {
            openAction.accept(player);
        }
    }

    public void handleClose(Player player) {
        if (closeAction != null) {
            closeAction.accept(player);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || !buttons.containsKey(event.getRawSlot())) {
            return;
        }

        Consumer<Player> clickAction = buttons.get(event.getRawSlot()).getClickAction();
        if (clickAction != null) {
            clickAction.accept((Player) event.getWhoClicked());
        }
    }

    public void handleDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void setOpenAction(Consumer<Player> openAction) {
        this.openAction = openAction;
    }

    public void setCloseAction(Consumer<Player> closeAction) {
        this.closeAction = closeAction;
    }
}
