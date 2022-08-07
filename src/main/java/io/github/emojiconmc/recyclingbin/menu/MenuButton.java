package io.github.emojiconmc.recyclingbin.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

public class MenuButton {

    private final ItemStack itemStack;
    private Consumer<Player> clickAction;

    public MenuButton(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public MenuButton(Material material) {
        this(new ItemStack(material));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Consumer<Player> getClickAction() {
        return clickAction;
    }

    public MenuButton setClickAction(Consumer<Player> clickAction) {
        this.clickAction = clickAction;
        return this;
    }
}
