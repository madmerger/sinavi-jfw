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

package jp.co.ctc_g.jfw.core.consts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import jp.co.ctc_g.jfw.core.consts.LogLevel;

import org.junit.jupiter.api.Test;

public class LogLevelTest {

    @Test
    public void TRACEを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("TRACE"), is(LogLevel.TRACE));;
    }

    @Test
    public void DEBUGを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("DEBUG"), is(LogLevel.DEBUG));;
    }

    @Test
    public void INFOを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("INFO"), is(LogLevel.INFO));;
    }

    @Test
    public void WARNを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("WARN"), is(LogLevel.WARN));;
    }

    @Test
    public void ERRORを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("ERROR"), is(LogLevel.ERROR));;
    }

    @Test
    public void FATALを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("FATAL"), is(LogLevel.FATAL));;
    }

    @Test
    public void 無効なログレベルを指定してログレベルを取得する() throws Exception {
        assertThat(LogLevel.getLogLevel("ALERT"), is(LogLevel.UNSUPPORTED));;
    }
}
