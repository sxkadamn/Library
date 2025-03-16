package net.lielibrary.bukkit.parser.service;

import net.lielibrary.bukkit.parser.AbstractTimeService;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.TimeZone;

public class MoscowTimeService extends AbstractTimeService {
    private final TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    public MoscowTimeService() {
        dateFormat.setTimeZone(timeZone);
    }

    @Override
    public LocalTime getCurrentTime() {
        String nowString = dateFormat.format(new Date());
        return LocalTime.parse(nowString, formatter);
    }
}

