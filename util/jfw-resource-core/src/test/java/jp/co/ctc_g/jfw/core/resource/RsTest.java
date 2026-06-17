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

package jp.co.ctc_g.jfw.core.resource;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:/jp/co/ctc_g/jfw/core/resource/Context.xml")
public class RsTest {

    private static final String EXISTS_IN_APP = "jp.co.ctc_g.jfw.core.resource.RsTest.VICTIM";

    @Test
    public void findテスト() {
        String expected = "OK";
        String actual = Rs.find(EXISTS_IN_APP);
        assertEquals(expected, actual);
    }

    @Test
    public void find無効キーテスト() {
        String actual = Rs.find("");
        assertNotNull(actual);
        assertEquals("", actual);
        actual = Rs.find((String)null);
        assertNotNull(actual);
        assertEquals("", actual);
    }

    @Test
    public void find存在しないキーテスト() {
        String key = "2c0cb5e902703e9e30523c1a6ba51d77";
        String actual = Rs.find(key);
        assertNotNull(actual);
        assertEquals(key, actual);
    }

    @Test
    public void find値がないテスト() {
        String key = "jp.co.ctc_g.jfw.core.resource.RsTest.NO_VALUE";
        String actual = Rs.find(key);
        assertNotNull(actual);
        assertEquals(actual, key);
    }

    @Test
    public void findデフォルト値設定テスト() {
        String actual = Rs.find("2c0cb5e902703e9e30523c1a6ba51d77", "default");
        assertNotNull(actual);
        assertEquals(actual, "default");
        actual = Rs.find("", "d");
        assertNotNull(actual);
        assertEquals("", actual);
        actual = Rs.find(null, "d");
        assertNotNull(actual);
        assertEquals("", actual);
        String key = "jp.co.ctc_g.jfw.core.resource.RsTest.NO_VALUE";
        actual = Rs.find(key, "d");
        assertNotNull(actual);
        assertEquals(actual, key);
    }

}
