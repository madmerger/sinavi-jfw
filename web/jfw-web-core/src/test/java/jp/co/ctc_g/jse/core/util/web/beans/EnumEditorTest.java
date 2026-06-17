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
import org.junit.jupiter.api.Test;

public class EnumEditorTest {

    @Test
    public void null値の変換() {
        EnumEditor editor = new EnumEditor(TestEnum.class);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));

        editor = new EnumEditor(true, TestEnum.class);
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
    }

    @Test
    public void null値の変換を許可しない() {
        try {
            EnumEditor editor = new EnumEditor(false, TestEnum.class);
            editor.setAsText(null);
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("必須入力です。"));
        }
    }

    @Test
    public void String2Enum変換() {
        EnumEditor editor = new EnumEditor(false, TestEnum.class);
        editor.setAsText("OK");
        assertThat(editor.getAsText(), is("OK"));
        editor.setAsText("NG");
        assertThat(editor.getAsText(), is("NG"));
    }

    @Test
    public void バインドできないときはとエラーが発生する() {
        try {
            EnumEditor editor = new EnumEditor(false, TestEnum.class);
            editor.setAsText("BAD");
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("次の形式(OK,NG)のどれかで入力して下さい。"));
        }
    }
    
    @Test
    public void デフォルトメッセージをオーバーライドできる() {
        try {
            EnumEditor editor = new EnumEditor(false, TestEnum.class, "override message");
            editor.setAsText("BAD");
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("override message"));
        }
    }

    private enum TestEnum {
        OK, NG;
    }
}