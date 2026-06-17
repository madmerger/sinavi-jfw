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

import jp.co.ctc_g.jse.core.framework.PostBackExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

public class PostBackExceptionHandlerTest {

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
        PostBackExceptionHandler handler = new PostBackExceptionHandler();
        assertThat(handler.getOrder(), is(Ordered.HIGHEST_PRECEDENCE));
    }

    @Test
    public void サポートしない例外のチェック() throws Exception {
        PostBackExceptionHandler handler = new PostBackExceptionHandler();
        boolean supported = handler.supported(new Object(), new Exception());
        assertThat(supported, is(false));
    }

    @Test
    public void サポートしする例外のチェック() throws Exception {
        PostBackExceptionHandler exceptionHandler = new PostBackExceptionHandler();
        PostBackManager.begin(request, handler);
        boolean supported = exceptionHandler.supported(handler, new BindException(new Object(), "test"));
        assertThat(supported, is(true));
    }

    @Test
    public void 例外のハンドリング() throws Exception {
        PostBackExceptionHandler exceptionHandler = new PostBackExceptionHandler();
        PostBackManager.begin(request, handler);
        ModelAndView mav = exceptionHandler.resolveException(request, response, handler, new BindException(new Object(), "test"));
        assertThat(mav.getViewName(), is("forward:/test"));
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
