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

import org.junit.jupiter.api.Test;

public class FormatsTest {

    @Test
    public void format日付テスト() {
        String format = "%4$2s %3$2s %2$2s %1$2s";
        String expected = " d  c  b  a";
        String actual = Formats.format(format, "a", "b", "c", "d");
        assertEquals(expected, actual);
    }
    
    @Test
    public void format浮動小数値テスト() {
        String format = "%,9.3f";
        String expected = "6,217.580";
        String actual = Formats.format(format, 6217.58f);
        assertEquals(expected, actual);
    }
}
