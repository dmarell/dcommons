/*
 * Created by Daniel Marell 12-07-04 7:38 AM
 */
package se.marell.dcommons.time;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SwedishHolidayExplorerTest {
    @Test
    public void testGetHolidayAllaHelgonsDag() throws Exception {
        HolidayExplorer holidayExplorer = new SwedishHolidayExplorer();
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2012-11-03")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2013-11-02")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2014-11-01")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2015-10-31")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2016-11-05")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2017-11-04")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2018-11-03")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2019-11-02")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2020-10-31")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2021-11-06")), is("Alla helgons dag"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2055-11-06")), is("Alla helgons dag"));
    }

    @Test
    public void testGetHoliday() throws Exception {
        HolidayExplorer holidayExplorer = new SwedishHolidayExplorer();
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2012-06-06")), is("Nationaldagen"));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2012-05-17")), is("Kristi himmelsfärdsdag"));
        assertNull(holidayExplorer.getHoliday(LocalDate.parse("2012-05-18")));
        assertThat(holidayExplorer.getHoliday(LocalDate.parse("2055-06-06")), is("Nationaldagen,Pingstdagen"));
    }

    @Test
    public void testGetHolidays() throws Exception {
        HolidayExplorer holidayExplorer = new SwedishHolidayExplorer();
        for (int y = 2000; y <= 3000; y += 11) {
            List<Holiday> holidays = holidayExplorer.getHolidays(y);
            assertThat("year=" + y, holidays.size(), is(15));
            for (Holiday h : holidays) {
                assertNotNull(h.getName());
                assertThat(h.getDate().getYear(), is(y));
            }
        }
    }

    //@Test
    public void testLoopHolidays() throws Exception {
        loopYear(2000);
        loopYear(2012);
        loopYear(2055);
        loopYear(2143);
    }

    private void loopYear(int year) {
        HolidayExplorer holidayExplorer = new SwedishHolidayExplorer();
        List<Holiday> holidays = holidayExplorer.getHolidays(year);
        for (Holiday h : holidays) {
            System.out.println(h);
        }
        System.out.println();
    }
}
