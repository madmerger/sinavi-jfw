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
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class IntegerConverterTest {

    @Test
    public void Longからのconvertテスト() {
        Long v = 255L;
        Integer expected = 255;
        Integer actual = new IntegerConverter().convert(v);
        assertEquals(expected, actual);
    }
    
    @Test
    public void BigIntegerからのconvertテスト() {
        BigInteger f = new BigInteger("255");
        Integer expected = 255;
        Integer actual = new IntegerConverter().convert(f);
        assertEquals(expected, actual);
    }
    
    @Test
    public void BigDecimalからのconvertテスト() {
        BigDecimal f = new BigDecimal("255");
        Integer expected = 255;
        Integer actual = new IntegerConverter().convert(f);
        assertEquals(expected, actual);
    }
    
    @Test
    public void Stringからのconvertテスト1() {
        String s = "255";
        Integer expected = new Integer("255");
        Integer actual = new IntegerConverter().convert(s);
        assertEquals(expected, actual);
    }
    
    @Test
    public void Stringからのconvertテスト2() {
        String s = null;
        Integer expected = null;
        Integer actual = new IntegerConverter().convert(s);
        assertEquals(expected, actual);
    }
    
    @Test
    public void Stringからのconvertテスト3() {
        String s = "";
        Integer expected = null;
        Integer actual = new IntegerConverter().convert(s);
        assertEquals(expected, actual);
    }
}
