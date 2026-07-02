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
import static org.junit.Assert.assertThat;

import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;

public class ControllerFqcnPrefixingSessionAttributeStoreTest {

    private MockHttpServletRequest request;
    private WebRequest webRequest;

    @Before
    public void setup() throws Exception {
        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        webRequest = new ServletWebRequest(request);
        RequestContextHolder.setRequestAttributes(webRequest);
        HandlerMethod handler = new HandlerMethod(new ControllerFqcnPrefixingSessionAttributeStoreTestController(),
            ControllerFqcnPrefixingSessionAttributeStoreTestController.class.getMethod("handlerMethod"));
        PostBackManager.begin(request, handler);
    }

    @Test
    public void コントローラのFQCN付き属性キーでセッションに保存() {
        ControllerFqcnPrefixingSessionAttributeStore store = new ControllerFqcnPrefixingSessionAttributeStore();
        store.storeAttribute(webRequest, "test", "test");
        HttpSession session = request.getSession();
        String s = (String) session.getAttribute(ControllerFqcnPrefixingSessionAttributeStoreTestController.class.getName() + ".test");
        assertThat(s, is("test"));
    }

    @Test
    public void コントローラのFQCN修飾なしの属性キーでセッションから取得() {
        ControllerFqcnPrefixingSessionAttributeStore store = new ControllerFqcnPrefixingSessionAttributeStore();
        store.storeAttribute(webRequest, "test", "test");

        String s = (String) store.retrieveAttribute(webRequest, "test");
        assertThat(s, is("test"));
    }

    @Test
    public void コントローラのFQCN修飾なしの属性キーでセッションから削除() {
        ControllerFqcnPrefixingSessionAttributeStore store = new ControllerFqcnPrefixingSessionAttributeStore();
        store.storeAttribute(webRequest, "test", "test");

        store.cleanupAttribute(webRequest, "test");

        HttpSession session = request.getSession();
        assertThat(session.getAttributeNames().hasMoreElements(), is(false));
    }

    @Controller
    class ControllerFqcnPrefixingSessionAttributeStoreTestController {

        @RequestMapping("/handlerMethod")
        public String handlerMethod() {
            return "handlerMethod";
        }
    }

}
