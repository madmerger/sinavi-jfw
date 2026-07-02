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

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.ctc_g.jfw.core.exception.AbstractException;
import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * {@link JseExceptionHandler}の抽象実装です。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see JseExceptionHandler
 */
public abstract class AbstractJseExceptionHandler implements JseExceptionHandler {

    protected static final Log L = LogFactory.getLog(AbstractJseExceptionHandler.class);

    private static final String LOG_FORMAT;

    static {
        Config c = WebCoreInternals.getConfig(AbstractJseExceptionHandler.class);
        LOG_FORMAT = c.find("log_format");
    }

    /**
     * デフォルトコンストラクタです。
     */
    public AbstractJseExceptionHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void logException(Exception ex, HttpServletRequest request) {
        String message = null;
        if (ex instanceof AbstractException) {
            AbstractException e = (AbstractException) ex;
            Map<String, String> m = Maps.hash("code", e.getCode()).map("message", e.getMessage());
            message = Strings.substitute(LOG_FORMAT, m);
        } else {
            message = ex.getMessage();
        }
        internalLogException(message, ex);
    }

    /**
     * 例外発生時にログを出力するための拡張ポイントです。
     * @param message 例外メッセージ
     * @param ex 例外情報
     */
    protected abstract void internalLogException(String message, Exception ex);

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        logException(ex, request);
        storeException(ex, request);
        return internalResolveException(request, response, handler, ex);
    }

    /**
     * 例外情報をリクエストスコープに保存します。
     * @param ex 例外
     * @param request リクエスト
     */
    protected void storeException(Exception ex, HttpServletRequest request) {
        request.setAttribute(Controllers.EXCEPTION_KEY, ex);
    }

    /**
     * 例外をハンドリングするための拡張ポイントです。
     * @param request リクエスト
     * @param response レスポンス
     * @param handler ハンドラ
     * @param ex 例外
     * @return モデルやJSPパスなど
     */
    protected abstract ModelAndView internalResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
