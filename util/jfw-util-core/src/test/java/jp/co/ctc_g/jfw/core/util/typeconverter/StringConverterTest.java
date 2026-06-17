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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class StringConverterTest {

    @Test
    public void Integerからのconvertテスト() {

        assertThat(new StringConverter().convert(Integer.valueOf(10)), is("10"));
    }

    @Test
    public void Longからのconvertテスト() {

        assertThat(new StringConverter().convert(Long.valueOf(10)), is("10"));
    }

    @Test
    public void BigIntegerからのconvertテスト() {

        assertThat(new StringConverter().convert(BigInteger.valueOf(10)), is("10"));
    }

    @Test
    public void Floatからのconvertテスト() {

        assertThat(new StringConverter().convert(Float.valueOf(10.9F)), is("10.9"));
    }

    @Test
    public void Doubleからのconvertテスト() {

        assertThat(new StringConverter().convert(Double.valueOf(10.9D)), is("10.9"));
    }

    @Test
    public void BigDecimalからのconvertテスト() {

        assertThat(new StringConverter().convert(BigDecimal.valueOf(10.9D)), is("10.9"));
    }

    @Test
    public void Dateからのconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        assertEquals("2011/11/11", new StringConverter().convert(sdf.parse(ymdhmss)));
    }

    @Test
    public void SqlDateからのconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        java.sql.Date sd = new java.sql.Date(d.getTime());
        assertEquals("2011/11/11", new StringConverter().convert(sd));
    }

    @Test
    public void Timestampからのconvertテスト() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
        String ymdhmss = "2011/11/11 13:45:56.789";
        Date d = sdf.parse(ymdhmss);
        Timestamp ts = new Timestamp(d.getTime());
        assertEquals("2011/11/11 13:45:56", new StringConverter().convert(ts));
    }

}
