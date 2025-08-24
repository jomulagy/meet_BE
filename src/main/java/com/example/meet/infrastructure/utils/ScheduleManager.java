package com.example.meet.infrastructure.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ScheduleManager {
    public static int calculateNextQuarterStartMonth(int currentMonth) {
        if (currentMonth >= 1 && currentMonth <= 3) {
            return 4; // 4월
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            return 7; // 7월
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            return 10; // 10월
        } else {
            return 1; // 1월
        }
    }

    public static List<LocalDateTime> getFridaysAndSaturdays(LocalDate startDate, LocalDate endDate) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                dates.add(date.atTime(19, 0));
            }
            date = date.plusDays(1);
        }

        return dates;
    }
}
