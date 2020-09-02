package org.diploma.app.model.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {

    public static long toTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
