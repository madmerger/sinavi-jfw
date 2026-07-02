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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.ctc_g.jfw.core.util.Lists;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.Ordered;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

public class JseHandlerExceptionResolverCompositeTest {

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @Before
    public void setUp() {
        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        response = new MockHttpServletResponse();
    }

    @Test
    public void デフォルト状態でビューがnullになる() throws Exception {
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.afterPropertiesSet();

        ModelAndView mv = composite.resolveException(request, response, new Object(), new Exception());
        assertThat(mv, is(nullValue()));
    }

    @Test
    public void Orderを取得() throws Exception {
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.afterPropertiesSet();
        assertThat(composite.getOrder(), is(Ordered.HIGHEST_PRECEDENCE));
    }

    @Test
    public void Exceptionがエクスポーズされる() throws Exception {
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        JxHandlerExceptionResolver resolver = new JxHandlerExceptionResolver();
        composite.setCustomExceptionHandlers(Lists.gen((JseExceptionHandler)resolver));
        composite.afterPropertiesSet();
        HandlerMethod handler = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        ModelAndView mv = composite.resolveException(request, response, handler, new Exception());
        assertThat(mv, is(notNullValue()));
        assertThat(mv.getViewName(), is("/error"));
        assertThat(mv.getModel().get("exception"), instanceOf(Exception.class));
    }

    @Test
    public void Exceptionのキーを設定する() throws Exception {
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        JxHandlerExceptionResolver resolver = new JxHandlerExceptionResolver();
        composite.setCustomExceptionHandlers(Lists.gen((JseExceptionHandler)resolver));
        composite.setExceptionAttributeKey("modifiedExceptionAttributeKey");
        composite.afterPropertiesSet();
        HandlerMethod handler = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        ModelAndView mv = composite.resolveException(request, response, handler, new Exception());
        assertThat(composite.getExceptionAttributeKey(), is("modifiedExceptionAttributeKey"));
        assertThat(mv.getModel().get("modifiedExceptionAttributeKey"), instanceOf(Exception.class));
    }

    @Test
    public void ExceptionHandlerのリストを取得できる() throws Exception {
        List<JseExceptionHandler> exceptionResolvers = new ArrayList<JseExceptionHandler>();
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.setCustomExceptionHandlers(exceptionResolvers);
        composite.afterPropertiesSet();

        assertThat(composite.getExceptionHandlers().size(), is(1));
    }

    @Test
    public void デフォルトと追加したExceptionHandlerのリストを取得できる() throws Exception {
        List<JseExceptionHandler> exceptionResolvers = new ArrayList<JseExceptionHandler>();
        exceptionResolvers.add(new JxHandlerExceptionResolverLowest());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddle());
        exceptionResolvers.add(new JxHandlerExceptionResolverHighest());
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.setCustomExceptionHandlers(exceptionResolvers);
        composite.afterPropertiesSet();

        assertThat(composite.getExceptionHandlers().size(), is(4));
    }

    @Test
    public void 追加したExceptionHandlerのリストのみを取得できる() throws Exception {
        List<JseExceptionHandler> exceptionResolvers = new ArrayList<JseExceptionHandler>();
        exceptionResolvers.add(new JxHandlerExceptionResolverLowest());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddle());
        exceptionResolvers.add(new JxHandlerExceptionResolverHighest());
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.setCustomExceptionHandlers(exceptionResolvers);
        composite.afterPropertiesSet();

        assertThat(composite.getCustomExceptionHandlers().size(), is(3));
    }

    @Test
    public void JxHandlerExceptionResolverの順番が正しくソートされる() throws Exception {
        List<JseExceptionHandler> exceptionResolvers = new ArrayList<JseExceptionHandler>();
        exceptionResolvers.add(new JxHandlerExceptionResolverLowest());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddle());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddle2());
        exceptionResolvers.add(new JxHandlerExceptionResolverHighest());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddleLow());
        exceptionResolvers.add(new JxHandlerExceptionResolverMiddleHigh());
        JseHandlerExceptionResolverComposite composite = new JseHandlerExceptionResolverComposite();
        composite.setCustomExceptionHandlers(exceptionResolvers);
        composite.afterPropertiesSet();

        List<JseExceptionHandler> sortedExceptionResolvers = composite.getExceptionHandlers();
        assertThat(sortedExceptionResolvers.get(0), instanceOf(PostBackExceptionHandler.class));
        assertThat(sortedExceptionResolvers.get(1), instanceOf(JxHandlerExceptionResolverHighest.class));
        assertThat(sortedExceptionResolvers.get(2), instanceOf(JxHandlerExceptionResolverMiddleHigh.class));
        assertThat(sortedExceptionResolvers.get(3), instanceOf(JxHandlerExceptionResolverMiddle.class));
        assertThat(sortedExceptionResolvers.get(4), instanceOf(JxHandlerExceptionResolverMiddle2.class));
        assertThat(sortedExceptionResolvers.get(5), instanceOf(JxHandlerExceptionResolverMiddleLow.class));
        assertThat(sortedExceptionResolvers.get(6), instanceOf(JxHandlerExceptionResolverLowest.class));
    }

    class JxHandlerExceptionResolver extends AbstractJxHandlerExceptionResolver {
        @Override
        public ModelAndView resolveException(HttpServletRequest request,
                HttpServletResponse response, Object handler, Exception ex) {
            return new ModelAndView("/error");
        }
    }

    abstract class AbstractJxHandlerExceptionResolver implements JseExceptionHandler {
        @Override
        public ModelAndView resolveException(HttpServletRequest request,
                HttpServletResponse response, Object handler, Exception ex) {
            return null;
        }
        @Override
        public boolean supported(Object handler, Exception ex) {
            return true;
        }
        @Override
        public int getOrder() {
            return 0;
        }
        @Override
        public void logException(Exception ex, HttpServletRequest request) {
        }
    }

    class JxHandlerExceptionResolverLowest
            extends AbstractJxHandlerExceptionResolver {
        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    class JxHandlerExceptionResolverMiddleLow
    extends AbstractJxHandlerExceptionResolver {
        @Override
        public int getOrder() {
            return 50;
        }
    }

    class JxHandlerExceptionResolverMiddle
            extends AbstractJxHandlerExceptionResolver {
    }

    class JxHandlerExceptionResolverMiddle2
            extends AbstractJxHandlerExceptionResolver {
    }

    class JxHandlerExceptionResolverMiddleHigh extends
            AbstractJxHandlerExceptionResolver {
        @Override
        public int getOrder() {
            return -50;
        }
    }

    class JxHandlerExceptionResolverHighest extends
    AbstractJxHandlerExceptionResolver {
        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
    
    @Controller
    class TestController {
        @RequestMapping
        public String test() {
            return "test";
        }
    }
}
