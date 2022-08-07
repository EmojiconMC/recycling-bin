package io.github.emojiconmc.recyclingbin.file;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.Material;

public class BlacklistFile extends PluginFile {

    public BlacklistFile(RecyclingBinPlugin plugin) {
        super(plugin, "blacklist.yml");
    }

    public boolean isBlacklistedItem(Material material) {
        for (String blacklistedMaterial : getFileConfig().getStringList("blacklist")) {
            if (Material.getMaterial(blacklistedMaterial) != null
                    && blacklistedMaterial.equalsIgnoreCase(material.name())) {
                return true;
            }
        }

        return false;
    }
}
