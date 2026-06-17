/*
 * Copyright (c) 2013 ITOCHU Techno-Solutions Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.ctc_g.jfw.core.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class DatesTest {

    @Test
    public void makeFrom日付生成テスト正常系1() {

        // year, monthの場合
        for (int year = 1970; year < 2100; year++) {
            for (int month = 1; month <= 12; month++) {
                Date actual = Dates.makeFrom(year, month);
                String ay = Formats.simpleDateFormat(actual, "yyyy");
                String am = Formats.simpleDateFormat(actual, "MM");
                assertEquals(year, Integer.valueOf(ay).intValue());
                assertEquals(month, Integer.valueOf(am).intValue());
            }
        }
    }

    @Test
    public void makeFrom日付生成テスト正常系2() {

        // year, month, dayの場合
        for (int year = 1970; year < 2100; year++) {
            for (int month = 1; month <= 12; month++) {
                for (int day = 1; day <= 28; day++) {
                    Date actual = Dates.makeFrom(year, month, day);
                    String ay = Formats.simpleDateFormat(actual, "yyyy");
                    String am = Formats.simpleDateFormat(actual, "MM");
                    String ad = Formats.simpleDateFormat(actual, "dd");
                    assertEquals(year, Integer.valueOf(ay).intValue());
                    assertEquals(month, Integer.valueOf(am).intValue());
                    assertEquals(day, Integer.valueOf(ad).intValue());
                }
            }
        }
    }

    @Test
    public void 正常に次の年を取得できるかどうか() throws Exception {

        assertNull(Dates.nextYear(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(sdf.format(Dates.nextYear(sdf.parse("2011/11"))), is("2012/11"));
    }

    @Test
    public void 正常に前の年を取得できるかどうか() throws Exception {

        assertNull(Dates.previousYear(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(sdf.format(Dates.previousYear(sdf.parse("2011/11"))), is("2010/11"));
    }

    @Test
    public void 正常に年を取得できるかどうか() throws Exception {

        assertThat(Dates.getYear(null), is(-1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(Dates.getYear(sdf.parse("2011/11")), is(2011));
    }

    @Test
    public void 正常に次の月を取得できるかどうか() throws Exception {

        assertNull(Dates.nextMonth(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(sdf.format(Dates.nextMonth(sdf.parse("2011/12"))), is("2012/01"));
    }

    @Test
    public void 正常に前の月を取得できるかどうか() throws Exception {

        assertNull(Dates.previousMonth(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(sdf.format(Dates.previousMonth(sdf.parse("2012/01"))), is("2011/12"));
    }

    @Test
    public void 正常に月を取得できるかどうか() throws Exception {

        assertThat(Dates.getMonth(null), is(-1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(Dates.getMonth(sdf.parse("2012/01")), is(1));
    }

    @Test
    public void 正常に次の日を取得できるかどうか() throws Exception {

        assertNull(Dates.nextDay(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(sdf.format(Dates.nextDay(sdf.parse("2012/02/29"))), is("2012/03/01"));
    }

    @Test
    public void 正常に前の日を取得できるかどうか() throws Exception {

        assertNull(Dates.previousDay(null));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(sdf.format(Dates.previousDay(sdf.parse("2012/03/01"))), is("2012/02/29"));
    }

    @Test
    public void 正常に日を取得できるかどうか() throws Exception {

        assertThat(Dates.getDay(null), is(-1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(Dates.getDay(sdf.parse("2012/03/01")), is(1));
    }

    @Test
    public void 正常に曜日を取得できるかどうか() throws Exception {

        assertThat(Dates.getDayOfWeek(null), is(-1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/06")), is(1));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/07")), is(2));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/08")), is(3));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/09")), is(4));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/10")), is(5));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/11")), is(6));
        assertThat(Dates.getDayOfWeek(sdf.parse("2011/11/12")), is(7));

        assertThat(Dates.getDayOfWeek(sdf.parse("2012/02/26")), is(1));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/02/27")), is(2));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/02/28")), is(3));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/02/29")), is(4));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/03/01")), is(5));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/03/02")), is(6));
        assertThat(Dates.getDayOfWeek(sdf.parse("2012/03/03")), is(7));
    }

    @Test
    public void 正常に最後の日を取得できるかどうか() throws Exception {

        assertThat(Dates.lastDay(null), is(-1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        assertThat(Dates.lastDay(sdf.parse("2011/11")), is(30));
        assertThat(Dates.lastDay(sdf.parse("2011/12")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/01")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/02")), is(29));
        assertThat(Dates.lastDay(sdf.parse("2012/03")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/04")), is(30));
        assertThat(Dates.lastDay(sdf.parse("2012/05")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/06")), is(30));
        assertThat(Dates.lastDay(sdf.parse("2012/07")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/08")), is(31));
        assertThat(Dates.lastDay(sdf.parse("2012/09")), is(30));
        assertThat(Dates.lastDay(sdf.parse("2012/10")), is(31));
    }

    @Test
    public void うるう年かどうか() throws Exception {

        assertTrue(Dates.isLeapYear(2012));
        assertFalse(Dates.isLeapYear(2011));
    }

    @Test
    public void 日付が月に対して有効か() throws Exception {

        assertTrue(Dates.isDate(2011, 11, 11));
        assertFalse(Dates.isDate(2011, 2, 29));
    }

    @Test
    public void 時間が有効か() throws Exception {

        assertTrue(Dates.isTime(23, 59, 59));
        assertFalse(Dates.isTime(25, 61, 61));
    }

    @Test
    public void 現在の年を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String expect = sdf.format(new Date(System.currentTimeMillis()));
        assertThat(Dates.getYear(), is(Integer.parseInt(expect)));
    }

    @Test
    public void 現在の月を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String expect = sdf.format(new Date(System.currentTimeMillis()));
        assertThat(Dates.getMonth(), is(Integer.parseInt(expect)));
    }

    @Test
    public void 現在の日を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String expect = sdf.format(new Date(System.currentTimeMillis()));
        assertThat(Dates.getDay(), is(Integer.parseInt(expect)));
    }

    @Test
    public void 現在の時を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String expect = sdf.format(new Date(System.currentTimeMillis()));
        assertThat(Dates.getHour(), is(Integer.parseInt(expect)));
    }

    @Test
    public void 現在の分を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        String expect = sdf.format(new Date(System.currentTimeMillis()));
        assertThat(Dates.getMinutes(), is(Integer.parseInt(expect)));
    }

    @Test
    public void TimestampからDateが生成されるかどうか() throws Exception {

        long time = System.currentTimeMillis();
        Timestamp ts = new Timestamp(time);
        assertThat(Dates.getDate(ts), is(instanceOf(java.util.Date.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        assertThat(sdf.format(Dates.getDate(ts)), is(sdf.format(new Date(time))));
    }

    @Test
    public void 現在の日付からDateが生成されるかどうか() throws Exception {

        assertThat(Dates.getDate(), is(instanceOf(java.util.Date.class)));
    }

    @Test
    public void 指定した日付からDateが生成されるかどうか() throws Exception {

        assertThat(Dates.getDate(2011, 11, 11), is(instanceOf(java.util.Date.class)));
        assertThat(Dates.getDate(2011, 11, 11, 11, 11, 11), is(instanceOf(java.util.Date.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(sdf.format(Dates.getDate(2011, 11, 11)), is("2011/11/11"));
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss");
        assertThat(sdf.format(Dates.getDate(2011, 11, 11, 11, 11, 11)), is("2011/11/11 11:11:11"));
    }

    @Test
    public void DateからSqlDateが生成されるかどうか() throws Exception {

        long time = System.currentTimeMillis();
        Date d = new Date(time);
        assertThat(Dates.getSqlDate(d), is(instanceOf(java.sql.Date.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        assertThat(sdf.format(Dates.getSqlDate(d)), is(sdf.format(d)));
    }

    @Test
    public void 現在の日付からSqlDateが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlDate(), is(instanceOf(java.sql.Date.class)));
    }

    @Test
    public void 現在の日付からSqlTimeが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlTime(), is(instanceOf(java.sql.Time.class)));
    }

    @Test
    public void DateからTimestampが生成されるかどうか() throws Exception {

        long time = System.currentTimeMillis();
        Date d = new Date(time);
        assertThat(Dates.getSqlTimestamp(d), is(instanceOf(java.sql.Timestamp.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        assertThat(sdf.format(Dates.getSqlTimestamp(d)), is(sdf.format(d)));
    }

    @Test
    public void 現在の日付からTimestampが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlTimestamp(), is(instanceOf(java.sql.Timestamp.class)));
    }

    @Test
    public void 指定した日付からSqlDateが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlDate(2011, 11, 11), is(instanceOf(java.sql.Date.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        assertThat(sdf.format(Dates.getSqlDate(2011, 11, 11)), is("2011/11/11"));
    }

    @Test
    public void 指定した時間からTimeが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlTime(11, 11, 11), is(instanceOf(java.sql.Time.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        assertThat(sdf.format(Dates.getSqlTime(11, 11, 11)), is("11:11:11"));
    }

    @Test
    public void 指定した日付と時間からTimestampが生成されるかどうか() throws Exception {

        assertThat(Dates.getSqlTimestamp(2011, 11, 11, 11, 11, 11), is(instanceOf(java.sql.Timestamp.class)));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        assertThat(sdf.format(Dates.getSqlTimestamp(2011, 11, 11, 11, 11, 11)), is("2011/11/11 11:11:11.000"));
    }

    @Test
    public void 指定した日付からlongで表される時間が取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("2011/11/11");
        assertThat(Dates.getTime(2011, 11, 11), is(d.getTime()));
    }

    @Test
    public void 指定した日付と時間からlongで表される時間が取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = sdf.parse("2011/11/11 11:11:11");
        assertThat(Dates.getTime(2011, 11, 11, 11, 11, 11), is(d.getTime()));
    }

    @Test
    public void 指定した値から月を取得できるかどうか() throws Exception {

        assertThat(Dates.toRealMonth(0), is(1));
        assertThat(Dates.toRealMonth(1), is(2));
        assertThat(Dates.toRealMonth(2), is(3));
        assertThat(Dates.toRealMonth(3), is(4));
        assertThat(Dates.toRealMonth(4), is(5));
        assertThat(Dates.toRealMonth(5), is(6));
        assertThat(Dates.toRealMonth(6), is(7));
        assertThat(Dates.toRealMonth(7), is(8));
        assertThat(Dates.toRealMonth(8), is(9));
        assertThat(Dates.toRealMonth(9), is(10));
        assertThat(Dates.toRealMonth(10), is(11));
        assertThat(Dates.toRealMonth(11), is(12));
    }

    @Test
    public void 存在しない月を指定した場合に例外が発生するかどうか() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
    
            Dates.toRealMonth(12);
        });
    }

    @Test
    public void Calendarクラスの月へ変換されるかどうか() throws Exception {

        assertThat(Dates.toCalendarMonth(1), is(0));
        assertThat(Dates.toCalendarMonth(2), is(1));
        assertThat(Dates.toCalendarMonth(3), is(2));
        assertThat(Dates.toCalendarMonth(4), is(3));
        assertThat(Dates.toCalendarMonth(5), is(4));
        assertThat(Dates.toCalendarMonth(6), is(5));
        assertThat(Dates.toCalendarMonth(7), is(6));
        assertThat(Dates.toCalendarMonth(8), is(7));
        assertThat(Dates.toCalendarMonth(9), is(8));
        assertThat(Dates.toCalendarMonth(10), is(9));
        assertThat(Dates.toCalendarMonth(11), is(10));
        assertThat(Dates.toCalendarMonth(12), is(11));
    }

    @Test
    public void 日付の差分が取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date d1 = sdf.parse("2011/11/11 11:11:11.000");
        Date d2 = sdf.parse("2011/11/12 11:11:11.000");
        assertThat(Dates.difference(d1, d2), is(Long.valueOf(86400000)));
        assertThat(Dates.difference(d1.getTime(), d2.getTime()), is(Long.valueOf(86400000)));
    }

    @Test
    public void 指定した日付に指定の年数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("2011/11/11");
        assertThat(Dates.addYear(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addYear(d, 1)), is("2012/11/11"));
        assertThat(sdf.format(Dates.addYear(d, -1)), is("2010/11/11"));
    }

    @Test
    public void 指定した日付に指定の月数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("2011/12/11");
        assertThat(Dates.addMonth(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addMonth(d, 1)), is("2012/01/11"));
        assertThat(sdf.format(Dates.addMonth(d, -13)), is("2010/11/11"));
    }

    @Test
    public void 指定した日付に指定の日数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("2012/02/26");
        assertThat(Dates.addDay(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addDay(d, 1)), is("2012/02/27"));
        assertThat(sdf.format(Dates.addDay(d, 3)), is("2012/02/29"));
        assertThat(sdf.format(Dates.addDay(d, 4)), is("2012/03/01"));
        assertThat(sdf.format(Dates.addDay(d, -1)), is("2012/02/25"));
        assertThat(sdf.format(Dates.addDay(d, -31)), is("2012/01/26"));
        d = sdf.parse("2012/03/01");
        assertThat(sdf.format(Dates.addDay(d, -1)), is("2012/02/29"));
        d = sdf.parse("2012/01/01");
        assertThat(sdf.format(Dates.addDay(d, -1)), is("2011/12/31"));
        d = sdf.parse("2011/12/31");
        assertThat(sdf.format(Dates.addDay(d, 1)), is("2012/01/01"));
    }

    @Test
    public void 指定した日付に指定の時間を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = sdf.parse("2012/03/01 12:00:00");
        assertThat(Dates.addHour(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addHour(d, 1)), is("2012/03/01 13:00:00"));
        assertThat(sdf.format(Dates.addHour(d, 12)), is("2012/03/02 00:00:00"));
        assertThat(sdf.format(Dates.addHour(d, 13)), is("2012/03/02 01:00:00"));
        assertThat(sdf.format(Dates.addHour(d, 24)), is("2012/03/02 12:00:00"));
        assertThat(sdf.format(Dates.addHour(d, 25)), is("2012/03/02 13:00:00"));
        assertThat(sdf.format(Dates.addHour(d, -1)), is("2012/03/01 11:00:00"));
        assertThat(sdf.format(Dates.addHour(d, -12)), is("2012/03/01 00:00:00"));
        assertThat(sdf.format(Dates.addHour(d, -13)), is("2012/02/29 23:00:00"));
        assertThat(sdf.format(Dates.addHour(d, -24)), is("2012/02/29 12:00:00"));
        assertThat(sdf.format(Dates.addHour(d, -25)), is("2012/02/29 11:00:00"));
        d = sdf.parse("2012/01/01 00:00:00");
        assertThat(sdf.format(Dates.addHour(d, -1)), is("2011/12/31 23:00:00"));
        d = sdf.parse("2011/12/31 23:00:00");
        assertThat(sdf.format(Dates.addHour(d, 1)), is("2012/01/01 00:00:00"));
    }

    @Test
    public void 指定した日付に指定の分数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = sdf.parse("2012/03/01 12:00:00");
        assertThat(Dates.addMinute(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addMinute(d, 1)), is("2012/03/01 12:01:00"));
        assertThat(sdf.format(Dates.addMinute(d, 59)), is("2012/03/01 12:59:00"));
        assertThat(sdf.format(Dates.addMinute(d, 60)), is("2012/03/01 13:00:00"));
        assertThat(sdf.format(Dates.addMinute(d, 61)), is("2012/03/01 13:01:00"));
        d = sdf.parse("2012/03/01 00:00:00");
        assertThat(sdf.format(Dates.addMinute(d, -1)), is("2012/02/29 23:59:00"));
        assertThat(sdf.format(Dates.addMinute(d, -59)), is("2012/02/29 23:01:00"));
        assertThat(sdf.format(Dates.addMinute(d, -60)), is("2012/02/29 23:00:00"));
        assertThat(sdf.format(Dates.addMinute(d, -61)), is("2012/02/29 22:59:00"));
        d = sdf.parse("2012/01/01 00:00:00");
        assertThat(sdf.format(Dates.addMinute(d, -1)), is("2011/12/31 23:59:00"));
        d = sdf.parse("2011/12/31 23:59:00");
        assertThat(sdf.format(Dates.addMinute(d, 1)), is("2012/01/01 00:00:00"));
    }

    @Test
    public void 指定した日付に指定の秒数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = sdf.parse("2012/03/01 12:00:00");
        assertThat(Dates.addSecond(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addSecond(d, 1)), is("2012/03/01 12:00:01"));
        assertThat(sdf.format(Dates.addSecond(d, 59)), is("2012/03/01 12:00:59"));
        assertThat(sdf.format(Dates.addSecond(d, 60)), is("2012/03/01 12:01:00"));
        assertThat(sdf.format(Dates.addSecond(d, 61)), is("2012/03/01 12:01:01"));
        d = sdf.parse("2012/03/01 00:00:00");
        assertThat(sdf.format(Dates.addSecond(d, -1)), is("2012/02/29 23:59:59"));
        assertThat(sdf.format(Dates.addSecond(d, -59)), is("2012/02/29 23:59:01"));
        assertThat(sdf.format(Dates.addSecond(d, -60)), is("2012/02/29 23:59:00"));
        assertThat(sdf.format(Dates.addSecond(d, -61)), is("2012/02/29 23:58:59"));
        d = sdf.parse("2012/01/01 00:00:00");
        assertThat(sdf.format(Dates.addSecond(d, -1)), is("2011/12/31 23:59:59"));
        d = sdf.parse("2011/12/31 23:59:59");
        assertThat(sdf.format(Dates.addSecond(d, 1)), is("2012/01/01 00:00:00"));
    }

    @Test
    public void 指定した日付に指定のミリ秒数を加算したDateを取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date d = sdf.parse("2012/03/01 12:00:00.000");
        assertThat(Dates.addMillisecond(d, 0), is(sameInstance(d)));
        assertThat(sdf.format(Dates.addMillisecond(d, 1)), is("2012/03/01 12:00:00.001"));
        assertThat(sdf.format(Dates.addMillisecond(d, 999)), is("2012/03/01 12:00:00.999"));
        assertThat(sdf.format(Dates.addMillisecond(d, 1000)), is("2012/03/01 12:00:01.000"));
        assertThat(sdf.format(Dates.addMillisecond(d, 1001)), is("2012/03/01 12:00:01.001"));
        d = sdf.parse("2012/03/01 00:00:00.000");
        assertThat(sdf.format(Dates.addMillisecond(d, -1)), is("2012/02/29 23:59:59.999"));
        assertThat(sdf.format(Dates.addMillisecond(d, -999)), is("2012/02/29 23:59:59.001"));
        assertThat(sdf.format(Dates.addMillisecond(d, -1000)), is("2012/02/29 23:59:59.000"));
        assertThat(sdf.format(Dates.addMillisecond(d, -1001)), is("2012/02/29 23:59:58.999"));
        d = sdf.parse("2012/01/01 00:00:00.000");
        assertThat(sdf.format(Dates.addMillisecond(d, -1)), is("2011/12/31 23:59:59.999"));
        d = sdf.parse("2011/12/31 23:59:59.999");
        assertThat(sdf.format(Dates.addMillisecond(d, 1)), is("2012/01/01 00:00:00.000"));
    }

    @Test
    public void 指定された日時以降の情報を削除できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date d = sdf.parse("2012/03/31 12:59:59.999");
        assertThat(sdf.format(Dates.truncate(d, Dates.YEAR)), is("2012/01/01 00:00:00.000"));
        assertThat(sdf.format(Dates.truncate(d, Dates.MONTH)), is("2012/03/01 00:00:00.000"));
        assertThat(sdf.format(Dates.truncate(d, Dates.DAY)), is("2012/03/31 00:00:00.000"));
        assertThat(sdf.format(Dates.truncate(d, Dates.HOUR)), is("2012/03/31 12:00:00.000"));
        assertThat(sdf.format(Dates.truncate(d, Dates.MINUTE)), is("2012/03/31 12:59:00.000"));
        assertThat(sdf.format(Dates.truncate(d, Dates.SECOND)), is("2012/03/31 12:59:59.000"));
        // Dateの場合は落ちない
        assertThat(sdf.format(Dates.truncate(d, Dates.MSEC)), is("2012/03/31 12:59:59.999"));
    }

    @Test
    public void 指定した日付の月末を取得できるかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("2012/01/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/01/31"));
        d = sdf.parse("2012/02/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/02/29"));
        d = sdf.parse("2012/03/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/03/31"));
        d = sdf.parse("2012/04/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/04/30"));
        d = sdf.parse("2012/05/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/05/31"));
        d = sdf.parse("2012/06/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/06/30"));
        d = sdf.parse("2012/07/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/07/31"));
        d = sdf.parse("2012/08/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/08/31"));
        d = sdf.parse("2012/09/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/09/30"));
        d = sdf.parse("2012/10/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/10/31"));
        d = sdf.parse("2012/11/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/11/30"));
        d = sdf.parse("2012/12/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/12/31"));
        d = sdf.parse("2011/02/12");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2011/02/28"));
        d = sdf.parse("2012/02/29");
        assertThat(sdf.format(Dates.endOfMonth(d)), is("2012/02/29"));
    }

    @Test
    public void 指定した範囲が重なっているかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date fd1 = sdf.parse("2012/02/01");
        Date fd2 = sdf.parse("2012/02/29");
        Date td1 = sdf.parse("2012/02/26");
        Date td2 = sdf.parse("2012/03/03");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/01");
        td2 = sdf.parse("2012/02/29");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/01/31");
        td2 = sdf.parse("2012/02/01");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/01");
        td2 = sdf.parse("2012/02/02");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/29");
        td2 = sdf.parse("2012/02/29");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/29");
        td2 = sdf.parse("2012/03/01");
        assertTrue(Dates.isDateOverlap(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/03/01");
        td2 = sdf.parse("2012/03/03");
        assertFalse(Dates.isDateOverlap(fd1, fd2, td1, td2));
    }

    @Test
    public void 指定した範囲が完全に含まれているかどうか() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date fd1 = sdf.parse("2012/02/01");
        Date fd2 = sdf.parse("2012/02/29");
        Date td1 = sdf.parse("2012/02/26");
        Date td2 = sdf.parse("2012/03/03");
        assertFalse(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/01");
        td2 = sdf.parse("2012/02/29");
        assertTrue(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/01");
        td2 = sdf.parse("2012/02/02");
        assertTrue(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/29");
        td2 = sdf.parse("2012/02/29");
        assertTrue(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/01/31");
        td2 = sdf.parse("2012/02/01");
        assertFalse(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/02/29");
        td2 = sdf.parse("2012/03/01");
        assertFalse(Dates.isDateInclude(fd1, fd2, td1, td2));
        td1 = sdf.parse("2012/03/01");
        td2 = sdf.parse("2012/03/03");
        assertFalse(Dates.isDateInclude(fd1, fd2, td1, td2));
    }

    @Test
    public void 日付が月に対して有効かどうか() throws Exception {

        assertTrue(Dates.enableDayOfMonth(2011, 11, 11));
        assertTrue(Dates.enableDayOfMonth(2011, 11, 30));
        assertTrue(Dates.enableDayOfMonth(2011, 2, 28));
        assertTrue(Dates.enableDayOfMonth(2012, 2, 29));
        assertFalse(Dates.enableDayOfMonth(2011, 2, 29));
    }

}
