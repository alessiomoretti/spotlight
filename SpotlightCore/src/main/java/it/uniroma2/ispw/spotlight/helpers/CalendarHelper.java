package it.uniroma2.ispw.spotlight.helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * This class contains a static collection of utilities to handle dates
 */
public class CalendarHelper {

    /**
     * Return a date created from the following (ms = 0 by default)
     * @param day Integer
     * @param month Integer
     * @param year Integer
     * @param hour Integer
     * @param minute Integer
     * @return Date
     */
    public static Date getDate(Integer day, Integer month, Integer year, Integer hour, Integer minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Return the Date object of today at 00.00
     * @return Date
     */
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Return a LocalDate from the given Date
     * @param date Date
     * @return LocalDate
     */
    public static LocalDate getLocalDate(Date date) {
        LocalDate d = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        return d;
    }

    /**
     * Return a LocalDateTime from the given Date
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        LocalDateTime d = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return d;
    }
}
