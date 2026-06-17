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

public class HalfAndFullwidthNumberWithCommaEditorTest {
    
    @Test
    public void Byte型へ正常に変換される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Byte.class, false);
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
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Byte.class, false);
        try {
            editor.setAsText("－１２９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("１２８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
    }

    @Test
    public void Short型へ正常に変換される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Short.class, false);
        editor.setAsText("－３２，７６８");
        assertThat(editor.getAsText(), is("-32,768"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("３２，７６７");
        assertThat(editor.getAsText(), is("32,767"));
    }

    @Test
    public void Short型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Short.class, false);
        try {
            editor.setAsText("－３２，７６９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("３２，７６８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
    }

    @Test
    public void Integer型へ正常に変換される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Integer.class, false);
        editor.setAsText("－２，１４７，４８３，６４８");
        assertThat(editor.getAsText(), is("-2,147,483,648"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("２，１４７，４８３，６４７");
        assertThat(editor.getAsText(), is("2,147,483,647"));
    }

    @Test
    public void Integer型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Integer.class, false);
        try {
            editor.setAsText("－２，１４７，４８３，６４９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("２，１４７，４８３，６４８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
    }

    @Test
    public void Long型へ正常に変換される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Long.class, false);
        editor.setAsText("－９，２２３，３７２，０３６，８５４，７７５，８０８");
        assertThat(editor.getAsText(), is("-9,223,372,036,854,775,808"));
        
        editor.setAsText("０");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("１");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("９，２２３，３７２，０３６，８５４，７７５，８０７");
        assertThat(editor.getAsText(), is("9,223,372,036,854,775,807"));
    }
    
    @Test
    public void Long型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(Long.class, false);
        try {
            editor.setAsText("－９，２２３，３７２，０３６，８５４，７７５，８０９");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("９，２２３，３７２，０３６，８５４，７７５，８０８");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
    }

    @Test
    public void BigInteger型へ正常に変換される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(BigInteger.class, false);
        editor.setAsText(String.valueOf(BigInteger.ONE));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ONE)));
        
        editor.setAsText(String.valueOf(BigInteger.TEN));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.TEN)));
        
        editor.setAsText(String.valueOf(BigInteger.ZERO));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ZERO)));
    
        editor.setAsText("２，１４７，４８３，６４７");
        assertThat(editor.getAsText(), is(String.valueOf("2,147,483,647")));
        
        editor.setAsText("２，１４７，４８３，６４８");
        assertThat(editor.getAsText(), is(String.valueOf("2,147,483,648")));
        
        editor.setAsText("８７６，５４３，２１０，９８７，６５４，３２１");
        assertThat(editor.getAsText(), is(String.valueOf("876,543,210,987,654,321")));
        
        editor.setAsText("９，２２３，３７２，０３６，８５４，７７５，８０７");
        assertThat(editor.getAsText(), is(String.valueOf("9,223,372,036,854,775,807")));
        
        editor.setAsText("－９，２２３，３７２，０３６，８５４，７７５，８０７");
        assertThat(editor.getAsText(), is(String.valueOf("-9,223,372,036,854,775,807")));
        
        editor.setAsText("９，２２３，３７２，０３６，８５４，７７５，８０８");
        assertThat(editor.getAsText(), is(String.valueOf("9,223,372,036,854,775,808")));
    
        editor.setAsText("１７９，７６９，３１３，４８６，２３１，５７０，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００");
        assertThat(editor.getAsText(), is("179,769,313,486,231,570,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000"));
    }
    
    @Test
    public void BigInteger型へ変換時にエラーが発生する() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(BigInteger.class, false);
        try {
            editor.setAsText("0.0");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("2147483647");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$)で入力してください。"));
        }
    }
    
    @Test
    public void null値と空文字が正常が許可される() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(BigInteger.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfAndFullwidthNumberWithCommaEditor editor = new HalfAndFullwidthNumberWithCommaEditor(BigInteger.class, false, "override message");
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
            new HalfAndFullwidthNumberWithCommaEditor(null, false);
            fail();
        });
    }
}
