package report.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateUtil {

    public static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FORMAT_DD_MM_YYYY_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT_DD_MM_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String format(LocalDate date) {
        return date.format(DATE_FORMAT_DD_MM_YYYY);
    }

    public static String format(LocalDateTime datetime) {
        return datetime.format(DATE_TIME_FORMAT_DD_MM_YYYY_HH_MM_SS);
    }

    public static String format(String datetime) {
        return format(Instant.parse(datetime).atZone(ZONE_BR).toLocalDateTime());
    }

    public static String dashFormat(LocalDate date) {
        return date.format(DATE_FORMAT_DD_MM_YYYY_DASH);
    }
}
