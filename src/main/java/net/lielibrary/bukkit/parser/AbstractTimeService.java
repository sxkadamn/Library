package net.lielibrary.bukkit.parser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractTimeService implements TimeProvider {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public LocalTime parseTime(String time) {
        return LocalTime.parse(time, formatter);
    }
}