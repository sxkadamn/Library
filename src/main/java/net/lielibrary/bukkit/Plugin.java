package net.lielibrary.bukkit;

import net.lielibrary.bukkit.color.BukkitColor;
import net.lielibrary.bukkit.color.ColorImpl;
import net.lielibrary.gui.impl.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    private static Plugin library;

    private static MenuManager menuManager;

    private static BukkitColor withColor;

    @Override
    public void onEnable() {
        library = this;

        withColor = new ColorImpl();
        menuManager = new MenuManager();
    }


    public static Plugin getLibrary() {
        return library;
    }

    public static MenuManager getMenuManager() {
        return menuManager;
    }

    public static BukkitColor getWithColor() {
        return withColor;
    }
}
