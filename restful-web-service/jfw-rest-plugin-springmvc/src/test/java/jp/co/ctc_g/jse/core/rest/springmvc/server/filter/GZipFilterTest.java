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

package jp.co.ctc_g.jse.core.rest.springmvc.server.filter;

import static org.junit.Assert.assertTrue;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class GZipFilterTest {

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterConfig config;

    private MockFilterChain chain;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        config = new MockFilterConfig();
    }

    @Test
    public void ヘッダーにaccept_encodingが設定されていないときはHttpServletResponseがくる() throws Exception {
        chain = new MockFilterChain() {

            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                assertTrue(res instanceof HttpServletResponse);
            };
        };
        GZipFilter filter = new GZipFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        filter.destroy();
    }

    @Test
    public void ヘッダーにaccept_encodingが設定されているときは拡張されたGZipResponseWrapperがくる() throws Exception {
        request.addHeader("accept-encoding", "gzip");
        chain = new MockFilterChain() {

            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                assertTrue(res instanceof GZipResponseWrapper);
            };
        };
        GZipFilter filter = new GZipFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        filter.destroy();
    }
}
