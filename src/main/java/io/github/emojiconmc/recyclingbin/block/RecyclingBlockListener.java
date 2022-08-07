package io.github.emojiconmc.recyclingbin.block;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import io.github.emojiconmc.recyclingbin.recycler.RecyclingMenu;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class RecyclingBlockListener implements Listener {

    private RecyclingBinPlugin plugin;
    private NamespacedKey key;

    public RecyclingBlockListener(RecyclingBinPlugin plugin) {
        this.plugin = plugin;
        key = new NamespacedKey(plugin, "recycling_bin_block");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.PLAYER_HEAD || !(block.getState() instanceof TileState)) {
            return;
        }

        ItemStack itemPlaced;
        if (event.getHand() == EquipmentSlot.HAND) {
            itemPlaced = event.getPlayer().getInventory().getItemInMainHand();
        } else {
            itemPlaced = event.getPlayer().getInventory().getItemInOffHand();
        }

        if (itemPlaced.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("recycling-block-name", ChatColor.GRAY + "Recycling Bin")))) {
            TileState tileState = (TileState) block.getState();
            PersistentDataContainer container = tileState.getPersistentDataContainer();
            container.set(key, UUIDDataType.getInstance(), event.getPlayer().getUniqueId());
            tileState.update();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("recyclingbin.block.use")
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || event.getClickedBlock() == null
                || event.getClickedBlock().getType() != Material.PLAYER_HEAD
                || !(event.getClickedBlock().getState() instanceof TileState)) {
            return;
        }

        PersistentDataContainer container = ((TileState) event.getClickedBlock().getState()).getPersistentDataContainer();
        if (container.has(key, UUIDDataType.getInstance())) {
            new RecyclingMenu(plugin).open(event.getPlayer());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE
                || event.getBlock().getType() != Material.PLAYER_HEAD
                || !(event.getBlock().getState() instanceof TileState)) {
            return;
        }

        PersistentDataContainer container = ((TileState) event.getBlock().getState()).getPersistentDataContainer();
        if (container.has(key, UUIDDataType.getInstance())) {
            event.setDropItems(false);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    RecyclingBlock.getInstance(plugin).getItemStack());
        }
    }
}
