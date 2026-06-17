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
import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class HalfwidthNumberWithCommaEditorTest {
    
    @BeforeAll
    public static void setupClass() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("classpath:/jp/co/ctc_g/jse/core/FrameworkResources");
        MessageSourceLocator.set(source);
    }
    
    @Test
    public void Short型へ正常に変換される() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Short.class, false);
        editor.setAsText("-32,768");
        assertThat(editor.getAsText(), is("-32,768"));
        
        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("32,767");
        assertThat(editor.getAsText(), is("32,767"));
    }

    @Test
    public void Short型へ変換時にエラーが発生する() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Short.class, false);
        try {
            editor.setAsText("-32,769");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("32,768");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
    }
    
    @Test
    public void Integer型へ正常に変換される() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Integer.class, false);
        editor.setAsText("-2,147,483,648");
        assertThat(editor.getAsText(), is("-2,147,483,648"));
        
        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("2,147,483,647");
        assertThat(editor.getAsText(), is("2,147,483,647"));
    }

    @Test
    public void Integer型へ変換時にエラーが発生する() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Integer.class, false);
        try {
            editor.setAsText("-2,147,483,649");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("2,147,483,648");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
    }
    
    @Test
    public void Long型へ正常に変換される() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Long.class, false);
        editor.setAsText("-9,223,372,036,854,775,808");
        assertThat(editor.getAsText(), is("-9,223,372,036,854,775,808"));
        
        editor.setAsText("0");
        assertThat(editor.getAsText(), is("0"));
        
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
        
        editor.setAsText("9,223,372,036,854,775,807");
        assertThat(editor.getAsText(), is("9,223,372,036,854,775,807"));
    }
    
    @Test
    public void Long型へ変換時にエラーが発生する() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(Long.class, false);
        try {
            editor.setAsText("-9,223,372,036,854,775,809");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
        
        try {
            editor.setAsText("9,223,372,036,854,775,808");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
    }
    
    @Test
    public void BigInteger型へ正常に変換される() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(BigInteger.class, false);
        editor.setAsText(String.valueOf(BigInteger.ONE));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ONE)));
        
        editor.setAsText(String.valueOf(BigInteger.TEN));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.TEN)));
        
        editor.setAsText(String.valueOf(BigInteger.ZERO));
        assertThat(editor.getAsText(), is(String.valueOf(BigInteger.ZERO)));
        
        editor.setAsText("2,147,483,647");
        assertThat(editor.getAsText(), is(String.valueOf("2,147,483,647")));
        
        editor.setAsText("2,147,483,648");
        assertThat(editor.getAsText(), is(String.valueOf("2,147,483,648")));
        
        editor.setAsText("876,543,210,987,654,321");
        assertThat(editor.getAsText(), is(String.valueOf("876,543,210,987,654,321")));
        
        editor.setAsText("9,223,372,036,854,775,807");
        assertThat(editor.getAsText(), is(String.valueOf("9,223,372,036,854,775,807")));
        
        editor.setAsText("-9,223,372,036,854,775,807");
        assertThat(editor.getAsText(), is(String.valueOf("-9,223,372,036,854,775,807")));
        
        editor.setAsText("9,223,372,036,854,775,808");
        assertThat(editor.getAsText(), is(String.valueOf("9,223,372,036,854,775,808")));
    }
    
    @Test
    public void BigInteger型へ変換時にエラーが発生する() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(BigInteger.class, false);
        try {
            editor.setAsText("0.0");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0]|([1-9][0-9]{0,2}([,，][0-9]{3})*))$)で入力してください。"));
        }
    }

    @Test
    public void null値と空文字が正常が許可される() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(BigInteger.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfwidthNumberWithCommaEditor editor = new HalfwidthNumberWithCommaEditor(BigInteger.class, false, "override message");
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
            new HalfwidthNumberWithCommaEditor(null, false);
            fail();
        });
    }
}
