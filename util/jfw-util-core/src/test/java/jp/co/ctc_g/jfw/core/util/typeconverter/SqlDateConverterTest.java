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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class SqlDateConverterTest {

    @Test
    public void Stringからのconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        assertEquals(sdf.parse(ymdhmss), new SqlDateConverter().convert(ymdhmss));
        sdf.applyPattern("yyyy/MM/dd");
        String ymdhms = "2011/11/11 13:45:56";
        assertEquals(sdf.parse(ymdhms), new SqlDateConverter().convert(ymdhms));
        String ymdhm = "2011/11/11 13:45";
        assertEquals(sdf.parse(ymdhm), new SqlDateConverter().convert(ymdhm));
        String ymdh = "2011/11/11 13";
        assertEquals(sdf.parse(ymdh), new SqlDateConverter().convert(ymdh));
        String ymd = "2011/11/11";
        assertEquals(sdf.parse(ymd), new SqlDateConverter().convert(ymd));
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String hymdhmss = "2011-11-11 13:45:56.789";
        assertEquals(sdf.parse(hymdhmss), new SqlDateConverter().convert(hymdhmss));
        sdf.applyPattern("yyyy-MM-dd");
        String hymdhms = "2011-11-11 13:45:56";
        assertEquals(sdf.parse(hymdhms), new SqlDateConverter().convert(hymdhms));
        String hymdhm = "2011-11-11 13:45";
        assertEquals(sdf.parse(hymdhm), new SqlDateConverter().convert(hymdhm));
        String hymdh = "2011-11-11 13";
        assertEquals(sdf.parse(hymdh), new SqlDateConverter().convert(hymdh));
        String hymd = "2011-11-11";
        assertEquals(sdf.parse(hymd), new SqlDateConverter().convert(hymd));
    }

    @Test
    public void Stringからconvertテスト2() {

        String d = "";
        Date expected = null;
        Date actual = new SqlDateConverter().convert(d);
        assertEquals(expected, actual);
    }

    @Test
    public void Timestampからconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        Timestamp ts = new Timestamp(d.getTime());
        assertEquals(sdf.parse(ymdhmss), new SqlDateConverter().convert(ts));
    }

    @Test
    public void Dateからconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        assertEquals(sdf.parse(ymdhmss), new SqlDateConverter().convert(d));
    }

}
