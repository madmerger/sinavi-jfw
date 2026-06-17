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

package jp.co.ctc_g.jfw.core.util.jdbc;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SQLsTest {

    @Test
    public void エスケープテスト正常系1() {
        String[] expected = {
                "AAA",
                "\\%AAA",
                "\\_AAA",
                "AA\\%A",
                "AA\\_A",
                "AAA\\%",
                "AAA\\_"
        };
        String[] datum = {
                "AAA",
                "%AAA",
                "_AAA",
                "AA%A",
                "AA_A",
                "AAA%",
                "AAA_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.escape(datum[i]));
        }
    }

    @Test
    public void エスケープテスト正常系2() {
        String[] expected = {
                "亜唖阿",
                "\\％亜唖阿",
                "\\＿亜唖阿",
                "亜唖\\％阿",
                "亜唖\\＿阿",
                "亜唖阿\\％",
                "亜唖阿\\＿"
        };
        String[] datum = {
                "亜唖阿",
                "％亜唖阿",
                "＿亜唖阿",
                "亜唖％阿",
                "亜唖＿阿",
                "亜唖阿％",
                "亜唖阿＿"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.escape(datum[i]));
        }
    }

    @Test
    public void エスケープテスト正常系3() {
        String[] expected = {
                "亜唖阿",
                "\\%亜唖阿",
                "\\_亜唖阿",
                "亜唖\\%阿",
                "亜唖\\_阿",
                "亜唖阿\\%",
                "亜唖阿\\_"
        };
        String[] datum = {
                "亜唖阿",
                "%亜唖阿",
                "_亜唖阿",
                "亜唖%阿",
                "亜唖_阿",
                "亜唖阿%",
                "亜唖阿_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.escape(datum[i]));
        }
    }

    @Test
    public void エスケープテスト正常系4() {
        String[] expected = {
                "AAA",
                "\\％AAA",
                "\\＿AAA",
                "AA\\％A",
                "AA\\＿A",
                "AAA\\％",
                "AAA\\＿"
        };
        String[] datum = {
                "AAA",
                "％AAA",
                "＿AAA",
                "AA％A",
                "AA＿A",
                "AAA％",
                "AAA＿"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.escape(datum[i]));
        }
    }

    @Test
    public void エスケープテスト正常系5() {
        assertNull(SQLs.escape(null));
    }

    @Test
    public void エスケープテスト正常系6() {
        assertEquals("", SQLs.escape(""));
    }

    @Test
    public void エスケープテスト正常系7() {
        String[] expected = {
                "\\\\",
                "\\\\A",
                "A\\\\A",
                "A\\\\",
                "AAA",
                "\\\\\\%AAA",
                "\\\\\\_AAA",
                "AA\\\\\\%A",
                "AA\\\\\\_A",
                "AAA\\\\\\%",
                "AAA\\\\\\_"
        };
        String[] datum = {
                "\\",
                "\\A",
                "A\\A",
                "A\\",
                "AAA",
                "\\%AAA",
                "\\_AAA",
                "AA\\%A",
                "AA\\_A",
                "AAA\\%",
                "AAA\\_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.escape(datum[i]), "index = " + i);
        }
    }

    @Test
    public void エスケープ前方マッチ追加テスト正常系1() {
        String[] expected = {
                "AAA%",
                "\\%AAA%",
                "\\_AAA%",
                "AA\\%A%",
                "AA\\_A%",
                "AAA\\%%",
                "AAA\\_%"
        };
        String[] datum = {
                "AAA",
                "%AAA",
                "_AAA",
                "AA%A",
                "AA_A",
                "AAA%",
                "AAA_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.likeStartsWith(datum[i]));
        }
    }

    @Test
    public void エスケープ後方マッチ追加テスト正常系1() {
        String[] expected = {
                "%AAA",
                "%\\%AAA",
                "%\\_AAA",
                "%AA\\%A",
                "%AA\\_A",
                "%AAA\\%",
                "%AAA\\_"
        };
        String[] datum = {
                "AAA",
                "%AAA",
                "_AAA",
                "AA%A",
                "AA_A",
                "AAA%",
                "AAA_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.likeEndsWith(datum[i]));
        }
    }

    @Test
    public void エスケープ前後方マッチ追加テスト正常系1() {
        String[] expected = {
                "%AAA%",
                "%\\%AAA%",
                "%\\_AAA%",
                "%AA\\%A%",
                "%AA\\_A%",
                "%AAA\\%%",
                "%AAA\\_%"
        };
        String[] datum = {
                "AAA",
                "%AAA",
                "_AAA",
                "AA%A",
                "AA_A",
                "AAA%",
                "AAA_"
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], SQLs.likeContains(datum[i]));
        }
    }

}
