/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.adapterservices.schema.adapter;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Adapter for converting XML Schema types to Java dates and vice versa.
 */
public final class LocalDateAdapter {

    private static final DateTimeZone TIME_ZONE = DateTimeZone.forID("Europe/Stockholm");
    private static final String ISO_DATE_PATTERN = "YYYY-MM-dd";
    private static final String ISO_DATETIME_PATTERN = "YYYY-MM-dd'T'HH:mm:ss";
    private static final String XSD_DATE_TIMEZONE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}([+-].*|Z)";
    private static final String XSD_DATETIME_TIMEZONE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.?[0-9]*([+-].*|Z)";

    private LocalDateAdapter() {
    }

    /**
     * Converts an xs:date to a Joda Time LocalDate.
     */
    public static LocalDate parseDate(String dateString) {
        // Workaround for the fact that DatatypeConverter doesn't allow setting the default TimeZone,
        // (which means that the system default TimeZone will be used, which might be wrong if hosted in
        // a different TimeZone), and that LocaleDate doesn't handle dates with explicit TimeZone.
        // Hence if the date contains an explicit TimeZone, use DatatypeConverter to do the parsing,
        // otherwise use LocalDate's parsing.
        if (dateString.matches(XSD_DATE_TIMEZONE_REGEXP) || dateString.matches(XSD_DATETIME_TIMEZONE_REGEXP)) {
            return new LocalDate(javax.xml.bind.DatatypeConverter.parseDate(dateString).getTime(), TIME_ZONE);
        } else {
            return new LocalDate(dateString.substring(0, ISO_DATE_PATTERN.length()), TIME_ZONE);
        }
    }

    /**
     * Converts an xs:datetime to a Joda Time LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        // Workaround for the fact that DatatypeConverter doesn't allow setting the default TimeZone,
        // (which means that the system default TimeZone will be used, which might be wrong if hosted in
        // a different TimeZone), and that LocalDateTime doesn't handle datetimes with explicit TimeZone.
        // Hence if the date contains an explicit TimeZone, use DatatypeConverter to do the parsing,
        // otherwise use LocalDateTime's parsing.
        if (dateTimeString.matches(XSD_DATETIME_TIMEZONE_REGEXP) || dateTimeString.matches(XSD_DATE_TIMEZONE_REGEXP)) {
            return new LocalDateTime(javax.xml.bind.DatatypeConverter.parseDateTime(dateTimeString).getTime(), TIME_ZONE);
        } else {
            return new LocalDateTime(dateTimeString, TIME_ZONE);
        }
    }

    /**
     * Print a DateTime (always using ISO format).
     */
    public static String printDateTime(LocalDateTime dateTime) {
        return printIsoDateTime(dateTime);
    }

    /**
     * Print a Date (always using ISO format).
     */
    public static String printDate(LocalDate date) {
        return printIsoDate(date);
    }

    /**
     * Print a DateTime in ISO format.
     */
    public static String printIsoDateTime(LocalDateTime dateTime) {
        return dateTime.toString(ISO_DATETIME_PATTERN);
    }

    /**
     * Print a Date in ISO format.
     */
    public static String printIsoDate(LocalDate date) {
        return date.toString(ISO_DATE_PATTERN);
    }
}
