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
import java.util.List;

import org.junit.jupiter.api.Test;

public class AnnotationsTest {

    @Test
    public void findMethodsAnnotatedByテスト正常系1() {
        Method[] actual = Annotations.findMethodsAnnotatedBy(Annotation1.class, AnnotationsTestBean.class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
        List<String> expected = Lists.gen(
                "method1Annotation1",
                "method3Annotation1",
                "method4Annotation1");
        for (Method m : actual) {
            assertTrue(expected.contains(m.getName()));
        }
    }
    
    @Test
    public void findMethodsAnnotatedByテスト正常系2() {
        Method[] actual = Annotations.findMethodsAnnotatedBy(Annotation2.class, AnnotationsTestBean.class);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals(actual[0].getName(), "method2Annotation2");
    }
    
    @Test
    public void findMethodsAnnotatedByテスト正常系3() {
        Method[] actual = Annotations.findMethodsAnnotatedBy(Annotation3.class, AnnotationsTestBean.class);
        assertNotNull(actual);
        assertEquals(0, actual.length);
    }
    
    @Test
    public void findDeclaredMethodsAnnotatedByテスト正常系1() {
        Method[] actual = Annotations.findDeclaredMethodsAnnotatedBy(
                Annotation1.class, AnnotationsTestBean.class);
        assertNotNull(actual);
        assertEquals(4, actual.length);
        List<String> expected = Lists.gen(
                "method1Annotation1",
                "method3Annotation1",
                "method4Annotation1",
                "method5Annotation1");
        for (Method m : actual) {
            assertTrue(expected.contains(m.getName()));
        }
    }
    
    @Test
    public void findDeclaredMethodsAnnotatedByテスト正常系3() {
        Method[] actual = Annotations.findDeclaredMethodsAnnotatedBy(
                Annotation3.class, AnnotationsTestBean.class);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals(actual[0].getName(), "method6Annotation3");
    }
}
