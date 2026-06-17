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

package jp.co.ctc_g.jse.core.util.web.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;

public class HalfwidthNumberEditorTest {

    @Test
    public void Byte型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Byte.class, false);
        editor.setAsText("-128");
        assertThat(editor.getAsText(), is("-128"));

        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));

        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));

        editor.setAsText("127");
        assertThat(editor.getAsText(), is("127"));
    }

    @Test
    public void Byte型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Byte.class, false);
        try {
            editor.setAsText("-129");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("128");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("0.0");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }
    }

    @Test
    public void ブランク文字がByte型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Byte.class, true);
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));

        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
    }

    @Test
    public void ブランク文字がByte型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Byte.class, false);
        try {
            editor.setAsText("");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("必須入力です。"));
        }

        try {
            editor.setAsText(null);
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("必須入力です。"));
        }
    }

    @Test
    public void Short型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Short.class, false);
        editor.setAsText("-32768");
        assertThat(editor.getAsText(), is("-32768"));

        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));

        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));

        editor.setAsText("32767");
        assertThat(editor.getAsText(), is("32767"));
    }

    @Test
    public void Short型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Short.class, false);
        try {
            editor.setAsText("-32769");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("32768");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }
    }

    @Test
    public void Integer型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Integer.class, false);
        editor.setAsText("-2147483648");
        assertThat(editor.getAsText(), is("-2147483648"));

        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));

        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));

        editor.setAsText("2147483647");
        assertThat(editor.getAsText(), is("2147483647"));
    }

    @Test
    public void Integer型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Integer.class, false);
        try {
            editor.setAsText("-2147483649");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("2147483648");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }
    }

    @Test
    public void Long型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Long.class, false);
        editor.setAsText("-9223372036854775808");
        assertThat(editor.getAsText(), is("-9223372036854775808"));

        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));

        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));

        editor.setAsText("9223372036854775807");
        assertThat(editor.getAsText(), is("9223372036854775807"));
    }

    @Test
    public void Long型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(Long.class, false);
        try {
            editor.setAsText("-9223372036854775809");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("9223372036854775808");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }
    }

    @Test
    public void BigInteger型へ正常に変換される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(BigInteger.class, false);
        editor.setAsText(String.valueOf(BigInteger.ONE));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ONE)));

        editor.setAsText(String.valueOf(BigInteger.TEN));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.TEN)));

        editor.setAsText(String.valueOf(BigInteger.ZERO));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ZERO)));

        editor.setAsText("2147483647");
        assertThat(editor.getAsText(), is(String.valueOf("2147483647")));

        editor.setAsText("2147483648");
        assertThat(editor.getAsText(), is(String.valueOf("2147483648")));

        editor.setAsText("876543210987654321");
        assertThat(editor.getAsText(), is(String.valueOf("876543210987654321")));

        editor.setAsText("9223372036854775807");
        assertThat(editor.getAsText(), is(String.valueOf("9223372036854775807")));

        editor.setAsText("-9223372036854775807");
        assertThat(editor.getAsText(), is(String.valueOf("-9223372036854775807")));

        editor.setAsText("9223372036854775808");
        assertThat(editor.getAsText(), is(String.valueOf("9223372036854775808")));
    }

    @Test
    public void BigInteger型へ変換時にエラーが発生する() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(BigInteger.class, false);
        try {
            editor.setAsText("0.0");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }

        try {
            editor.setAsText("9,223,372,036,854,775,808");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|[1-9][0-9]*)$)で入力してください。"));
        }
    }

    @Test
    public void null値と空文字が正常が許可される() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(BigInteger.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));

        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }

    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfwidthNumberEditor editor = new HalfwidthNumberEditor(BigInteger.class, false, "override message");
        try {
            editor.setAsText("foo");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("override message"));
        }
    }

    @Test
    public void 型指定が不正のときはエラーが発生する() {
        assertThrows(InternalException.class, () -> {
            new HalfwidthNumberEditor(null, false);
            fail();
        });
    }
}
