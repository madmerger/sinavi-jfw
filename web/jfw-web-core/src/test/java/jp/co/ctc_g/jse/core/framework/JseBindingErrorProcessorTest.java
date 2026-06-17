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

package jp.co.ctc_g.jse.core.framework;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.beans.PropertyChangeEvent;

import jp.co.ctc_g.jse.core.util.web.beans.PropertyEditingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MethodInvocationException;
import org.springframework.validation.BeanPropertyBindingResult;

public class JseBindingErrorProcessorTest {

    private JseBindingErrorProcessor processor;

    @BeforeEach
    public void setup() {
        processor = new JseBindingErrorProcessor();
    }

    @Test
    public void PropertyEditingExceptionの場合にフィールドエラーが設定される() {
        class T {
        }
        MethodInvocationException mie = new MethodInvocationException(new PropertyChangeEvent(new T(), "aaaa", null, null),
            new PropertyEditingException("message"));
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new T(), "Test");
        processor.processPropertyAccessException(mie, result);
        assertThat(result.getFieldError(), notNullValue());
    }
}
