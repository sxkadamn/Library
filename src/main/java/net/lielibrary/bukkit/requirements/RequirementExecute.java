package net.lielibrary.bukkit.requirements;

import org.bukkit.entity.Player;

import java.util.List;

public class RequirementExecute {
    public static void execute(List<String> commands, Player player) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String commandString : commands) {
            RequireHandler handler = RequirementFactory.create(commandString);
            if (handler != null) {
                handler.execute(player);
            }
        }
    }
}
