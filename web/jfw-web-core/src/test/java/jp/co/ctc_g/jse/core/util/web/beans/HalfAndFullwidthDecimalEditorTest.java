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

public class HalfAndFullwidthDecimalEditorTest {
        
    @Test
    public void BigDecimal型へ正常に変換される() {
        HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(BigDecimal.class, false);
        editor.setAsText("０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("０．００");
        assertThat(editor.getAsText(), is("0.00"));
        
        editor.setAsText("０．０００");
        assertThat(editor.getAsText(), is("0.000"));
        
        editor.setAsText("－０．０");
        assertThat(editor.getAsText(), is("0.0"));
        
        editor.setAsText("１００２００３０１．００１");
        assertThat(editor.getAsText(), is("100200301.001"));
        
        editor.setAsText("１００２００３００．００１");
        assertThat(editor.getAsText(), is("100200300.001"));
        
        editor.setAsText("－１００２００３００．００１");
        assertThat(editor.getAsText(), is("-100200300.001"));
        
        editor.setAsText("０．００００００００００００００００００００００００００００００００００００００００００００００４９");
        assertThat(editor.getAsText(), is("0.000000000000000000000000000000000000000000000049"));
        
        editor.setAsText("１７９７６９３１３４８６２３１５７００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００００．０");
        assertThat(editor.getAsText(), is("179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0"));
    }
    
    @Test
    public void BigDecimal型へ変換時にエラーが発生する() {
        HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(BigDecimal.class, false);
        try {
            editor.setAsText("１");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0-9０-９]+[.．][0-9０-９]+)$)で入力してください。"));
        }
    }
    
    @Test
    public void null値と空文字が正常が許可される() {
        HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(BigDecimal.class, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(BigDecimal.class, false, "override message");
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
            new HalfAndFullwidthDecimalEditor(null, false);
            fail();
        });
    }
    
    @Test
    public void Float型への変換はサポートしていない() {
        assertThrows(RuntimeException.class, () -> {
            HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(Float.class, false);
            editor.setAsText("０．０");
            editor.getAsText();
        });
    }
    
    @Test
    public void Double型への変換はサポートしてない() {
        assertThrows(RuntimeException.class, () -> {
            HalfAndFullwidthDecimalEditor editor = new HalfAndFullwidthDecimalEditor(Double.class, false);
            editor.setAsText("０．０");
            editor.getAsText();
        });
    }
}
