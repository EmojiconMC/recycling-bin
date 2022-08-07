package io.github.emojiconmc.recyclingbin.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private static MenuManager instance;
    private final Map<UUID, Menu> menus;

    private MenuManager() {
        menus = new HashMap<>();
    }

    public void registerMenu(UUID playerUUID, Menu menu) {
        menus.put(playerUUID, menu);
    }

    public void unregisterMenu(UUID playerUUID) {
        menus.remove(playerUUID);
    }

    public Menu getMenu(UUID playerUUID) {
        return menus.get(playerUUID);
    }

    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }

        return instance;
    }
}
