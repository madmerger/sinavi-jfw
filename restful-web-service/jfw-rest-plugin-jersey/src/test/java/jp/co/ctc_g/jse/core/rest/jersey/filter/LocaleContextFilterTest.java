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

package jp.co.ctc_g.jse.core.rest.jersey.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import jp.co.ctc_g.jse.core.rest.jersey.i18n.LocaleContextKeeper;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocaleContextFilterTest extends JerseyTest {
    
    private AtomicInteger called;
    private Locale locale = null;
    
    @BeforeEach
    public void setup() {
        called = new AtomicInteger(0);
        locale = null;
    }

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        ResourceConfig config = new ResourceConfig();
        config.register(LocaleContextFilter.class, 1);
        config.register(new ContainerRequestFilter() {

            @Override
            public void filter(ContainerRequestContext requestContext) throws IOException {
                called.incrementAndGet();
                locale = LocaleContextKeeper.getLocale();
            }

        }, 2);
        config.register(TestResource.class);
        return config;
    }
    
    @Test
    public void Httpヘッダに登録されている場合はその値がスレッドローカルに設定される() {
        Response response = target("test").request().header("Accept-Language", "en").get();
        assertThat(response.getStatus(), is(200));
        assertThat(locale, notNullValue());
        assertThat(locale.getLanguage(), is("en"));
    }
    
    @Test
    public void Httpヘッダに登録されていない場合はデフォルト値がスレッドローカルに設定される() {
        Response response = target("test").request().get();
        assertThat(response.getStatus(), is(200));
        assertThat(locale, notNullValue());
        assertThat(locale.getLanguage(), is(Locale.getDefault().getLanguage()));
    }

}
