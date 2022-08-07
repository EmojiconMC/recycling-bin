package io.github.emojiconmc.recyclingbin.block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class RecyclingBlock {

    private static RecyclingBlock instance;
    private ItemStack itemStack;

    private RecyclingBlock(RecyclingBinPlugin plugin) {
        itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta blockMeta = (SkullMeta) itemStack.getItemMeta();
        blockMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("recycling-block-name", ChatColor.GRAY + "Recycling Bin")));
        GameProfile gameProfile = new GameProfile(UUID.fromString("b9653dbf-3499-433f-bb03-5a03c1a2fc25"), null);
        gameProfile.getProperties().put("textures", new Property("textures", plugin.getConfig().getString("recycling-block-texture",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI1MThkMDRmOWMwNmM5NWRkMGVkYWQ2MTdhYmI5M2QzZDg2NTdmMDFlNjU5MDc5ZDMzMGNjYTZmNjViY2NmNyJ9fX0=")));
        try {
            Field field = blockMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(blockMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        itemStack.setItemMeta(blockMeta);
    }

    public void initRecipe(RecyclingBinPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "recycling_block_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, itemStack);

        List<String> recipeShapeList = plugin.getConfig().getStringList("recipe-shape");
        if (recipeShapeList.size() != 3) {
            plugin.getLogger().warning("Improper recipe shape in config.yml!");
            return;
        }

        for (String item : recipeShapeList) {
            if (item.length() != 3) {
                plugin.getLogger().warning("Improper recipe length in config.yml!");
                return;
            }
        }

        recipe.shape(recipeShapeList.get(0), recipeShapeList.get(1), recipeShapeList.get(2));

        if (!plugin.getConfig().isConfigurationSection("recipe-ingredients")) {
            return;
        }

        for (String ingredientKeyValue : plugin.getConfig().getConfigurationSection("recipe-ingredients").getKeys(false)) {
            if (ingredientKeyValue.length() != 1) {
                plugin.getLogger().warning("Improper recipe ingredient keys in config.yml!");
                return;
            }

            char ingredientKey = ingredientKeyValue.charAt(0);
            String ingredient = plugin.getConfig().getString("recipe-ingredients." + ingredientKey);
            if (ingredient == null) {
                plugin.getLogger().warning("Invalid recipe ingredients in config.yml!");
                return;
            }

            Material type = Material.getMaterial(ingredient);
            if (type == null) {
                plugin.getLogger().warning("Invalid recipe ingredients in config.yml!");
                return;
            }

            recipe.setIngredient(ingredientKey, type);
        }

        Bukkit.addRecipe(recipe);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static RecyclingBlock getInstance(RecyclingBinPlugin plugin) {
        if (instance == null) {
            instance = new RecyclingBlock(plugin);
        }

        return instance;
    }
}
