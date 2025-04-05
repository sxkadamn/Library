package net.lielibrary.gui.impl;

import net.lielibrary.AnimatedMenu;
import net.lielibrary.gui.customize.AnimationType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;


public class MenuImpl extends AnimatedMenu {
    public MenuImpl(String title, int rows, AnimationType animationType, Material fillMaterial, int speed, Sound sound, boolean updateEnabled, @NotNull FileConfiguration config) {
        super(title, rows, animationType, fillMaterial, speed, sound, updateEnabled, config);
    }
}
