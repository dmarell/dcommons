/*
 * Copyright (c) 2002,2012 Daniel Marell
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package se.marell.dcommons.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Swedish holiday calendar.
 */
public class SwedishHolidayExplorer implements HolidayExplorer {

    private static class MonthDay {
        public int month;
        public int day;

        public MonthDay(int month, int day) {
            this.month = month;
            this.day = day;
        }
    }

    @Override
    public String getHoliday(LocalDate date) {
        return getHoliday(date, ",");
    }

    @Override
    public String getHoliday(LocalDate date, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String s : getHolidayList(date)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(s);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    @Override
    public List<String> getHolidayList(LocalDate date) {
        List<String> result = new ArrayList<>();
        int year = date.getYear();
        Month month = date.getMonth();
        int day = date.getDayOfMonth();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (month == Month.JANUARY) {
            if (day == 1) {
                result.add("Nyårsdagen");
            } else if (day == 6) {
                result.add("Trettondagen");
            }
        } else if (month == Month.JUNE) {
            if (date.getDayOfMonth() == 6) {
                result.add("Nationaldagen");
            }

            // Midsommardagen är den lördag som infaller under tiden den 20..26 juni
            LocalDate d = LocalDate.of(year, Month.JUNE, 20);
            int msDay = (20 + (DayOfWeek.SATURDAY.getValue() - d.getDayOfWeek().getValue()));
            if (day == msDay) {
                result.add("Midsommardagen");
            } else if (day == msDay - 1) {
                result.add("Midsommarafton");
            }
        } else if (month == Month.DECEMBER) {
            if (day == 24) {
                result.add("Julafton");
            } else if (day == 25) {
                result.add("Juldagen");
            } else if (day == 26) {
                result.add("Annandagen");
            }
        }

        MonthDay easterMd = easterday(year);
        LocalDate easternDay = LocalDate.of(year, easterMd.month, easterMd.day);
        if (month == easternDay.getMonth() && day == easternDay.getDayOfMonth()) {
            result.add("Påskdagen");
        }

        LocalDate easternEvening = easternDay.minusDays(1);
        if (month == easternEvening.getMonth() && day == easternEvening.getDayOfMonth()) {
            result.add("Påskafton");
        }

        LocalDate easternFriday = easternDay.minusDays(2);
        if (month == easternFriday.getMonth() && day == easternFriday.getDayOfMonth()) {
            result.add("Långfredag");
        }

        LocalDate easternMonday = easternDay.plusDays(1);
        if (month == easternMonday.getMonth() && day == easternMonday.getDayOfMonth()) {
            result.add("Annandag påsk");
        }

        {
            LocalDate d = easternDay.plusDays(5 * 7 + 4);
            if (month == d.getMonth() && day == d.getDayOfMonth()) {
                result.add("Kristi himmelsfärdsdag");
            }
        }

        {
            LocalDate d = easternDay.plusDays(7 * 7);
            if (month == d.getMonth() && day == d.getDayOfMonth()) {
                result.add("Pingstdagen");
            }
        }

        // Alla helgona är den lördag som infaller under tiden 31:a oktober..6:e november
        {
            LocalDate ah = LocalDate.of(year, Month.OCTOBER, 31);
            int ahd = (DayOfWeek.SATURDAY.getValue() - ah.getDayOfWeek().getValue());
            if (ahd < 0) {
                ahd += 7;
            }
            if (dayOfWeek == DayOfWeek.SATURDAY && month == Month.OCTOBER && day == 31 ||
                    month == Month.NOVEMBER && day == ahd) {
                result.add("Alla helgons dag");
            }
        }

        return result;
    }

    @Override
    public List<Holiday> getHolidays(int year) {
        List<Holiday> result = new ArrayList<>();
        LocalDate d = LocalDate.of(year, Month.JANUARY, 1);
        do {
            for (String s : getHolidayList(d)) {
                result.add(new Holiday(d, s));
            }
            d = d.plusDays(1);
        } while (d.getYear() == year);
        return result;
    }

    /**
     * Calculate Easter day
     *
     * @param y Year, four digits
     * @return Month (1..11) and day (1..31) in month of Easter this year
     * <p>
     * See http://aa.usno.navy.mil/faq/docs/easter.html
     */
    private static MonthDay easterday(int y) {
        int c = y / 100;
        int n = y - 19 * (y / 19);
        int k = (c - 17) / 25;
        int i = c - c / 4 - (c - k) / 3 + 19 * n + 15;
        i = i - 30 * (i / 30);
        i = i - (i / 28) * (1 - (i / 28) * (29 / (i + 1))
                * ((21 - n) / 11));
        int j = y + y / 4 + i + 2 - c + c / 4;
        j = j - 7 * (j / 7);
        int l = i - j;
        int m = 3 + (l + 40) / 44; // month
        int d = l + 28 - 31 * (m / 4); // day

        return new MonthDay(m, d);
    }
}
