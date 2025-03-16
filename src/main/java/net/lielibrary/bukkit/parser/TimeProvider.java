package net.lielibrary.bukkit.parser;

import java.time.LocalTime;

public interface TimeProvider {
    LocalTime getCurrentTime();
    LocalTime parseTime(String time);
}