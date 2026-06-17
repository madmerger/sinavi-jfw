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

package jp.co.ctc_g.jfw.profill.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PrincipalFilterTest {

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterConfig config;

    private MockFilterChain chain;

    private PrincipalStub principal;

    @BeforeEach
    public void setup() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        config = new MockFilterConfig();
        principal = new PrincipalStub("stub");
    }

    @Test
    public void Principalが設定されているときはPrincipalが取得できる() throws Exception {

        request.setUserPrincipal(principal);
        chain = new MockFilterChain() {

            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {

                Principal p = PrincipalKeeper.getPrincipal();
                assertThat(p, notNullValue());
                assertThat(p.getName(), is("stub"));
            };
        };
        PrincipalFilter filter = new PrincipalFilter();
        filter.init(config);
        filter.doFilter(request, response, chain);
        assertThat(PrincipalKeeper.getPrincipal(), nullValue());
        filter.destroy();
    }

}
