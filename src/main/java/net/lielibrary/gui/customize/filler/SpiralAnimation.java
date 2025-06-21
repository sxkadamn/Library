package net.lielibrary.gui.customize.filler;

import net.lielibrary.gui.customize.AnimationStrategy;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SpiralAnimation implements AnimationStrategy {
    @Override
    public List<Integer> getAnimationSlots(Inventory inventory) {
        int size = inventory.getSize();
        int rows = size / 9;
        int cols = 9;
        List<Integer> slots = new ArrayList<>(size);
        boolean[][] visited = new boolean[rows][cols];
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        int row = rows / 2;
        int col = cols / 2;
        int dir = 0;
        int steps = 1;
        int stepsTaken = 0;
        int turnCounter = 0;

        while (slots.size() < size) {
            if (row >= 0 && row < rows && col >= 0 && col < cols && !visited[row][col]) {
                int slot = row * 9 + col;
                if (slot < size) {
                    slots.add(slot);
                    visited[row][col] = true;
                }
            }
            row += directions[dir][0];
            col += directions[dir][1];
            stepsTaken++;

            if (stepsTaken == steps) {
                stepsTaken = 0;
                dir = (dir + 1) % 4;
                turnCounter++;
                if (turnCounter == 2) {
                    steps++;
                    turnCounter = 0;
                }
            }
        }

        return slots;
    }
}