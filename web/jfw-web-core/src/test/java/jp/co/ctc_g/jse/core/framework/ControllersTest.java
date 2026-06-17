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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.method.HandlerMethod;

public class ControllersTest {

    @Test
    public void requestスコープの文字列表現が取得できる() {
        assertThat(Controllers.SCOPE_REQUEST, is("request"));
    }
    
    @Test
    public void sessionスコープの文字列表現が取得できる() {
        assertThat(Controllers.SCOPE_SESSION, is("session"));
    }
       
    @Test
    public void リダイレクト時のパスプリフィックスが取得できる() {
        assertThat(Controllers.REDIRECT, is("redirect:"));
    }
    
    @Test
    public void フォワード時のパスプリフィックスが取得できる() {
        assertThat(Controllers.FORWARD, is("forward:"));
    }

    @Test
    public void HandlerMethodインスタンスであることを判定() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("test"));
        assertThat(Controllers.isHandlerMethod(handler), is(true));
    }

    @Test
    public void HandlerMethodインスタンスでないことを判定() {
        assertThat(Controllers.isHandlerMethod(""), is(false));
    }

    @Test
    public void null値がHandlerMethodインスタンスでないことを判定() {
        assertThat(Controllers.isHandlerMethod(null), is(false));
    }

    @Test
    public void HandlerMethodインスタンスにSessionAttributeComplete注釈がついていることを判定() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("sessionAttributeComplete"));
        assertThat(Controllers.isSessionAttributeComplete(handler.getMethod()), is(true));
    }

    @Test
    public void HandlerMethodインスタンスにSessionAttributeComplete注釈がついていないことを判定() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("test"));
        assertThat(Controllers.isSessionAttributeComplete(handler.getMethod()), is(false));
    }

    @Test
    public void HandlerMethodインスタンスに付与されているSessionAttributeComplete注釈のインスタンスを取得() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("sessionAttributeComplete"));
        assertThat(Controllers.findSessionAttributeCompleteAnnotation(handler.getMethod()), is(notNullValue()));
    }

    @Test
    public void HandlerMethodインスタンスに付与されていないメソッドを対象SessionAttributeComplete注釈のインスタンスを取得した場合nullが返却される() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("test"));
        assertThat(Controllers.findSessionAttributeCompleteAnnotation(handler.getMethod()), is(nullValue()));
    }

    @Test
    public void HandlerMethodインスタンスに付与されているSessionAttributeComplete注釈のvalue属性を取得() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("sessionAttributeComplete"));
        assertThat(Arrays.asList(Controllers.findClearSessionAttributeNames(handler.getMethod())), hasItem("modelAttribute"));
    }

    @Test
    public void HandlerMethodインスタンスに付与されていないメソッドを対象SessionAttributeComplete注釈のvalue属性を取得した場合nullが返却される() throws Exception {
        HandlerMethod handler = new HandlerMethod(new ControllersTestController(), ControllersTestController.class.getMethod("test"));
        assertThat(Controllers.findClearSessionAttributeNames(handler.getMethod()), is(nullValue()));
    }

    @Test
    public void HandlerMethodインスタンスに付与されているSessionAttributes注釈のインスタンスを取得() throws Exception {
        assertThat(Arrays.asList(Controllers.findSessionAttributeNames(ControllersTestController.class)), hasItem("mdoelAttribute"));
    }

    @Test
    public void HandlerMethodインスタンスに付与されていないSessionAttributes注釈のインスタンスを取得した場合nullが返却される() throws Exception {
        assertThat(Controllers.findSessionAttributeNames(NonSessionAttributesTestController.class), is(nullValue()));
    }

    @Test
    public void exceptionのタイプがマッチする() throws Exception {
        assertThat(Controllers.exceptionMatch(Exception.class, Exception.class), is(true));
    }

    @Test
    public void exceptionのタイプがマッチしない() throws Exception {
        assertThat(Controllers.exceptionMatch(Exception.class, IllegalArgumentException.class), is(false));
    }

    @Controller
    @SessionAttributes("mdoelAttribute")
    class ControllersTestController {

        @RequestMapping
        public String test() {
            return "test";
        }

        @SessionAttributeComplete("modelAttribute")
        public String sessionAttributeComplete() {
            return "SessionAttributeComplete";
        }
    }

    @Controller
    class NonSessionAttributesTestController {
        
    }
}
