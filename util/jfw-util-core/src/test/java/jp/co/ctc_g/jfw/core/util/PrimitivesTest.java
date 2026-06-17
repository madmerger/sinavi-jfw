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

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class PrimitivesTest {

    @Test
    public void charからCharacter変換() {

        char[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new char[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = new char[] {
            'a'
        };
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Character.class));
        assertThat(Primitives.wrap(suspect)[0].charValue(), is('a'));
    }

    @Test
    public void intからInteger変換() {

        int[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new int[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = new int[] {
            1
        };
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Integer.class));
        assertThat(Primitives.wrap(suspect)[0].intValue(), is(1));
    }

    @Test
    public void longからLong変換() {

        long[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new long[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = new long[] {
            10000000000L
        };
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Long.class));
        assertThat(Primitives.wrap(suspect)[0].longValue(), is(10000000000L));
    }

    @Test
    public void floatからFloat変換() {

        float[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new float[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = new float[] {
            255.255f
        };
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Float.class));
        assertThat(Primitives.wrap(suspect)[0].floatValue(), is(255.255f));
    }

    @Test
    public void doubleからDouble変換() {

        double[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new double[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = new double[] {
            255.255d
        };
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Double.class));
        assertThat(Primitives.wrap(suspect)[0].doubleValue(), is(255.255d));
    }

    @Test
    public void byteからByte変換() {

        byte[] suspect = null;
        assertThat(Primitives.wrap(suspect), nullValue());
        suspect = new byte[] {};
        assertThat(Primitives.wrap(suspect).length, is(0));
        suspect = "a".getBytes();
        assertThat(Primitives.wrap(suspect).length, is(1));
        assertThat(Primitives.wrap(suspect)[0], isA(Byte.class));
    }

}
