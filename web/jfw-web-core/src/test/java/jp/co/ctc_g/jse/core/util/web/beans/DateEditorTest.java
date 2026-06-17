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
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.experimental.theories.DataPoints;

import org.junit.experimental.theories.Theory;
// RunWith removed - use @ExtendWith or @Nested;


public class DateEditorTest {

    @DataPoints
    public static final String[] VALIDS = {
        "2011/12/22"
    };

    @DataPoints
    public static final String[] VALIDS_BLANK = {
        "", null
    };

    @DataPoints
    public static final String[] VALIDS_HYPHEN = {
        "2011-12-22"
    };

    @DataPoints
    public static final String[] INVALIDS = {
        "aaaa/aa/aa", "2010-12-22", "12-22-11", "2010/12/22<script>alert(xss)</script>", "2010/13/01", "2010/01/32",
        "2010/1/1", "2010/01/1", "2010/1/01", "2010/001/01"
    };

    @Theory
    public void バインドOKのテスト_コンストラクタ_デフォルト(String validation) {
        assumeThat(Arrays.asList(VALIDS), hasItem(validation));
        DateEditor editor = new DateEditor();
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_ブランク許可(String validation) {
        assumeThat(Arrays.asList(VALIDS), hasItem(validation));
        DateEditor editor = new DateEditor(true);
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_ブランク禁止(String validation) {
        assumeThat(Arrays.asList(VALIDS), hasItem(validation));
        DateEditor editor = new DateEditor(false);
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_ブランクOK(String validation) {
        assumeThat(Arrays.asList(VALIDS_BLANK), hasItem(validation));
        DateEditor editor = new DateEditor(true);
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(""));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_パターン指定_(String validation) {
        assumeThat(Arrays.asList(VALIDS_HYPHEN), hasItem(validation));
        DateEditor editor = new DateEditor(true, "yyyy-MM-dd");
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_日付の厳密なチェック指定(String validation) {
        assumeThat(Arrays.asList(VALIDS_HYPHEN), hasItem(validation));
        DateEditor editor = new DateEditor(true, true, "yyyy-MM-dd");
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory
    public void バインドOKのテスト_コンストラクタ_メッセージ指定(String validation) {
        assumeThat(Arrays.asList(VALIDS_HYPHEN), hasItem(validation));
        DateEditor editor = new DateEditor(true, true, "yyyy-MM-dd", "message");
        editor.setAsText(validation);
        assertThat(editor.getAsText(), is(validation));
    }

    @Theory()
    public void バインドNGのテスト(String validation) {
        assumeThat(Arrays.asList(INVALIDS), hasItem(validation));
        DateEditor editor = new DateEditor();
        try {
            editor.setAsText(validation);
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("日付の形式(yyyy/MM/dd)で入力してください。"));
        }
    }

    @Theory
    public void バインドNGのテスト_コンストラクタ_ブランク禁止(String validation) {
        assumeThat(Arrays.asList(VALIDS_BLANK), hasItem(validation));
        DateEditor editor = new DateEditor(false);
        AtomicInteger count = new AtomicInteger();
        try {
            editor.setAsText(validation);
        } catch (PropertyEditingException ex) {
            count.incrementAndGet();
        }
        assertThat(count.get(), is(1));
    }

    @Theory()
    public void バインドNGのテスト_メッセージオーバーライド(String validation) {
        assumeThat(Arrays.asList(INVALIDS), hasItem(validation));
        DateEditor editor = new DateEditor(true, true, "yyyy/MM/dd", "message");
        AtomicInteger count = new AtomicInteger();
        try {
            editor.setAsText(validation);
            fail();
        } catch (PropertyEditingException ex) {
            assertThat(ex.getMessage(), is("message"));
            count.incrementAndGet();
        }
        assertThat(count.get(), is(1));
    }

}
