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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;

public class ReflectsTest {

    @Test
    public void findMethodSignedテスト正常系1() {
        Method equals = Reflects.findMethodSigned("equals", getClass(), Object.class);
        assertNotNull(equals);
        assertEquals("equals", equals.getName());
        assertEquals(1, equals.getParameterTypes().length);
        assertEquals(Object.class, equals.getParameterTypes()[0]);
    }
    
    @Test
    public void findMethodSignedテスト異常系1() {
        Method equals = Reflects.findMethodSigned("equals", getClass());
        assertNull(equals);
    }
    
    @Test
    public void findMethodNamedテスト正常系2() {
        Method equals = Reflects.findMethodNamed("equals", getClass());
        assertNotNull(equals);
        assertEquals("equals", equals.getName());
        assertEquals(1, equals.getParameterTypes().length);
        assertEquals(Object.class, equals.getParameterTypes()[0]);
    }
    
    @Test
    public void findMethodNamedテスト異常系2() {
        Method equals = Reflects.findMethodNamed("equal", getClass());
        assertNull(equals);
    }
    
    public static class ReflectsTestObject {
        public ReflectsTestObject() {
        }
        public ReflectsTestObject(String arg) {
        }
    }

    @Test
    public void make() {
        ReflectsTestObject obj = Reflects.make(ReflectsTestObject.class);
        ReflectsTestObject obj2 = Reflects.make(ReflectsTestObject.class, "");
        assertThat(obj, is(instanceOf(ReflectsTestObject.class)));
        assertThat(obj2, is(instanceOf(ReflectsTestObject.class)));
    }
    
    @Test
    public void make異常系() {
        assertThrows(InternalException.class, () -> {
            try{
                Reflects.make(ReflectsTestObject.class, "", "");
            }catch (InternalException e) {
                assertThat(e.getCode(), is("E-UTIL#0026"));
                throw e;
            }
        });
    }
}