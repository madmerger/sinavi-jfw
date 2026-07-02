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
import jakarta.servlet.http.HttpSession;

import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Strings;

/**
 * <p>
 * セッションスコープでトークンの制御を行うための{@link TokenProcessor}の実装クラスです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see TokenProcessor
 * @see AbstractTokenProcessor
 * @see IDGenerator
 * @see TokenManager
 */
public class SessionTokenProcessor extends AbstractTokenProcessor implements TokenProcessor {

    /**
     * デフォルトコンストラクタです。
     */
    public SessionTokenProcessor() {}

    /**
     * コンストラクタです。<br/>
     * @param idGenerator トークンIDの生成器である{@link IDGenerator}のインスタンス
     */
    public SessionTokenProcessor(IDGenerator idGenerator) {
        super(idGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(HttpServletRequest request) {
        return isTokenValid(request, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(HttpServletRequest request, boolean reset) {
        Args.checkNotNull(request);
        HttpSession session = request.getSession();
        String savedToken = (String)session.getAttribute(TokenManager.TOKEN_ATTRIBUTE_KEY);
        String requestedToken = (String)request.getParameter(TokenManager.SESSION_TOKEN_PARAMETER_NAME);
        if (reset) {
            resetToken(request);
        }
        if (Strings.isEmpty(savedToken) ||
                Strings.isEmpty(requestedToken) ||
                !savedToken.equals(requestedToken)) {
            return false;
        }
        return savedToken.equals(requestedToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentToken(HttpServletRequest request) {
        Args.checkNotNull(request);
        return (String)request.getSession().getAttribute(TokenManager.TOKEN_ATTRIBUTE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetToken(HttpServletRequest request) {
        Args.checkNotNull(request);
        request.getSession().removeAttribute(TokenManager.TOKEN_ATTRIBUTE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToken(HttpServletRequest request) {
        Args.checkNotNull(request);
        request.getSession().setAttribute(TokenManager.TOKEN_ATTRIBUTE_KEY, idGenerator.generate());
    }

}