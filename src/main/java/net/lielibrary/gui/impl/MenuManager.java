package net.lielibrary.gui.impl;

import net.lielibrary.AnimatedMenu;
import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.customize.AnimationType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public class MenuManager {
    private final HashMap<String, HashMap<String, AnimatedMenu>> guis;
    private final HashMap<Inventory, AnimatedMenu> inv_guis;

    public MenuManager() {
        guis = new HashMap<>();
        inv_guis = new HashMap<>();
    }

    public AnimatedMenu createMenuFromConfig(String title, int rows, Player player) {
        ConfigurationSection config = Plugin.getLibrary().getConfig().getConfigurationSection("menu.animation");

        if (config == null) {
            throw new IllegalStateException("Configuration section 'menu.animation' not found in config.yml");
        }

        AnimationType type = AnimationType.fromString(config.getString("type", "FILL_GLASS"));
        Material material = Material.matchMaterial(config.getString("material", "GRAY_STAINED_GLASS_PANE"));
        if (material == null) {
            material = Material.GRAY_STAINED_GLASS_PANE;
        }
        int speed = config.getInt("speed", 5);
        Sound sound;
        try {
            sound = Sound.valueOf(config.getString("sound", "BLOCK_NOTE_BLOCK_BANJO"));
        } catch (IllegalArgumentException e) {
            sound = Sound.BLOCK_NOTE_BLOCK_BANJO;
        }
        float soundVolume = (float) config.getDouble("sound_volume", 1.0);
        float soundPitch = (float) config.getDouble("sound_pitch", 1.0);
        boolean autoUpdate = config.getBoolean("autoUpdate", false);

        AnimatedMenu animatedMenu = new MenuImpl(title, rows, type, material, speed, sound, soundVolume, soundPitch, autoUpdate, Plugin.getLibrary().getConfig());

        return registerGUI(title, player.getName(), animatedMenu);
    }

    public void applyAnimationEffectToAllMenus(Consumer<Player> effect) {
        for (HashMap<String, AnimatedMenu> playerMenus : guis.values()) {
            for (AnimatedMenu menu : playerMenus.values()) {
                menu.addAnimationEffect(effect);
            }
        }
    }

    public AnimatedMenu registerGUI(String id, String player, AnimatedMenu menu) {
        guis.computeIfAbsent(player, k -> new HashMap<>()).put(id, menu);
        inv_guis.put(menu.getInventory(), menu);
        return menu;
    }

    public Collection<AnimatedMenu> getMenus(String player) {
        return guis.getOrDefault(player, new HashMap<>()).values();
    }

    public AnimatedMenu getMenu(Inventory inventory) {
        return inv_guis.get(inventory);
    }

    public AnimatedMenu getMenu(String player, String id) {
        return guis.getOrDefault(player, new HashMap<>()).get(id);
    }

    public void removeMenus(String player) {
        HashMap<String, AnimatedMenu> playerMenus = guis.remove(player);
        if (playerMenus != null) {
            playerMenus.values().forEach(menu -> inv_guis.remove(menu.getInventory()));
        }
    }

    public void closeAllMenusForPlayer(String player) {
        Player p = Bukkit.getPlayerExact(player);
        if (p != null && guis.containsKey(player)) {
            for (AnimatedMenu menu : guis.get(player).values()) {
                if (menu.getInventory().getViewers().contains(p)) {
                    p.closeInventory();
                }
            }
        }
    }

    public boolean isMenuOpen(String player, String id) {
        AnimatedMenu menu = getMenu(player, id);
        Player p = Bukkit.getPlayerExact(player);
        return menu != null && p != null && menu.getInventory().getViewers().contains(p);
    }

    public void closeAllMenus() {
        for (AnimatedMenu menu : inv_guis.values()) {
            for (HumanEntity viewer : menu.getInventory().getViewers()) {
                viewer.closeInventory();
            }
        }
        inv_guis.clear();
    }

    public void reloadMenusForPlayer(String player) {
        if (guis.containsKey(player)) {
            HashMap<String, AnimatedMenu> playerMenus = guis.get(player);
            for (AnimatedMenu menu : playerMenus.values()) {
                Inventory inv = menu.getInventory();
                for (HumanEntity viewer : inv.getViewers()) {
                    viewer.closeInventory();
                }
                inv_guis.remove(inv);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        menu.refreshItems();
                        inv_guis.put(menu.getInventory(), menu);
                    }
                }.runTask(Plugin.getLibrary());
            }
        }
    }
}