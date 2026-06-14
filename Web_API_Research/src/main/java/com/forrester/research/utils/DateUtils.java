package com.forrester.research.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class DateUtils {

    private DateUtils() {
        //Private constructor to hide the implicit public one.
        throw new IllegalStateException("Utility class");
    }


    public static String convertDateToISOFormat(String date) {
        if (null != date) {
            if (date.contains("T")) {
                date = date.substring(0, date.indexOf('T'));
            }
            String [] str = date.split("-");
            DateTimeFormatter formatterWithThreeDecimals = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'");
            OffsetDateTime dd = OffsetDateTime.of(LocalDate.of(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2])),
                    LocalTime.of(5, 0, 0, 000),
                    ZoneOffset.of(TimeZone.getTimeZone("EST").toZoneId().getId()));
            return dd.format(formatterWithThreeDecimals);

        }
        return null;
    }

}
