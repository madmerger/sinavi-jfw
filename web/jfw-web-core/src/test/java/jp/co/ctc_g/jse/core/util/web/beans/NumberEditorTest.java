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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.Test;


public class NumberEditorTest {

    @Test
    public void Byte型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Byte.class, format, pattern, false);
        editor.setAsText("127");
        assertThat(editor.getAsText(), is("127"));

        editor.setAsText("-128");
        assertThat(editor.getAsText(), is("-128"));
    }
    
    @Test
    public void Byte型へ変換時にエラーが発生() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Byte.class, format, pattern, false);
        try {
            editor.setAsText("128");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        try {
            editor.setAsText("-129");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }
    
    @Test
    public void Short型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Short.class, format, pattern, false);
        editor.setAsText("32767");
        assertThat(editor.getAsText(), is("32767"));
        
        editor.setAsText("-32768");
        assertThat(editor.getAsText(), is("-32768"));
    }
    
    @Test
    public void Short型へ変換時にエラーが発生() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Short.class, format, pattern, false);
        try {
            editor.setAsText("32768");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        try {
            editor.setAsText("-32769");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }
    
    @Test
    public void Integer型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Integer.class, format, pattern, false);
        editor.setAsText("2147483647");
        assertThat(editor.getAsText(), is("2147483647"));
        
        editor.setAsText("-2147483648");
        assertThat(editor.getAsText(), is("-2147483648"));
    }
    
    @Test
    public void Integer型へ変換時にエラーが発生() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Integer.class, format, pattern, false);
        try {
            editor.setAsText("2147483648");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        try {
            editor.setAsText("-2147483649");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }
    
    @Test
    public void Long型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Long.class, format, pattern, false);
        editor.setAsText("9223372036854775807");
        assertThat(editor.getAsText(), is("9223372036854775807"));
        
        editor.setAsText("-9223372036854775808");
        assertThat(editor.getAsText(), is("-9223372036854775808"));
    }
    
    @Test
    public void Long型へ変換時にエラーが発生() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(Long.class, format, pattern, false);
        try {
            editor.setAsText("9223372036854775808");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
        try {
            editor.setAsText("-9223372036854775809");
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("数値形式(^[-－]?([0０]|[1-9１-９][0-9０-９]*)$)で入力してください。"));
        }
    }
    
    @Test
    public void BigInteger型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###");
        String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
        NumberEditor editor = new NumberEditor(BigInteger.class, format, pattern, false);
        editor.setAsText("9223372036854775808");
        assertThat(editor.getAsText(), is("9223372036854775808"));
        
        editor.setAsText("-9223372036854775809");
        assertThat(editor.getAsText(), is("-9223372036854775809"));
    }
    
    @Test
    public void 小数3桁のカンマ付き半角数字がBigDecimal型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###,##0.000");
        String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
        NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, false);
        editor.setAsText("1,000.000");
        assertThat(editor.getAsText(), is("1,000.000"));
        
        editor.setAsText("1,234.567");
        assertThat(editor.getAsText(), is("1,234.567"));
        
        editor.setAsText("1,234,567.890");
        assertThat(editor.getAsText(), is("1,234,567.890"));
    }

    @Test
    public void 小数3桁のカンマ付き全角数字がBigDecimal型へ正常に変換される() {
        NumberFormat format = new DecimalFormat("###,##0.000");
        String pattern = "^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$";
        NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, false, true);
        editor.setAsText("１，０００．０００");
        assertThat(editor.getAsText(), is("1,000.000"));
        
        editor.setAsText("－１，２３４．５６７");
        assertThat(editor.getAsText(), is("-1,234.567"));
        
        editor.setAsText("１，２３４，５６７．８９０");
        assertThat(editor.getAsText(), is("1,234,567.890"));
    }

    @Test
    public void null値と空文字が正常が許可される() {
        NumberFormat format = new DecimalFormat("###,##0.000");
        String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
        NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, true);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
        
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void デフォルトのメッセージをオーバーライドできる() {
        NumberFormat format = new DecimalFormat("###,##0.000");
        String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
        NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, false, "override message");
        try {
            editor.setAsText("foo");
            fail();
        } catch(PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("override message"));
        }
    }

    @Test
    public void null値が許可されない() {
        assertThrows(PropertyEditingException.class, () -> {
            NumberFormat format = new DecimalFormat("###,##0.000");
            String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
            NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, false);
            editor.setAsText(null);
            fail();
        });
    }

    @Test
    public void 空文字が許可されない() {
        assertThrows(PropertyEditingException.class, () -> {
            NumberFormat format = new DecimalFormat("###,##0.000");
            String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
            NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, false);
            editor.setAsText("");
            fail();
        });
    }

    @Test
    public void 型指定が不正で初期化失敗() {
        assertThrows(InternalException.class, () -> {
            NumberFormat format = new DecimalFormat("###,##0.000");
            String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
            new NumberEditor(null, format, pattern, false);
            fail();
        });
    }
    
    @Test
    public void 型指定が不正で入力値でエラーが発生する() {
        assertThrows(IllegalArgumentException.class, () -> {
            
            @SuppressWarnings("serial")
            class NumberEx extends Number {
                @Override
                public int intValue() {
                    return 0;
                }
                @Override
                public long longValue() {
                    return 0;
                }
                @Override
                public float floatValue() {
                    return 0;
                }
                @Override
                public double doubleValue() {
                    return 0;
                }
            };
            
            NumberFormat format = new DecimalFormat("###");
            String pattern = "^[-－]?([0０]|[1-9１-９][0-9０-９]*)$";
            NumberEditor editor = new NumberEditor(NumberEx.class, format, pattern, false);
            editor.setAsText("9223372036854775808");
        });
    }

    @Test
    public void format指定が不正で初期化失敗() {
        assertThrows(InternalException.class, () -> {
            String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
            new NumberEditor(BigDecimal.class, null, pattern, false);
            fail();
        });
    }

    @Test
    public void pattern指定が不正で初期化失敗() {
        assertThrows(InternalException.class, () -> {
            NumberFormat format = new DecimalFormat("###,##0.000");
            new NumberEditor(BigDecimal.class, format, null, false);
            fail();
        });
    }

    @Test
    public void 不正な入力値でエラーが発生する() {
        assertThrows(PropertyEditingException.class, () -> {
            NumberFormat format = new DecimalFormat("###,##0.000");
            String pattern = "^[-]?([0-9]{0,3}([,][0-9]{3})*[.][0-9]+)$";
            NumberEditor editor = new NumberEditor(BigDecimal.class, format, pattern, true);
            editor.setAsText("１，０００．０００");
            fail();
        });
    }
}
