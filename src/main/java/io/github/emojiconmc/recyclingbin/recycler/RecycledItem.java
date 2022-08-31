package io.github.emojiconmc.recyclingbin.recycler;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecycledItem {

    private ItemStack playerItemStack, cleanItemStack;
    private List<Recipe> recipes;
    private int currentRecipe;

    public RecycledItem(RecyclingBinPlugin plugin, ItemStack playerItemStack) {
        this.playerItemStack = playerItemStack;
        cleanItemStack = new ItemStack(playerItemStack.getType(), playerItemStack.getAmount());
        recipes = new ArrayList<>();
        currentRecipe = 0;
        if (plugin.getBlacklistManager().isBlacklistedItem(cleanItemStack.getType())) {
            return;
        }

        for (Recipe recipe : Bukkit.getRecipesFor(cleanItemStack)) {
            if (cleanItemStack.getAmount() < recipe.getResult().getAmount()) {
                continue;
            }

            if (recipe instanceof StonecuttingRecipe || recipe instanceof SmithingRecipe) {
                recipes.clear();
                recipes.add(recipe);
                break;
            }

            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                recipes.add(recipe);
            }
        }
    }

    public List<ItemStack> getIngredientList() {
        if (recipes.isEmpty() || currentRecipe >= recipes.size()) {
            return new ArrayList<>();
        }

        Recipe recipe = recipes.get(currentRecipe);
        List<ItemStack> ingredients;
        if (recipe instanceof StonecuttingRecipe) {
            ingredients = Arrays.asList(((StonecuttingRecipe) recipe).getInput());
        } else if (recipe instanceof SmithingRecipe) {
            SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
            ingredients = Arrays.asList(smithingRecipe.getBase().getItemStack(),
                    smithingRecipe.getAddition().getItemStack());
        } else if (recipe instanceof ShapedRecipe) {
            ingredients = new ArrayList<>(((ShapedRecipe) recipe).getIngredientMap().values());
        } else if (recipe instanceof ShapelessRecipe) {
            ingredients = new ArrayList<>(((ShapelessRecipe) recipe).getIngredientList());
        } else {
            return new ArrayList<>();
        }

        Map<Material, Integer> sortedIngredients = new HashMap<>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) {
                continue;
            }

            if (sortedIngredients.containsKey(ingredient.getType())) {
                sortedIngredients.put(ingredient.getType(),
                        sortedIngredients.get(ingredient.getType()) + ingredient.getAmount());
            } else {
                sortedIngredients.put(ingredient.getType(), ingredient.getAmount());
            }
        }

        sortedIngredients.replaceAll((material, amount) -> amount *
                Math.floorDiv(cleanItemStack.getAmount(), recipe.getResult().getAmount()));
        if (playerItemStack.getItemMeta() instanceof Damageable && playerItemStack.getType().getMaxDurability() > 0) {
            short maxDurability = playerItemStack.getType().getMaxDurability();
            double damage = ((Damageable) playerItemStack.getItemMeta()).getDamage();
            double multiplier = (maxDurability - damage) / maxDurability;
            sortedIngredients.replaceAll((material, amount) -> (int) Math.floor(amount * multiplier));
        }

        List<ItemStack> sortedItemStacks = new ArrayList<>();
        sortedIngredients.forEach((material, amount) -> {
            if (amount >= material.getMaxStackSize()) {
                for (int i = material.getMaxStackSize(); i <= amount; i += material.getMaxStackSize()) {
                    sortedItemStacks.add(new ItemStack(material, material.getMaxStackSize()));
                }
            }

            if (amount % material.getMaxStackSize() != 0) {
                sortedItemStacks.add(new ItemStack(material, amount % material.getMaxStackSize()));
            }
        });

        return sortedItemStacks;
    }

    public void switchRecipe() {
        if (recipes.size() > 1) {
            if (currentRecipe + 1 < recipes.size()) {
                currentRecipe++;
            } else {
                currentRecipe = 0;
            }
        }
    }

    public ItemStack getPlayerItemStack() {
        return playerItemStack;
    }
}
