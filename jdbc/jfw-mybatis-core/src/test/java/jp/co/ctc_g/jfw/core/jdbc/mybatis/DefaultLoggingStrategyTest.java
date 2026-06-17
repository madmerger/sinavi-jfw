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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultLoggingStrategyTest {

    private LoggingStrategy strategy;

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private PrintStream original;

    @BeforeEach
    public void setup() {
        original = System.out;
        System.setOut(new PrintStream(buffer));
    }

    @AfterEach
    public void teardown() {
        buffer.reset();
        System.setOut(original);
    }

    @Test
    public void デフォルトのログレベルで出力される() {
        strategy = new DefaultLoggingStrategy();
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("INFO select 1 from dual;"));
    }

    @Test
    public void traceレベルで出力される() {
        strategy = new DefaultLoggingStrategy("TRACE");
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("TRACE select 1 from dual;"));
    }

    @Test
    public void debugレベルで出力される() {
        strategy = new DefaultLoggingStrategy("DEBUG");
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("DEBUG select 1 from dual;"));
    }
    
    @Test
    public void warnレベルで出力される() {
        strategy = new DefaultLoggingStrategy("WARN");
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("WARN select 1 from dual;"));
    }
    
    @Test
    public void errorレベルで出力される() {
        strategy = new DefaultLoggingStrategy("ERROR");
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("ERROR select 1 from dual;"));
    }
    
    @Test
    public void faitalレベルで出力される() {
        strategy = new DefaultLoggingStrategy("FATAL");
        strategy.log("select 1 from dual;");
        assertThat(new String(buffer.toByteArray()), containsString("ERROR select 1 from dual;"));
    }

}
