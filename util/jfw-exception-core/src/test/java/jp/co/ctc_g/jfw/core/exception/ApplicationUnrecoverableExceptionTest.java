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

package jp.co.ctc_g.jfw.core.exception;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;
import jp.co.ctc_g.jfw.core.util.Maps;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ApplicationUnrecoverableExceptionTest {

    @BeforeAll
    public static void setup() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/jp/co/ctc_g/jfw/core/exception/error");
        MessageSourceLocator.set(messageSource);
    }

    @Test
    public void instatiate() {

        AbstractException e1 = new ApplicationUnrecoverableException("E-UNREC#0001");
        AbstractException e2 = new ApplicationUnrecoverableException("E-UNREC#0002", Maps.hash("key", "value"));
        AbstractException e3 = new ApplicationUnrecoverableException("E-UNREC#0003", e1);
        AbstractException e4 = new ApplicationUnrecoverableException("E-UNREC#0004", e1, Maps.hash("key", "value"));

        assertThat(e1, is(instanceOf(ApplicationUnrecoverableException.class)));
        assertThat(e2, is(instanceOf(ApplicationUnrecoverableException.class)));
        assertThat(e3, is(instanceOf(ApplicationUnrecoverableException.class)));
        assertThat(e4, is(instanceOf(ApplicationUnrecoverableException.class)));

        assertThat(e1.getMessage(), is("ApplicationUnrecoverableException:1"));
        assertThat(e2.getMessage(), is("ApplicationUnrecoverableException:2,value"));
        assertThat(e3.getMessage(), is("ApplicationUnrecoverableException:3"));
        assertThat(e4.getMessage(), is("ApplicationUnrecoverableException:4,value"));

        assertThat(e1.getId().length(), is(36));
        assertThat(e2.getId().length(), is(36));
        assertThat(e3.getId().length(), is(36));
        assertThat(e4.getId().length(), is(36));

        assertThat(e1.getCode(), is("E-UNREC#0001"));
        assertThat(e2.getCode(), is("E-UNREC#0002"));
        assertThat(e3.getCode(), is("E-UNREC#0003"));
        assertThat(e4.getCode(), is("E-UNREC#0004"));

        assertThat(e1.getDate(), is(instanceOf(Date.class)));
        assertThat(e2.getDate(), is(instanceOf(Date.class)));
        assertThat(e3.getDate(), is(instanceOf(Date.class)));
        assertThat(e4.getDate(), is(instanceOf(Date.class)));
    }
}
