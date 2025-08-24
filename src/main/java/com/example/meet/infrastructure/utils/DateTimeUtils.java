package com.example.meet.infrastructure.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    public static final ZoneId SERVER_ZONE = ZoneId.of("Asia/Seoul");

    // "2025-08-24T11:50:00+09:00"
    public static final DateTimeFormatter ISO_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ISO_LOCAL_DATE;

    /** LocalDateTime -> 오프셋 포함 문자열 */
    public static String formatWithOffset(LocalDateTime ldt) {
        return ldt.atZone(SERVER_ZONE)           // ZonedDateTime로 올림(오프셋 생김)
                .toOffsetDateTime()            // OffsetDateTime으로 변환(선택)
                .format(ISO_WITH_OFFSET);
    }
}
