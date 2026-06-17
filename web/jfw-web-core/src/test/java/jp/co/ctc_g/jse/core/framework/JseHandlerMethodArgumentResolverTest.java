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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.BindException;

import jp.co.ctc_g.jse.core.message.MessageContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;

public class JseHandlerMethodArgumentResolverTest {

    private MockHttpServletRequest request;
    private NativeWebRequest webRequest;
    private WebDataBinderFactory factory;

    @BeforeEach
    public void setUp() throws Exception {

        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        webRequest = new ServletWebRequest(request);
        RequestContextHolder.setRequestAttributes(webRequest);
        request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
        factory = new DefaultDataBinderFactory(new ConfigurableWebBindingInitializer());
    }

    @Test
    public void 初期化が正常に行われる() throws Exception {
        JseHandlerMethodArgumentResolver argumentResolver = new JseHandlerMethodArgumentResolver();
        argumentResolver.afterPropertiesSet();

        MethodParameter mp = new MethodParameter(TestController.class.getMethod("postBackArg", PostBack.class), 0);
        MethodParameter mm = new MethodParameter(TestController.class.getMethod("messageContextArg", MessageContext.class), 0);
        assertThat(argumentResolver.supportsParameter(mp), is(true));
        assertThat(argumentResolver.supportsParameter(mm), is(true));
    }

    @Test
    public void 例外未発生時の引数PostBackの解決を行う() throws Exception {
        RequestContextHolder.getRequestAttributes().setAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, new PostBack(new BindException()),
                                                                 RequestAttributes.SCOPE_REQUEST);
        JseHandlerMethodArgumentResolver argumentResolver = new JseHandlerMethodArgumentResolver();
        argumentResolver.afterPropertiesSet();
        MethodParameter mp = new MethodParameter(TestController.class.getMethod("postBackArg", PostBack.class), 0);
        Object o = argumentResolver.resolveArgument(mp, new ModelAndViewContainer(), webRequest, factory);
        assertThat(o.getClass().getName(), is(PostBack.class.getName()));
        PostBack p = (PostBack) o;
        assertThat(p.isPostBackRequest(), is(true));
        assertThat(p.getType().getName(), is(BindException.class.getName()));
        assertThat(p.getModel(), is(nullValue()));
        assertThat(p.getModelName(), is(nullValue()));
    }

    class TestController {

        public void postBackArg(PostBack postBack) {}

        public void messageContextArg(MessageContext m) {}

    }
}
