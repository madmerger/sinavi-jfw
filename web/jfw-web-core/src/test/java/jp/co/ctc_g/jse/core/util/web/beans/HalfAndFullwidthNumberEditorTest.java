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

public class HalfAndFullwidthNumberEditorTest {
    
    @Test
    public void Byte型へ正常に変換される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Byte.class, false);
        editor.setAsText("－１２８");
        assertThat(editor.getAsText(), is("-128"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("１２７");
        assertThat(editor.getAsText(), is("127"));
    }

    @Test
    public void Byte型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Byte.class, false);
        try {
            editor.setAsText("－１２９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("１２８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }

    @Test
    public void Short型へ正常に変換される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Short.class, false);
        editor.setAsText("－３２７６８");
        assertThat(editor.getAsText(), is("-32768"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("３２７６７");
        assertThat(editor.getAsText(), is("32767"));
    }

    @Test
    public void Short型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Short.class, false);
        try {
            editor.setAsText("－３２７６９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("３２７６８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }

    @Test
    public void Integer型へ正常に変換される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Integer.class, false);
        editor.setAsText("－２１４７４８３６４８");
        assertThat(editor.getAsText(), is("-2147483648"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("２１４７４８３６４７");
        assertThat(editor.getAsText(), is("2147483647"));
    }

    @Test
    public void Integer型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Integer.class, false);
        try {
            editor.setAsText("－２１４７４８３６４９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("２１４７４８３６４８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }

    @Test
    public void Long型へ正常に変換される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Long.class, false);
        editor.setAsText("－９２２３３７２０３６８５４７７５８０８");
        assertThat(editor.getAsText(), is("-9223372036854775808"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("９２２３３７２０３６８５４７７５８０７");
        assertThat(editor.getAsText(), is("9223372036854775807"));
    }
    
    @Test
    public void Long型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(Long.class, false);
        try {
            editor.setAsText("－９２２３３７２０３６８５４７７５８０９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("９２２３３７２０３６８５４７７５８０８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }

    @Test
    public void BigInteger型へ正常に変換される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(BigInteger.class, false);
        editor.setAsText(String.valueOf(BigInteger.ONE));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ONE)));
        
        editor.setAsText(String.valueOf(BigInteger.TEN));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.TEN)));
        
        editor.setAsText(String.valueOf(BigInteger.ZERO));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ZERO)));
        
        editor.setAsText("２１４７４８３６４７");
        assertThat(editor.getAsText(), is(String.valueOf("2147483647")));
        
        editor.setAsText("２１４７４８３６４８");
        assertThat(editor.getAsText(), is(String.valueOf("2147483648")));
        
        editor.setAsText("８７６５４３２１０９８７６５４３２１");
        assertThat(editor.getAsText(), is(String.valueOf("876543210987654321")));
        
        editor.setAsText("９２２３３７２０３６８５４７７５８０７");
        assertThat(editor.getAsText(), is(String.valueOf("9223372036854775807")));
        
        editor.setAsText("－９２２３３７２０３６８５４７７５８０７");
        assertThat(editor.getAsText(), is(String.valueOf("-9223372036854775807")));
        
        editor.setAsText("９２２３３７２０３６８５４７７５８０８");
        assertThat(editor.getAsText(), is(String.valueOf("9223372036854775808")));
    
        editor.setAsText("１７９７６９３１３４８６２３１５７００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００");
        assertThat(editor.getAsText(), is("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
    }
    
    @Test
    public void BigInteger型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(BigInteger.class, false);
        try {
            editor.setAsText("0.0");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }
    
    @Test
    public void null値と空文字が正常が許可される() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(BigInteger.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfAndFullwidthNumberEditor editor = new HalfAndFullwidthNumberEditor(BigInteger.class, false, "override message");
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
            new HalfAndFullwidthNumberEditor(null, false);
            fail();
        });
    }
}
