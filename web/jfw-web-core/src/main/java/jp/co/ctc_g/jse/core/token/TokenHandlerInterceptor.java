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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * <p>
 * このクラスは{@link org.springframework.stereotype.Controller}によって注釈されたハンドラ・メソッドの呼び出しをインターセプトし、要求されたハンドラ・メソッドに付加された{@link Token}注釈
 * に従ってトークンの制御を行います。<br/>
 * 主な役割はトークンの制御処理をインターセプタとして提供することであり、トークンの保存やチェックといった制御処理自体は{@link TokenManager}に委譲します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see TokenManager
 */
public class TokenHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenManager manager;

    /**
     * デフォルトコンストラクタです。
     */
    public TokenHandlerInterceptor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            internalPreProcess((HandlerMethod) handler, request);
            return true; // トークンエラー発生は InvalidTokenException をスローすることによって通知するため常にtrueを返却する。
        } else {
            return true;
        }
    }

    private void internalPreProcess(HandlerMethod handlerMethod, HttpServletRequest request) {
        Token token = handlerMethod.getMethodAnnotation(Token.class);
        if (token != null) {
            if (token.check()) {
                if (!manager.isTokenValid(request, token.scope(), token.reset())) { throw new InvalidTokenException(); }
            }
            if (token.save()) {
                manager.saveToken(request, token.scope());
            }
        }
    }
}
