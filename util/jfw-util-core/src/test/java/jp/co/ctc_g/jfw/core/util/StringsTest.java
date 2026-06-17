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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class StringsTest {

    @Test
    public void 配列joinテスト() {
        String expected = "A:B:C:D:E";
        String actual = Strings.joinBy(":", Arrays.gen("A", "B", "C", "D", "E"));
        assertEquals(expected, actual);
    }

    @Test
    public void 単要素配列joinテスト() {
        String expected = "A";
        String actual = Strings.joinBy(":", Arrays.gen("A"));
        assertEquals(expected, actual);
    }

    @Test
    public void 空要素を含む配列joinテスト() {
        String expected = "A";
        String actual = Strings.joinBy(":", Arrays.gen("", "A"));
        assertEquals(expected, actual);
    }

    @Test
    public void 無要素配列joinテスト() {
        String expected = "";
        String actual = Strings.joinBy(":", Arrays.gen());
        assertEquals(expected, actual);
    }

    @Test
    public void リストjoinテスト() {
        String expected = "A:B:C:D:E";
        String actual = Strings.joinBy(":", Lists.gen("A", "B", "C", "D", "E"));
        assertEquals(expected, actual);
        expected = "ABCDE";
        actual = Strings.joinBy(null, Lists.gen("A", "B", "C", "D", "E"));
        assertEquals(expected, actual);
    }

    @Test
    public void 単要素リストjoinテスト() {
        String expected = "A";
        String actual = Strings.joinBy(":", Lists.gen("A"));
        assertEquals(expected, actual);
    }

    @Test
    public void 無要素リストjoinテスト() {
        String expected = "";
        String actual = Strings.joinBy(":", Lists.gen());
        assertEquals(expected, actual);
    }

    @Test
    public void judgeBlankテスト() {
        String[] successes = Arrays.gen(
            "123", "abc", "ABC", "あいう", "-123",
            "アイウ", "ｱｲｳ", "ァィゥ", "!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "{", "}", "[", "]"
        );
        for (String success : successes) {
            assertFalse(Strings.isBlank(success));
        }
        String[] failures = Arrays.gen(
            "", " ", "　", "\t", "\r", "\n", "\f",
            "  ", " \t", "\t ", "\r\t", "\r\n ", "\f　",
            " \r\n", "　\r\n", "　\n", "\t\n"
        );
        for (String failure : failures) {
            assertTrue(Strings.isBlank(failure));
        }
    }

    @Test
    public void replaceBlankテスト() {
        String[] seeds = Arrays.gen(
            null, "", "\n", "\t", " ", "　",
            "a", "abc", "a b c", "a \nb", " a,\r\n b",
            "あ", "あいう", "あ い う", "あ \nい", "あ, \r\n いう"
        );
        final String[] expected = Arrays.gen(
            "", "", "", "", "", "",
            "a", "abc", "abc", "ab", "a,b",
            "あ", "あいう", "あいう", "あい", "あ,いう"
        );
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String actual = Strings.removeBlanks(element);
                assertEquals(expected[index], actual);
            }
        });
    }

    @Test
    public void reverseテスト() {
        assertThat(Strings.reverse(null), nullValue());
        assertThat(Strings.reverse(""), is(""));
        final String[] seeds = Arrays.gen("ABCDE", "あいうえお", "12345");
        final String[] expected = Arrays.gen("EDCBA", "おえういあ", "54321");
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String actual = Strings.reverse(element);
                assertEquals(expected[index], actual);
            }
        });
    }

    @Test
    public void substituteテスト() {
        assertThat(Strings.substitute(null, null), nullValue());
        assertThat(Strings.substitute("", null), is(""));
        assertThat(Strings.substitute("a", null), is("a"));
        assertThat(Strings.substitute("a", Collections.<String, String> emptyMap()), is("a"));
        final String[] seeds = Arrays.gen(
                "a${i}u${e}o",
                "${a}i${u}e${o}",
                "あ${i}う${e}お",
                "${a}い${u}え${o}");
        final String[] expected = Arrays.gen(
                "aいuえo",
                "あiうeお",
                "あいうえお",
                "あいうえお");
        final Map<String, ?> replace =  Maps.hash("a", "あ")
                .map("i", "い").map("u", "う").map("e", "え").map("o", "お");
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String s = Strings.substitute(element, replace);
                assertEquals(expected[index], s, "loop:" + index);
            }
        });
    }

    @Test
    public void substituteエスケープテスト() {
        final String[] seeds = Arrays.gen(
                "a\\${i}u\\${e}o",
                "\\${a}i\\${u}e\\${o}",
                "あ\\${i}う\\${e}お",
                "\\${a}い\\${u}え\\${o}");
        final String[] expected = Arrays.gen(
                "a${i}u${e}o",
                "${a}i${u}e${o}",
                "あ${i}う${e}お",
                "${a}い${u}え${o}");
        final Map<String, ?> replace =  Maps.hash("a", "あ")
                .map("i", "い").map("u", "う").map("e", "え").map("o", "お");
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String s = Strings.substitute(element, replace);
                assertEquals(expected[index], s, "loop:" + index);
            }
        });
    }

    @Test
    public void substitute部分エスケープテスト() {
        final String[] seeds = Arrays.gen(
                "a\\${i}u${e}o",
                "${a}i\\${u}e\\${o}",
                "あ${i}う\\${e}お",
                "${a}い\\${u}え${o}");
        final String[] expected = Arrays.gen(
                "a${i}uえo",
                "あi${u}e${o}",
                "あいう${e}お",
                "あい${u}えお");
        final Map<String, ?> replace =  Maps.hash("a", "あ")
                .map("i", "い").map("u", "う").map("e", "え").map("o", "お");
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String element, int index, int total) {
                String s = Strings.substitute(element, replace);
                assertEquals(expected[index], s, "loop:" + index);
            }
        });
    }

    @Test
    public void substitute日付書式化テスト() {
        final String seed = "${time%tY}/${time%tm}/${time%td}";
        final String expected = "2008/11/15";
        final Map<String, ?> replace =  Maps.hash("time", Dates.makeFrom(2008, 11, 15));
        String value = Strings.substitute(seed, replace);
        assertEquals(expected, value);
    }

    @Test
    public void substitute浮動小数値書式化テスト() {
        final String seed = "幅10桁.3桁: ${number%010.3f}";
        final String expected = "幅10桁.3桁: 000011.150";
        final Map<String, ?> replace =  Maps.hash("number", 11.15f);
        String value = Strings.substitute(seed, replace);
        assertEquals(expected, value);
    }

    @Test
    public void findForテスト() {
        assertEquals("a", Strings.findPlaceHolder("${a}"));
        assertEquals("a.b", Strings.findPlaceHolder("${a.b}"));
    }

    @Test
    public void trimテスト() {
        assertThat(Strings.trim(null), nullValue());
        assertThat(Strings.trim(""), is(""));
        final String[] seeds = {
            "abcdefg", "あいうえお",
            // 半角空白
            " abcdefg", "abcdefg ", " あいうえお", "あいうえお ",
            // 全角空白
            "　abcdefg", "abcdefg　", "　あいうえお", "あいうえお　",
            // 全角空白複数
            "　　abcdefg", "abcdefg　　", "　　あいうえお", "あいうえお　　",
            // 全角半角空白混合
            "　 abcdefg", "abcdefg 　", "　 あいうえお", "あいうえお 　",
            // 全角半角空白混合かつ途中に半角空白
            "　 abc defg", "abc defg 　", "　 あい うえお", "あい うえお 　"
        };
        final String[] expects = {
            "abcdefg", "あいうえお",
            "abcdefg", "abcdefg", "あいうえお", "あいうえお",
            "abcdefg", "abcdefg", "あいうえお", "あいうえお",
            "abcdefg", "abcdefg", "あいうえお", "あいうえお",
            "abcdefg", "abcdefg", "あいうえお", "あいうえお",
            "abc defg", "abc defg", "あい うえお", "あい うえお",
        };
        Arrays.each(seeds, new EachCall<String>() {
           public void each(String e, int i, int t) {
               String s = Strings.trim(e);
               assertEquals(expects[i], s, "loop: " + i);
           }
        });
    }

    @Test
    public void splitテスト() {
        assertThat(Strings.trim(""), is(""));
        final String[] seeds = {
            "abc,def",
            "abc\\,def",
            "abc\\\\,def"
        };
        final String[][] expected = {
            {"abc", "def"},
            {"abc,def"},
            {"abc\\,def"}
        };
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String e, int i, int t) {
                String[] values = Strings.split(",", e);
                assertArrayEquals(expected[i], values, "loop: " + i);
            }
        });
    }

    @Test
    public void splitテスト2() {
        final String[] seeds = {
            "abcdef",
            "abc\\def",
            "abc\\\\def"
        };
        final String[][] expected = {
            {"abc", "f"},
            {"abcdef"},
            {"abc\\def"}
        };
        Arrays.each(seeds, new EachCall<String>() {
            public void each(String e, int i, int t) {
                String[] values = Strings.split("de", e);
                assertArrayEquals(expected[i], values, "loop: " + i);
            }
        });
    }
    
    @Test
    public void escapeRegex() {
        assertEquals("\\Q.\\E", Strings.escapeRegex("."));
        assertEquals("\\Q?\\E", Strings.escapeRegex("?"));
        assertEquals("\\Q*\\E", Strings.escapeRegex("*"));
        assertEquals("\\Q+\\E", Strings.escapeRegex("+"));
        assertEquals("\\Q\\d\\E", Strings.escapeRegex("\\d"));
        assertEquals("\\Q\\$\\E", Strings.escapeRegex("\\$"));
        assertEquals("\\Q|\\E", Strings.escapeRegex("|"));
        assertEquals("\\Q[]\\E", Strings.escapeRegex("[]"));
        assertEquals("\\Q()\\E", Strings.escapeRegex("()"));
        assertEquals("\\Q{}\\E", Strings.escapeRegex("{}"));
    }
    
    @Test
    public void toSnake() {
        assertEquals("", Strings.toSnake(""));
        assertEquals("A_B", Strings.toSnake("aB"));
        assertEquals("AB", Strings.toSnake("AB"));
        assertEquals("AB", Strings.toSnake("Ab"));
        
        assertEquals("ABC", Strings.toSnake("abc"));
        
        assertEquals("ABC", Strings.toSnake("Abc"));
        assertEquals("A_BC", Strings.toSnake("ABc"));
        assertEquals("ABC", Strings.toSnake("ABC"));
        
        assertEquals("A_BC", Strings.toSnake("aBc"));
        assertEquals("A_BC", Strings.toSnake("aBC"));
        
        assertEquals("AB_C", Strings.toSnake("abC"));
        
        assertEquals("AB_CD", Strings.toSnake("abCd"));
        assertEquals("AB_CD", Strings.toSnake("ABCd"));
        assertEquals("AB_CD", Strings.toSnake("abCD"));
        assertEquals("AB_CD", Strings.toSnake("AbCd"));

        assertEquals("0B", Strings.toSnake("0b"));
        assertEquals("0_B", Strings.toSnake("0B"));
        assertEquals("A1", Strings.toSnake("a1"));
        assertEquals("A1", Strings.toSnake("A1"));
    }
    
    @Test
    public void toCamelテスト() {
        assertThat(Strings.toCamel(""), is(""));
        assertThat(Strings.toCamel("A_B"), is("aB"));
        assertThat(Strings.toCamel("AB"), is("ab"));
        assertThat(Strings.toCamel("ABC"), is("abc"));
        assertThat(Strings.toCamel("A_BC"), is("aBc"));
        assertThat(Strings.toCamel("AB_C"), is("abC"));
        assertThat(Strings.toCamel("AB_CD"), is("abCd"));
        assertThat(Strings.toCamel("0B"), is("0b"));
        assertThat(Strings.toCamel("0_B"), is("0B"));
        assertThat(Strings.toCamel("A1"), is("a1"));
    }
    
    @Test
    public void properテスト() {
        assertThat(Strings.proper("", "bbb"), is("bbb"));
        assertThat(Strings.proper("aaa", "bbb"), is("aaa"));
    }
    
    @Test
    public void containBlankテスト() {
        assertThat(Strings.containBlank(null), is(true));
        assertThat(Strings.containBlank(""), is(true));
        assertThat(Strings.containBlank(" "), is(true));
        assertThat(Strings.containBlank("　"), is(true));
        assertThat(Strings.containBlank("\t"), is(true));
        assertThat(Strings.containBlank("\n"), is(true));
        assertThat(Strings.containBlank("\f"), is(true));
        assertThat(Strings.containBlank("\r"), is(true));
    }
    
    @Test
    public void escapeHTMLテスト() {
        assertThat(Strings.escapeHTML("&"), is("&amp;"));
        assertThat(Strings.escapeHTML("<"), is("&lt;"));
        assertThat(Strings.escapeHTML(">"), is("&gt;"));
        assertThat(Strings.escapeHTML("\""), is("&quot;"));
        assertThat(Strings.escapeHTML("a"), is("a"));
    }
    
    @Test
    public void isEmptyテスト() {
        assertThat(Strings.isEmpty(" ", true), is(true));
        assertThat(Strings.isEmpty(" ", false), is(false));
    }
    
    @Test
    public void equalsIgnoreCaseテスト() {
        assertThat(Strings.equalsIgnoreCase(null, null), is(true));
        assertThat(Strings.equalsIgnoreCase(null, ""), is(false));
        assertThat(Strings.equalsIgnoreCase("", ""), is(true));
        assertThat(Strings.equalsIgnoreCase("a", "b"), is(false));
    }
    
    @Test
    public void startsWithテスト() {
        assertThat(Strings.startsWith("", ""), is(false));
        assertThat(Strings.startsWith("", null), is(false));
        assertThat(Strings.startsWith(null, null), is(false));
        assertThat(Strings.startsWith(null, ""), is(false));
        assertThat(Strings.startsWith("/start/hoge", "/start"), is(true));
        assertThat(Strings.startsWith("/Start/hoge", "/start"), is(true));
        assertThat(Strings.startsWith("/Start/hoge", "/start", false), is(false));
    }
    
    @Test
    public void endWithテスト() {
        assertThat(Strings.endsWith("", ""), is(false));
        assertThat(Strings.endsWith("", null), is(false));
        assertThat(Strings.endsWith(null, null), is(false));
        assertThat(Strings.endsWith(null, ""), is(false));
        assertThat(Strings.endsWith("/start/hoge", "/hoge"), is(true));
        assertThat(Strings.endsWith("/start/Hoge", "/hoge"), is(true));
        assertThat(Strings.endsWith("/start/Hoge", "/hoge", false), is(false));
    }
}
