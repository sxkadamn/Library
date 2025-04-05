package net.lielibrary.gui.customize;

import org.bukkit.inventory.Inventory;

import java.util.List;

public interface AnimationStrategy {
    List<Integer> getAnimationSlots(Inventory inventory);

    default List<Integer> getAnimationSlots(Inventory inventory, int startSlot, int endSlot) {
        List<Integer> slots = getAnimationSlots(inventory);
        return slots.stream()
                .filter(slot -> slot >= startSlot && slot <= endSlot)
                .toList();
    }
}