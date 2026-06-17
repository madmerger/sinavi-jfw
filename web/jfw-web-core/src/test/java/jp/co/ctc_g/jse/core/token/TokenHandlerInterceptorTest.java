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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import jp.co.ctc_g.jse.core.framework.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

public class TokenHandlerInterceptorTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerMethod tokenSaveHanlder;
    private HandlerMethod tokenCheckHanlder;
    private HandlerMethod tokenResetAfterCheck;
    private HandlerMethod nothing;
    
    @BeforeEach
    public void setup() throws Exception{
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        TokenHandlerInterceptorTestController controller = new TokenHandlerInterceptorTestController();
        tokenSaveHanlder = new HandlerMethod(controller, TokenHandlerInterceptorTestController.class.getMethod("tokenSave"));
        tokenCheckHanlder = new HandlerMethod(controller, TokenHandlerInterceptorTestController.class.getMethod("tokenCheck"));
        tokenResetAfterCheck = new HandlerMethod(controller, TokenHandlerInterceptorTestController.class.getMethod("tokenResetAfterCheck"));
        nothing = new HandlerMethod(controller, TokenHandlerInterceptorTestController.class.getMethod("nothing"));
    }

    @Test
    public void Tokenアノテーションを指定してsessionにトークンを保存する() throws Exception {
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
        Field managerField = tokenHandler.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(tokenHandler, manager);   
        tokenHandler.preHandle(request, response, tokenSaveHanlder);

        assertThat(manager.getToken(request, Controllers.SCOPE_SESSION), is(notNullValue()));
    }


    @Test
    public void Tokenアノテーションを指定してトークンのチェックを行う() throws Exception {
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
        Field managerField = tokenHandler.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(tokenHandler, manager);
        tokenHandler.preHandle(request, response, tokenSaveHanlder);
        String requestedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        request.setParameter(TokenManager.SESSION_TOKEN_PARAMETER_NAME, requestedToken);
        try {
            tokenHandler.preHandle(request, response, tokenCheckHanlder);
        } catch (InvalidTokenException e) {
            fail("トークンチェックに失敗");
        }
        assertThat(manager.getToken(request, Controllers.SCOPE_SESSION), is(nullValue()));
    }

    @Test
    public void Tokenアノテーションを指定してリセットをオフにしトークンのチェックを行う() throws Exception {
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
        Field managerField = tokenHandler.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(tokenHandler, manager);
        tokenHandler.preHandle(request, response, tokenSaveHanlder);
        String requestedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
        request.setParameter(TokenManager.SESSION_TOKEN_PARAMETER_NAME, requestedToken);
        try {
            tokenHandler.preHandle(request, response, tokenResetAfterCheck);
        } catch (InvalidTokenException e) {
            fail("トークンチェックに失敗");
        }
        assertThat(manager.getToken(request, Controllers.SCOPE_SESSION), is(notNullValue()));
    }

    @Test
    public void Tokenチェックに失敗する() throws Exception {
        assertThrows(InvalidTokenException.class, () -> {
            TokenManager manager = new TokenManager();
            manager.afterPropertiesSet();
    
            TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
            Field managerField = tokenHandler.getClass().getDeclaredField("manager");
            managerField.setAccessible(true);
            managerField.set(tokenHandler, manager);
            tokenHandler.preHandle(request, response, tokenSaveHanlder);
            String requestedToken = manager.getToken(request, Controllers.SCOPE_SESSION);
            request.setParameter(TokenManager.SESSION_TOKEN_PARAMETER_NAME, requestedToken);
            tokenHandler.preHandle(request, response, tokenSaveHanlder);
            tokenHandler.preHandle(request, response, tokenCheckHanlder);
        });
    }

    @Test
    public void Tokenアノテーションによって注釈されていない場合トークンが生成されない() throws Exception {
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
        Field managerField = tokenHandler.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(tokenHandler, manager);
        boolean result = tokenHandler.preHandle(request, response, nothing);
        assertThat(result, is(true));
        String token = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(token, is(nullValue()));
    }
    
    @Test
    public void ハンドラメソッド以外が実行された場合トークンが生成されない() throws Exception {
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenHandlerInterceptor tokenHandler = new TokenHandlerInterceptor();
        Field managerField = tokenHandler.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(tokenHandler, manager);
        boolean result = tokenHandler.preHandle(request, response, new Object());
        assertThat(result, is(true));
        String token = manager.getToken(request, Controllers.SCOPE_SESSION);
        assertThat(token, is(nullValue()));
    }
    
    class TokenHandlerInterceptorTestController {

        @Token(save = true)
        public String tokenSave() {
            return "tokenSave";
        }

        @Token(check = true)
        public String tokenCheck() {
            return "tokenCheck";
        }

        @Token(check = true, reset = false)
        public String tokenResetAfterCheck() {
            return "tokenResetAfterCheck";
        }
        
        public void nothing() {
        }
    }
}
