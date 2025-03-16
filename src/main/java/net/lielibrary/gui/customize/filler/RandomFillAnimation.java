package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomFillAnimation implements AnimationStrategy {
    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);
        return slots;
    }
}