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

package jp.co.ctc_g.jse.core.amqp.retry;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;

import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationRecoverableException;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationUnrecoverableException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

public class LoggingErrorHandlerTest {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private PrintStream original;

    @BeforeEach
    public void readyBuffer() {
        original = System.out;
        System.setOut(new PrintStream(buffer));
    }

    @AfterEach
    public void resetBuffer() {
        buffer.reset();
        System.setOut(original);
    }

    @Test
    public void エラーログが出力される() throws IOException {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.handleError(new ListenerExecutionFailedException("amqp failed", new RuntimeException("runtime exception")));
        buffer.flush();
        String log = buffer.toString();
        assertThat(log, is(containsString("ERROR")));
        assertThat(log, is(containsString("runtime exception")));
    }

    @Test
    public void リトライ対象外の例外のときにエラーログが出力される() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.setRetryableExceptions(Collections.<Class<? extends Throwable>, Boolean> singletonMap(AmqpApplicationRecoverableException.class, true));
        handler.handleError(new ListenerExecutionFailedException("amqp failed", new AmqpApplicationUnrecoverableException("error log test")));
        String log = buffer.toString();
        assertThat(log, is(containsString("ERROR")));
        assertThat(log, is(containsString("error log test")));
    }

    @Test
    public void リトライ対象の例外のときにワーニングログが出力される() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.setRetryableExceptions(Collections.<Class<? extends Throwable>, Boolean> singletonMap(AmqpApplicationRecoverableException.class, true));
        handler.handleError(new ListenerExecutionFailedException("amqp failed", new AmqpApplicationRecoverableException("warn log test")));
        String log = buffer.toString();
        assertThat(log, is(containsString("WARN")));
        assertThat(log, is(containsString("warn log test")));
    }
}
