package io.github.emojiconmc.recyclingbin.recycler;

import com.google.common.collect.ImmutableList;
import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import io.github.emojiconmc.recyclingbin.file.LangFile;
import io.github.emojiconmc.recyclingbin.menu.Menu;
import io.github.emojiconmc.recyclingbin.menu.MenuButton;
import io.github.emojiconmc.recyclingbin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class RecyclingMenu extends Menu {

    private RecyclingBinPlugin plugin;

    private boolean clickedButton;
    private RecycledItem recycledItem;

    private int playerSlot;
    private ImmutableList<Integer> outputSlots;

    public RecyclingMenu(RecyclingBinPlugin plugin) {
        super(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menu-name",
                ChatColor.GREEN + "Recycling Menu!")), 6);
        this.plugin = plugin;

        clickedButton = false;
        playerSlot = 20;
        outputSlots = ImmutableList.of(14, 15, 16, 23, 24, 25, 32, 33, 34);

        setCloseAction(player -> {
            if (!clickedButton && recycledItem != null) {
                player.getInventory().addItem(recycledItem.getPlayerItemStack());
            }
        });

        LangFile lang = plugin.getLangFile();
        registerButton(45, new MenuButton(new ItemBuilder(Material.GREEN_TERRACOTTA)
                .setDisplayName(lang.getExactMessage("accept-button-name", ChatColor.GREEN + "Accept Recycle"))
                .setLore(lang.getMessageList("accept-button-lore",
                                ChatColor.GRAY + "Convert your item to",
                                ChatColor.GRAY + "its bare ingredients."))
                .build())
                .setClickAction(this::acceptRecycle));
        registerButton(49, new MenuButton(new ItemBuilder(Material.YELLOW_TERRACOTTA)
                .setDisplayName(lang.getExactMessage("switch-recipe-button-name", ChatColor.YELLOW + "Switch Recipe"))
                .setLore(lang.getMessageList("switch-recipe-button-lore",
                        ChatColor.GRAY + "Changes the outputted ingredients",
                        ChatColor.GRAY + "to a different recipe."))
                .build())
                .setClickAction(this::switchRecipe));
        registerButton(53, new MenuButton(new ItemBuilder(Material.RED_TERRACOTTA)
                .setDisplayName(lang.getExactMessage("decline-button-name", ChatColor.RED + "Decline Recycle"))
                .setLore(lang.getMessageList("decline-button-lore",
                                ChatColor.GRAY + "Cancels the trade and",
                                ChatColor.GRAY + "returns your item."))
                .build())
                .setClickAction(this::declineRecycle));

        for (int i : new int[]{10, 11, 12, 19, 21, 28, 29, 30}) {
            registerButton(i, new MenuButton(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("glass-pane-name", "&8 ")))
                    .build()));
        }

        for (int i : new int[]{46, 47, 48, 50, 51, 52}) {
            registerButton(i, new MenuButton(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("glass-pane-name", "&8 ")))
                    .build()));
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i != playerSlot && !outputSlots.contains(i) && !buttons.containsKey(i)) {
                registerButton(i, new MenuButton(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                plugin.getConfig().getString("glass-pane-name", "&8 ")))
                        .build()));
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            event.setCancelled(true);
            PlayerInventory playerInventory = event.getWhoClicked().getInventory();
            Bukkit.getScheduler().runTask(plugin, () -> playerInventory.setItemInOffHand(playerInventory.getItemInOffHand()));
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.CREATIVE
                || outputSlots.contains(event.getRawSlot())) {
            event.setCancelled(true);
            return;
        }

        if (event.getRawSlot() >= inventory.getSize()) {
            if (event.getClick().isShiftClick()) {
                event.setCancelled(true);
            }

            return;
        }

        if (event.getRawSlot() == playerSlot) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    handleRecycle(event.getInventory().getItem(playerSlot));
                }
            }.runTask(plugin);
            return;
        }

        super.handleClick(event);
    }

    @Override
    public void handleDrag(InventoryDragEvent event) {
        if (event.getNewItems().size() == 1 && event.getNewItems().containsKey(playerSlot)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    handleRecycle(event.getInventory().getItem(playerSlot));
                }
            }.runTask(plugin);
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize()) {
                super.handleDrag(event);
                return;
            }
        }
    }

    public void handleRecycle(ItemStack itemStack) {
        if (itemStack == null) {
            recycledItem = null;
            for (int i = 0; i < outputSlots.size(); i++) {
                inventory.setItem(outputSlots.get(i), null);
            }
        } else {
            recycledItem = new RecycledItem(plugin, itemStack);
            recycle();
        }
    }

    private void recycle() {
        List<ItemStack> ingredients = recycledItem.getIngredientList();
        if (!ingredients.isEmpty()) {
            for (int i = 0; i < ingredients.size(); i++) {
                if (i < outputSlots.size()) {
                    inventory.setItem(outputSlots.get(i), ingredients.get(i));
                }
            }
        }

        for (int i = ingredients.size(); i < outputSlots.size(); i++) {
            inventory.setItem(outputSlots.get(i), null);
        }
    }

    public void acceptRecycle(Player player) {
        if (recycledItem == null) {
            return;
        }

        List<ItemStack> ingredientList = recycledItem.getIngredientList();
        if (ingredientList.isEmpty()) {
            declineRecycle(player);
            return;
        }

        for (ItemStack ingredient : ingredientList) {
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(ingredient);
            if (!leftovers.isEmpty()) {
                for (ItemStack leftover : leftovers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }

                player.sendMessage(plugin.getLangFile().getMessage("overflow-items"));
            }
        }

        clickedButton = true;
        player.closeInventory();
    }

    public void switchRecipe(Player player) {
        if (recycledItem != null) {
            recycledItem.switchRecipe();
            recycle();
        }
    }

    public void declineRecycle(Player player) {
        if (recycledItem != null) {
            player.getInventory().addItem(recycledItem.getPlayerItemStack());
        }

        clickedButton = true;
        player.closeInventory();
    }
}
