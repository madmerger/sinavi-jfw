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

package jp.co.ctc_g.jse.vid;

import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jse.vid.InvalidViewTransitionException;
import jp.co.ctc_g.jse.vid.ViewId;
import jp.co.ctc_g.jse.vid.ViewIdConstraint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

public class ViewIdConstraintHandlerInterceptorTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void 制約設定されていないハンドラメソッドで画面遷移が許可される() throws Exception {
        HandlerMethod handler = new HandlerMethod(TestController.class, TestController.class.getMethod("handler1"));
        ViewId viewId1 = ViewIdGen.gen("view1");
        ViewId.is(viewId1, request);
        ViewIdConstraintHandlerInterceptor interceptor = new ViewIdConstraintHandlerInterceptor();
        interceptor.preHandle(request, response, handler);
    }

    @Test
    public void 画面遷移が許可される() throws Exception {
        HandlerMethod handler = new HandlerMethod(TestController.class, TestController.class.getMethod("handler2"));
        ViewId viewId1 = ViewIdGen.gen("view1");
        ViewId.is(viewId1, request);
        ViewIdConstraintHandlerInterceptor interceptor = new ViewIdConstraintHandlerInterceptor();
        interceptor.preHandle(request, response, handler);
    }

    @Test
    public void 画面遷移が拒否される() throws Exception {
        assertThrows(InvalidViewTransitionException.class, () -> {
            HandlerMethod handler = new HandlerMethod(TestController.class, TestController.class.getMethod("handler4"));
            ViewId viewId1 = ViewIdGen.gen("view1");
            ViewId.is(viewId1, request);
            ViewId viewId2 = ViewIdGen.gen("view2");
            ViewId.is(viewId2, request);
            ViewId viewId3 = ViewIdGen.gen("view3");
            ViewId.is(viewId3, request);
            ViewIdConstraintHandlerInterceptor interceptor = new ViewIdConstraintHandlerInterceptor();
            interceptor.preHandle(request, response, handler);
        });
    }

    @Test
    public void 画面IDが設定されていない画面からの遷移で画面ID制約がチェックできない() throws Exception {
        assertThrows(InternalException.class, () -> {
            HandlerMethod handler = new HandlerMethod(TestController.class, TestController.class.getMethod("handler2"));
    
            ViewIdConstraintHandlerInterceptor interceptor = new ViewIdConstraintHandlerInterceptor();
            interceptor.preHandle(request, response, handler);
        });
    }

    @Test
    public void 不正なスコープが指定され内部エラーが発生() throws Exception {
        assertThrows(InternalException.class, () -> {
            HandlerMethod handler = new HandlerMethod(TestController.class, TestController.class.getMethod("handler3"));
    
            ViewIdConstraintHandlerInterceptor interceptor = new ViewIdConstraintHandlerInterceptor();
            interceptor.preHandle(request, response, handler);
        });
    }

    static class ViewIdGen extends ViewId {
        private static final long serialVersionUID = 1L;
        protected ViewIdGen(String id) {
            super(id);
        }
        public static ViewId gen(String id) {
            return new ViewIdGen(id);
        }
    }
    

    @Controller
    class TestController {
        
        @RequestMapping
        public String handler1() {
            return "handler1";
        }
        
        @ViewIdConstraint(allow = "view1")
        @RequestMapping
        public String handler2() {
            return "handler2";
        }
        
        @ViewIdConstraint(allow = "view2", scope = "conversation")
        @RequestMapping
        public String handler3() {
            return "handler3";
        }
        
        @ViewIdConstraint(allow = "view1|view2")
        @RequestMapping
        public String handler4() {
            return "handler4";
        }
    }
}
