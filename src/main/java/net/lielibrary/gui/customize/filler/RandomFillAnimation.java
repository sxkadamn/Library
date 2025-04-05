package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomFillAnimation implements AnimationStrategy {
    private final Random random = new Random();

    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        int size = inventory.getSize();
        List<Integer> slots = new ArrayList<>(size);
        boolean[] used = new boolean[size];

        for (int i = 0; i < size; i++) {
            int slot;
            do {
                slot = random.nextInt(size);
            } while (used[slot]);
            used[slot] = true;
            slots.add(slot);
        }

        return slots;
    }
}