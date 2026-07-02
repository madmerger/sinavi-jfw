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

package jp.co.ctc_g.jse.core.token;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import jakarta.servlet.http.HttpServletRequest;

import jp.co.ctc_g.jse.core.framework.Controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class TokenManagerTest {

    private MockHttpServletRequest request;

    @Before
    public void setup() throws Exception {
        request = new MockHttpServletRequest();
    }

    @Test
    public void Tokenをセッションスコープに保存する()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        String generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(generatedToken, is(notNullValue()));
    }

    @Test
    public void Tokenをセッションスコープに保存し妥当性チェックがOK()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.setSessionTokenProcessor(new SessionTokenProcessorStub());
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        String generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        request.setAttribute(TokenManager.SESSION_TOKEN_PARAMETER_NAME, generatedToken);
        assertThat(manager.isTokenValid(request, Controllers.SCOPE_SESSION), is(true));
    }

    @Test
    public void Tokenをセッションスコープに保存し妥当性チェックがNG()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.setSessionTokenProcessor(new SessionTokenProcessorStub());
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        String generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        request.setAttribute(TokenManager.SESSION_TOKEN_PARAMETER_NAME, generatedToken);
        assertThat(manager.isTokenValid(request, Controllers.SCOPE_SESSION), is(false));
    }

    @Test
    public void セッションスコープに保存し妥当性チェック後にリセットしない()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.setSessionTokenProcessor(new SessionTokenProcessorStub());
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        String generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        request.setAttribute(TokenManager.SESSION_TOKEN_PARAMETER_NAME, generatedToken);
        assertThat(manager.isTokenValid(request, Controllers.SCOPE_SESSION, false), is(true));
        assertThat(generatedToken, is(manager.getToken(request, Controllers.SCOPE_SESSION)));
    }

    @Test
    public void IDGeneratorの実装を切り替える()
            throws Exception {
        
        TokenManager manager = new TokenManager();
        manager.setIdGenerator(new testIDGenerator());
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        String generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(generatedToken, is("0"));
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(generatedToken, is("1"));
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        generatedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(generatedToken, is("2"));
    }
    
    class SessionTokenProcessorStub implements TokenProcessor {

        private IDGenerator idGenerator = new IDGenerator.DefaultIdGenerator();
        private String token;

        @Override
        public boolean isTokenValid(HttpServletRequest request) {
            return token.equals(request.getAttribute(TokenManager.SESSION_TOKEN_PARAMETER_NAME));
        }

        @Override
        public boolean isTokenValid(HttpServletRequest request, boolean reset) {
            if (reset)
                token = null;
            return token.equals(request.getAttribute(TokenManager.SESSION_TOKEN_PARAMETER_NAME));
        }

        @Override
        public String getCurrentToken(HttpServletRequest request) {
            return token;
        }

        @Override
        public void resetToken(HttpServletRequest request) {
            token = null;
        }

        @Override
        public void saveToken(HttpServletRequest request) {
            token = idGenerator.generate();
        }
    }
    
    class testIDGenerator implements IDGenerator {

        private int counter = 0;
        
        @Override
        public String generate() {
            return String.valueOf(counter++);
        }
        
    }
}
