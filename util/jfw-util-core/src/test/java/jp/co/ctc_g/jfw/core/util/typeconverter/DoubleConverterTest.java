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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class DoubleConverterTest {

    @Test
    public void Floatからのconvertテスト() {
        Float f = 255.255f;
        Double expected = 255.255;
        Double actual = new DoubleConverter().convert(f);
        assertEquals(expected, actual);
    }
    
    @Test
    public void BigDecimalからのconvertテスト() {
        BigDecimal f = new BigDecimal("255.255");
        Double expected = 255.255;
        Double actual = new DoubleConverter().convert(f);
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void Stringからのconvertテスト1() {
        String s = "255";
        Double expected = new Double("255");
        Double actual = new DoubleConverter().convert(s);
        assertEquals(expected, actual);
    }
    
    @Test
    public void Stringからのconvertテスト2() {
        String s = null;
        Double expected = null;
        Double actual = new DoubleConverter().convert(s);
        assertEquals(expected, actual);
    }
    
    @Test
    public void Stringからのconvertテスト3() {
        String s = "";
        Double expected = null;
        Double actual = new DoubleConverter().convert(s);
        assertEquals(expected, actual);
    }
}
