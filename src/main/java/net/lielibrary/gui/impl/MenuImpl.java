package net.lielibrary.gui.impl;

import net.lielibrary.AnimatedMenu;
import net.lielibrary.gui.customize.AnimationType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class MenuImpl extends AnimatedMenu {
    public MenuImpl(String title, int rows, AnimationType animationType, Material fillMaterial, int speed,
                    Sound sound, float soundVolume, float soundPitch, boolean updateEnabled, FileConfiguration config) {
        super(title, rows, animationType, fillMaterial, speed, sound, soundVolume, soundPitch, updateEnabled, config, -1, -1);
    }
}