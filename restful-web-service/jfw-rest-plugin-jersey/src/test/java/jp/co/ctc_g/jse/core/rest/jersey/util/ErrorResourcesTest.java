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

package jp.co.ctc_g.jse.core.rest.jersey.util;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.CoreMatchers;

import java.util.Locale;

// hamcrest removed;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ErrorResourcesTest {

    @BeforeAll
    public static void setup() {
        Locale.setDefault(new Locale("ja","JP"));
    }

    @Test
    public void キーが見つからない場合はキーがメッセージになる() {
        String msg = ErrorResources.find("E-HOGEHOGE#001");
        assertThat(msg, CoreMatchers.is("E-HOGEHOGE#001"));
    }

    @Test
    public void キーがNULLもしくは空文字列の場合はキーがメッセージになる() {
        String msg = ErrorResources.find("");
        assertThat(msg, CoreMatchers.is(""));
        msg = ErrorResources.find(null);
        assertThat(msg, CoreMatchers.nullValue());
    }

    @Test
    public void プロパティファイルがあるロケールが指定された場合はロケールに対応したメッセージが取得できる() {
        String msg = ErrorResources.find("W-REST-CLIENT#400", Locale.ENGLISH);
        assertThat(msg, CoreMatchers.is("Bad Request"));
    }

    @Test
    public void プロパティファイルがないロケールが指定された場合はデフォルトロケールでメッセージが取得できる() {
        String msg = ErrorResources.find("W-REST-CLIENT#400", Locale.FRANCE);
        assertThat(msg, CoreMatchers.is("不正なリクエストです。"));
    }
}
