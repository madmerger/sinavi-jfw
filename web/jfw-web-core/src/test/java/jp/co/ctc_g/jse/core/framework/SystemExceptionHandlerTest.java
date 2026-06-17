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
import static org.hamcrest.MatcherAssert.assertThat;

import jp.co.ctc_g.jfw.core.exception.SystemException;
import jp.co.ctc_g.jse.core.framework.SystemExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

public class SystemExceptionHandlerTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerMethod handler;

    @BeforeEach
    public void setup() throws Exception{
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        TestController controller = new TestController();
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
        handler = new HandlerMethod(controller, TestController.class.getMethod("test"));
    }

    @Test
    public void Orderを取得() throws Exception {
        SystemExceptionHandler handler = new SystemExceptionHandler();
        assertThat(handler.getOrder(), is(Ordered.LOWEST_PRECEDENCE));
    }

    @Test
    public void サポートしする例外のチェック() throws Exception {
        SystemExceptionHandler exceptionHandler = new SystemExceptionHandler();
        boolean supported = exceptionHandler.supported(handler, new SystemException("test"));
        assertThat(supported, is(true));
    }

    @Test
    public void 例外のハンドリング() throws Exception {
        SystemExceptionHandler exceptionHandler = new SystemExceptionHandler();
        exceptionHandler.setViewName("error/error");
        ModelAndView mav = exceptionHandler.resolveException(request, response, handler, new SystemException("test"));
        assertThat(mav.getViewName(), is("error/error"));
    }

    @Test
    public void JFW定義例外以外の例外のハンドリング() throws Exception {
        SystemExceptionHandler exceptionHandler = new SystemExceptionHandler();
        exceptionHandler.setViewName("error/error");
        ModelAndView mav = exceptionHandler.resolveException(request, response, handler, new IllegalArgumentException("test"));
        assertThat(mav.getViewName(), is("error/error"));
    }
    
    @Controller
    class TestController {

        @PostBack.Action(Controllers.FORWARD + "/test")
        @RequestMapping
        public String test() {
            return "test";
        }
    }
}
