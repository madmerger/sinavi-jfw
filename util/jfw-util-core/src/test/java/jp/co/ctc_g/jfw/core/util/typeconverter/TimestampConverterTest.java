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

package jp.co.ctc_g.jfw.core.util.typeconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class TimestampConverterTest {

    @Test
    public void Stringからのconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        assertEquals(sdf.parse(ymdhmss), new TimestampConverter().convert(ymdhmss));
        sdf.applyPattern("yyyy/MM/dd");
        String ymdhms = "2011/11/11 13:45:56";
        assertEquals(sdf.parse(ymdhms), new TimestampConverter().convert(ymdhms));
        String ymdhm = "2011/11/11 13:45";
        assertEquals(sdf.parse(ymdhm), new TimestampConverter().convert(ymdhm));
        String ymdh = "2011/11/11 13";
        assertEquals(sdf.parse(ymdh), new TimestampConverter().convert(ymdh));
        String ymd = "2011/11/11";
        assertEquals(sdf.parse(ymd), new TimestampConverter().convert(ymd));
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String hymdhmss = "2011-11-11 13:45:56.789";
        assertEquals(sdf.parse(hymdhmss), new TimestampConverter().convert(hymdhmss));
        sdf.applyPattern("yyyy-MM-dd");
        String hymdhms = "2011-11-11 13:45:56";
        assertEquals(sdf.parse(hymdhms), new TimestampConverter().convert(hymdhms));
        String hymdhm = "2011-11-11 13:45";
        assertEquals(sdf.parse(hymdhm), new TimestampConverter().convert(hymdhm));
        String hymdh = "2011-11-11 13";
        assertEquals(sdf.parse(hymdh), new TimestampConverter().convert(hymdh));
        String hymd = "2011-11-11";
        assertEquals(sdf.parse(hymd), new TimestampConverter().convert(hymd));
    }

    @Test
    public void Stringからconvertテスト2() {

        String d = "";
        Date expected = null;
        Date actual = new TimestampConverter().convert(d);
        assertEquals(expected, actual);
    }

    @Test
    public void Timestampからconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        java.sql.Date sd = new java.sql.Date(d.getTime());
        assertEquals(sdf.parse(ymdhmss), new TimestampConverter().convert(sd));
    }

    @Test
    public void Dateからconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        assertEquals(sdf.parse(ymdhmss), new TimestampConverter().convert(d));
    }

}
