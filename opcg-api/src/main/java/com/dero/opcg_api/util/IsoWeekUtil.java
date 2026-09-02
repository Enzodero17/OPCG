package com.dero.opcg_api.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;

public final class IsoWeekUtil {

    private static final ZoneOffset MISSION_ZONE = ZoneOffset.UTC;

    public static final String NON_WEEKLY_PERIOD_KEY = "ALL";

    private IsoWeekUtil() {
    }

    public static String currentWeekKey() {
        LocalDate today = LocalDate.now(MISSION_ZONE);
        WeekFields weekFields = WeekFields.ISO;
        int week = today.get(weekFields.weekOfWeekBasedYear());
        int weekBasedYear = today.get(weekFields.weekBasedYear());
        return weekBasedYear + "-W" + String.format("%02d", week);
    }
}
