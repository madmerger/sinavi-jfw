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
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListsTest {

    @Test
    public void 単純genテスト() {
        List<String> elements = new ArrayList<String>();
        elements.add("a");
        elements.add("b");
        elements.add("c");
        elements.add("d");
        elements.add("e");
        List<String> results = Lists.gen("a", "b", "c", "d", "e");
        assertEquals(elements, results);
    }
    
    @Test
    public void コールバックgenテスト() {
        final List<String> elements = new ArrayList<String>();
        elements.add("a");
        elements.add("b");
        elements.add("c");
        elements.add("d");
        elements.add("e");
        List<String> results = Lists.gen(5, new GenCall<String>() {
            public String gen(int index, int total) {
                return elements.get(index);
            }
        });
        assertEquals(elements, results);
    }
    
    @Test
    public void 実際よりも少ない要素数を返却するコールバックgenテスト() {
        final List<String> elements = new ArrayList<String>();
        elements.add("a");
        elements.add("b");
        elements.add("c");
        elements.add("d");
        elements.add("e");
        List<String> results = Lists.gen(5, new GenCall<String>() {
            public String gen(int index, int total) {
                return index % 2 == 0 ? elements.get(index) : null;
            }
        });
        assertEquals(Lists.gen("a", "c", "e"), results);
        assertEquals(3, results.size());
    }
    
    @Test
    public void eachテスト() {
        final List<String> elements = Lists.gen("a", "b", "c", "d", "e");
        final int[] datum = {0, elements.size()};
        Lists.each(elements, new EachCall<String>() {
            public void each(String element, int index, int total) {
                assertEquals(elements.get(index), element);
                assertEquals(datum[0]++, index);
                assertEquals(datum[1], total);
            }
        });
    }
    
    @Test
    public void collectテスト() {
        final List<String> elements = Lists.gen("a", "b", "c", "d", "e");
        final int[] datum = {0, elements.size()};
        List<String> results = Lists.collect(elements, new CollectCall<String>() {
            public String collect(String element, int index, int total) {
                assertEquals(elements.get(index), element);
                assertEquals(datum[0]++, index);
                assertEquals(datum[1], total);
                return index % 2 == 0 ? element : null;
            }
        });
        assertEquals(Lists.gen("a", "c", "e"), results);
        assertEquals(Lists.gen("a", "b", "c", "d", "e"), elements);
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
        final List<String> groups = Lists.gen("A", "A", "B", "B", "B", "C");
        final List<String> names = Lists.gen("a", "b", "c", "d", "e", "f");
        List<Groupee> elements = new ArrayList<Groupee>();
        for (int i = 0; i < groups.size(); i++) {
            elements.add(new Groupee(groups.get(i), names.get(i)));
        }
        Map<String, List<Groupee>> results = Lists.group(elements, new GroupCall<Groupee>() {
            public String group(Groupee group, int index, int total) {
                return group.getGroup();
            }
        });
        Map<String, List<Groupee>> expected = new HashMap<String, List<Groupee>>();
        expected.put("A", Lists.gen(new Groupee("A", "a"), new Groupee("A", "b")));
        expected.put("B", Lists.gen(new Groupee("B", "c"), new Groupee("B", "d"), new Groupee("B", "e")));
        expected.put("C", Lists.gen(new Groupee("C", "f")));
        assertEquals(expected, results);
    }
    
    @Test
    public void isEmptyテスト() {
        assertTrue(Lists.isEmpty(Collections.emptyList()));
        assertTrue(Lists.isEmpty(null));
    }
    
    @Test
    public void emptyPartialListテスト() {
        assertThat(Lists.emptyPartialList(), isA(PartialList.class));
        assertTrue(Lists.emptyPartialList().isEmpty());
    }
    
   
}
