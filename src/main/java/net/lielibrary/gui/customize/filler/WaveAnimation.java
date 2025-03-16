package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class WaveAnimation implements AnimationStrategy {
    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = i; j < inventory.getSize(); j += 9) {
                slots.add(j);
            }
        }
        return slots;
    }
}