package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class WaveAnimation implements AnimationStrategy {
    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        int size = inventory.getSize();
        int rows = size / 9;
        List<Integer> slots = new ArrayList<>(size);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                if (slot < size) {
                    slots.add(slot);
                }
            }
        }

        return slots;
    }
}