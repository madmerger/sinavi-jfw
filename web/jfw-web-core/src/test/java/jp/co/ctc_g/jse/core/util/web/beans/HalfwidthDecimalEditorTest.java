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

public class HalfwidthDecimalEditorTest {
    
    @Test
    public void BigDecimal型へ正常に変換される() {
        HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(BigDecimal.class, false);
        editor.setAsText("0.0");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("0.00");
        assertThat(editor.getAsText(), is("0.00"));
        
        editor.setAsText("0.000");
        assertThat(editor.getAsText(), is("0.000"));
        
        editor.setAsText("-0.0");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("0.1");
        assertThat(editor.getAsText(), is("0.1"));
        
        editor.setAsText("0.01");
        assertThat(editor.getAsText(), is("0.01"));
        
        editor.setAsText("0.001");
        assertThat(editor.getAsText(), is("0.001"));
        
        editor.setAsText("0.0001");
        assertThat(editor.getAsText(), is("0.0001"));
        
        editor.setAsText("0.00001");
        assertThat(editor.getAsText(), is("0.00001"));
        
        editor.setAsText("100200301.001");
        assertThat(editor.getAsText(), is("100200301.001"));
        
        editor.setAsText("100200300.001");
        assertThat(editor.getAsText(), is("100200300.001"));
        
        editor.setAsText("-100200300.001");
        assertThat(editor.getAsText(), is("-100200300.001"));
        
        editor.setAsText("0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049");
        assertThat(editor.getAsText(), is("0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049"));
        
        editor.setAsText("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0");
        assertThat(editor.getAsText(), is("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0"));
    }
    
    @Test
    public void null値と空文字が正常が許可される() {
        HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(BigDecimal.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void 型変換時にエラーが発生する() {
        HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(BigDecimal.class, false);
        try {
            editor.setAsText("foo");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-]?([0-9]*[.][0-9]+)$)で入力してください。"));
        }
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(BigDecimal.class, false, "override message");
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
            new HalfwidthDecimalEditor(null, false);
            fail();
        });
    }
    
    @Test
    public void Float型への変換はサポートしていない() {
        assertThrows(RuntimeException.class, () -> {
            HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(Float.class, false);
            editor.setAsText("0.0");
            editor.getAsText();
        });
    }
    
    @Test
    public void Double型への変換はサポートしてない() {
        assertThrows(RuntimeException.class, () -> {
            HalfwidthDecimalEditor editor = new HalfwidthDecimalEditor(Double.class, false);
            editor.setAsText("0.0");
            editor.getAsText();
        });
    }
}
