package com.ouhinformation.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public static String format(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isEmpty()) {
            return "";
        }
        try {
            return Instant.parse(isoDateTime)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter);
        } catch (Exception e) {
            return isoDateTime; // fallback to raw
        }
    }
}
