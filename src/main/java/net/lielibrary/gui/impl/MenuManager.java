package net.lielibrary.gui.impl;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.Menu;
import net.lielibrary.gui.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

public final class MenuManager {

    private final HashMap<String, HashMap<String, Menu>> guis;
    private final HashMap<Inventory, Menu> inv_guis;

    public MenuManager() {
        guis = new HashMap<>();
        inv_guis = new HashMap<>();
    }

    public Menu createGUI(String id, String player, String title, int rows, boolean updateEnabled) {
        Menu menu = new Menu(id, title, rows, updateEnabled);
        registerGUI(id, player, menu);
        return menu;
    }

    public Menu createGUI(String id, String player, Text title, int rows, boolean updateEnabled) {
        return createGUI(id, player, title.getRaw(), rows, updateEnabled);
    }

    public Menu createGUI(String id, String player, String title, InventoryType inventoryType, boolean updateEnabled) {
        Menu menu = new Menu(id, Bukkit.getPlayerExact(player), title, inventoryType, updateEnabled);
        registerGUI(id, player, menu);
        return menu;
    }

    public void registerGUI(String id, String player, Menu menu) {
        guis.computeIfAbsent(player, k -> new HashMap<>()).put(id, menu);
        inv_guis.put(menu.getInventory(), menu);
    }

    public Collection<Menu> getMenus(String player) {
        return guis.getOrDefault(player, new HashMap<>()).values();
    }

    public Menu getMenu(Inventory inventory) {
        return inv_guis.get(inventory);
    }

    public Menu getMenu(String player, String id) {
        return guis.getOrDefault(player, new HashMap<>()).get(id);
    }

    public void removeMenus(String player) {
        guis.remove(player);
    }

    public void closeAllMenusForPlayer(String player) {
        Player p = Bukkit.getPlayerExact(player);
        if (p != null && guis.containsKey(player)) {
            for (Menu menu : guis.get(player).values()) {
                if (menu.getInventory().getViewers().contains(p)) {
                    p.closeInventory();
                }
            }
        }
    }

    public boolean isMenuOpen(String player, String id) {
        Menu menu = getMenu(player, id);
        Player p = Bukkit.getPlayerExact(player);
        return menu != null && p != null && menu.getInventory().getViewers().contains(p);
    }

    public void closeAllMenus() {
        for (Menu menu : inv_guis.values()) {
            for (HumanEntity viewer : menu.getInventory().getViewers()) {
                viewer.closeInventory();
            }
        }
    }

    public void reloadMenusForPlayer(String player) {
        if (guis.containsKey(player)) {
            HashMap<String, Menu> playerMenus = guis.get(player);
            for (Menu menu : playerMenus.values()) {
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