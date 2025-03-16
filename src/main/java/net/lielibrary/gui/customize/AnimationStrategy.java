package net.lielibrary.gui.customize;

import org.bukkit.inventory.Inventory;

import java.util.List;

public interface AnimationStrategy {
    List<Integer> getAnimationSlots(Inventory inventory);
}