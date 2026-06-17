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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArraysTest {
    
    @Test
    public void eachテスト() {
        final String[] elements = {"a", "b", "c", "d", "e"};
        final int[] datum = {0, elements.length};
        Arrays.each(elements, new EachCall<String>() {
            public void each(String element, int index, int total) {
                assertEquals(elements[index], element);
                assertEquals(datum[0]++, index);
                assertEquals(datum[1], total);
            }
        });
    }
    
    @Test
    public void collectテスト() {
        final String[] elements = {"a", "b", "c", "d", "e"};
        final int[] datum = {0, elements.length};
        String[] results = Arrays.collect(elements, new CollectCall<String>() {
            public String collect(String element, int index, int total) {
                assertEquals(elements[index], element);
                assertEquals(datum[0]++, index);
                assertEquals(datum[1], total);
                return index % 2 == 0 ? element : null;
            }
        });
        assertArrayEquals(new String[] {"a", "c", "e"}, results);
        assertArrayEquals(new String[] {"a", "b", "c", "d", "e"}, elements);
    }
    
    @Test
    public void groupテスト() {
        class Groupee {
            String g; String n;
            public Groupee(String g, String n) {this.g = g;this.n = n;}
            public String getGroup() {return g;}
            public String getName() {return n;}
            @Override
            public boolean equals(Object o) {
                if (o == null || !(o instanceof Groupee)) return false;
                Groupee t = (Groupee) o;
                // null値は認めない方向で
                return this.g.equals(t.getGroup()) && this.n.equals(t.getName());
            }
        }
        final String[] groups = {"A", "A", "B", "B", "B", "C"};
        final String[] names = {"a", "b", "c", "d", "e", "f"};
        final Groupee[] elements = new Groupee[groups.length];
        for (int i = 0; i < groups.length; i++) {
            elements[i] = new Groupee(groups[i], names[i]);
        }
        Map<String, Groupee[]> results = Arrays.group(elements, new GroupCall<Groupee>() {
            public String group(Groupee group, int index, int total) {
                return group.getGroup();
            }
        });
        Groupee[] groupA = results.get("A");
        Groupee[] groupB = results.get("B");
        Groupee[] groupC = results.get("C");
        assertArrayEquals(Arrays.gen(elements[0], elements[1]), groupA);
        assertEquals(2, groupA.length);
        assertArrayEquals(Arrays.gen(elements[2], elements[3], elements[4]), groupB);
        assertEquals(3, groupB.length);
        assertArrayEquals(Arrays.gen(elements[5]), groupC);
        assertEquals(1, groupC.length);
    }
    
    @Test
    public void 単純genテスト() {
        String[] elements = {"a", "b", "c", "d", "e"};
        String[] results = Arrays.gen("a", "b", "c", "d", "e");
        assertArrayEquals(elements, results);
    }
    
    @Test
    public void コールバックgenテスト() {
        final String[] elements = {"a", "b", "c", "d", "e"};
        String[] results = Arrays.gen(5, new GenCall<String>() {
            public String gen(int index, int total) {
                return elements[index];
            }
        });
        assertArrayEquals(elements, results);
    }
    
    @Test
    public void sliceテスト1() {
        String[] actual = Arrays.slice(Arrays.gen("a", "b", "c"), 1);
        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals("b", actual[0]);
        assertEquals("c", actual[1]);
    }
    
    @Test
    public void sliceテスト2() {
        String[] actual = Arrays.slice(new String[0], 0);
        assertNotNull(actual);
        assertEquals(0, actual.length);
    }
    
    @Test
    public void sliceテスト3() {
        String[] actual = Arrays.slice(Arrays.gen("a", "b", "c"), 1, 1);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals("b", actual[0]);
    }
    
    @Test
    public void sliceテスト4() {
        String[] actual = Arrays.slice(Arrays.gen("a", "b"), 0, 1);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals("a", actual[0]);
    }
    
}
