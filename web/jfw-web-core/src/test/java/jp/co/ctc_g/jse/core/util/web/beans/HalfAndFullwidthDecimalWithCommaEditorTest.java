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

import java.math.BigDecimal;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;

public class HalfAndFullwidthDecimalWithCommaEditorTest {
    
    @Test
    public void Float型へ正常に変換される() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(Float.class, false);
        editor.setAsText("０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("－０．０");
        assertThat(editor.getAsText(), is("-0.0"));
        
        editor.setAsText("０．００００００００００００００００００００００００００００００００００００００００００００１４０１２９８４６４３２４８１７");
        assertThat(editor.getAsText(), is("0.000000000000000000000000000000000000000000001401298464324817"));
        
        editor.setAsText("３４０，２８２，３４６，６３８，５２８，８６０，０００，０００，０００，０００，０００，０００，０００．０");
        assertThat(editor.getAsText(), is("340,282,346,638,528,860,000,000,000,000,000,000,000.0"));
    }

    @Test
    
    public void Float型へ変換時にエラーが発生する() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(Float.class, false);
        try {
            editor.setAsText("１");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("０");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
    }
    
    @Test
    public void Double型へ正常に変換される() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(Double.class, false);
        editor.setAsText("０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("－０．０");
        assertThat(editor.getAsText(), is("-0.0"));
        
        editor.setAsText("１００，２００，３０１．００１");
        assertThat(editor.getAsText(), is("100,200,301.001"));
        
        editor.setAsText("１００，２００，３００．００１");
        assertThat(editor.getAsText(), is("100,200,300.001"));
        
        editor.setAsText("－１００，２００，３００．００１");
        assertThat(editor.getAsText(), is("-100,200,300.001"));
        
        editor.setAsText("０．０００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００４９");
        assertThat(editor.getAsText(), is("0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049"));
        
        editor.setAsText("１７９，７６９，３１３，４８６，２３１，５７０，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００．０");
        assertThat(editor.getAsText(), is("179,769,313,486,231,570,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000.0"));
    }
    
    @Test
    public void Double型へ変換時にエラーが発生する() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(Double.class, false);
        try {
            editor.setAsText("１");
            fail();
            
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("０");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
    }
    
    @Test
    public void BigDecimal型へ正常に変換される() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(BigDecimal.class, false);
        editor.setAsText("０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("０．００");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("０．０００");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("－０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("１００，２００，３０１．００１");
        assertThat(editor.getAsText(), is("100,200,301.001"));
        
        editor.setAsText("１００，２００，３００．００１");
        assertThat(editor.getAsText(), is("100,200,300.001"));
        
        editor.setAsText("－１００，２００，３００．００１");
        assertThat(editor.getAsText(), is("-100,200,300.001"));
        
        editor.setAsText("０．０００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００４９");
        assertThat(editor.getAsText(), is("0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049"));
        
        editor.setAsText("１７９，７６９，３１３，４８６，２３１，５７０，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００，０００．０");
        assertThat(editor.getAsText(), is("179,769,313,486,231,570,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000,000.0"));
    }
    
    @Test
    public void BigDecimal型へ変換時にエラーが発生する() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(BigDecimal.class, false);
        try {
            editor.setAsText("１");
            fail();
            
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
        
        try {
            editor.setAsText("０");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$)で入力してください。"));
        }
    }
    
    @Test
    public void null値と空文字が正常が許可される() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(BigDecimal.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }

    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfAndFullwidthDecimalWithCommaEditor editor = new HalfAndFullwidthDecimalWithCommaEditor(BigDecimal.class, false, "override message");
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
            new HalfAndFullwidthDecimalWithCommaEditor(null, false);
            fail();
        });
    }
}
