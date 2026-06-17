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

package jp.co.ctc_g.jfw.core.jdbc.mybatis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultLiteralConvertorTest {

    private DefaultLiteralConvertor target;

    @BeforeEach
    public void instantiate() {
        target = new DefaultLiteralConvertor();
    }

    @Test
    public void Dateの値をリテラルに変換する() throws Exception {
        Boolean b = true;
        assertThat(target.convert(b), is("true"));
    }

    @Test
    public void NULLリテラルに変換する() throws Exception {
        assertThat(target.convert(null), is("null"));
    }
}
