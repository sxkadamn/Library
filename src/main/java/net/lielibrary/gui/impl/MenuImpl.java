package net.lielibrary.gui.impl;

import net.lielibrary.AnimatedMenu;
import net.lielibrary.gui.customize.AnimationType;
import org.bukkit.Material;
import org.bukkit.Sound;


public class MenuImpl extends AnimatedMenu {
    public MenuImpl(String title, int rows, AnimationType animationType, Material fillMaterial, int speed, Sound sound, boolean updateEnabled) {
        super(title, rows, animationType, fillMaterial, speed, sound, updateEnabled);
    }
}
