package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.stream.IntStream;

public class FillGlassAnimation implements AnimationStrategy {
    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        return IntStream.range(0, inventory.getSize())
                .boxed()
                .toList();
    }
}