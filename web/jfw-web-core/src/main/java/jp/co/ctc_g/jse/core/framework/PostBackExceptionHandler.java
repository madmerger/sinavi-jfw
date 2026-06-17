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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.core.Ordered;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * このクラスは、{@code Spring MVC}コントローラのアノテーション拡張に対するエラー処理を提供するハンドラ―クラスです。
 * {@code Spring MVC}コントローラのハンドラ―メソッドで例外発生時に{@link Input}アノテーションの定義の従い
 * エラーハンドリングを行います。
 * </p>
 * <h4>例外ハンドリング機構の優先順位</h4>
 * <p>
 * J-Frameworkでは複数の例外ハンドリングを行う機構を提供しています。
 * 各例外ハンドリング機能の優先度は、下記の通りです。
 * <ul>
 *   </li>{@link JseExceptionHandler}を用いてコントローラメソッドに例外ハンドリング処理を実装する方式</li>
 *   </li></li>
 * </ul>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class PostBackExceptionHandler
        extends AbstractJseExceptionHandler
        implements JseExceptionHandler {

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    /**
     * デフォルトコンストラクタです。
     */
    public PostBackExceptionHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supported(Object handler, Exception ex) {
        if (ex instanceof BindException && PostBackManager.isPostBackTargetException(ex))
            return true;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView internalResolveException(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception e) {

        BindException be = (BindException) e;
        PostBackManager.save(new PostBack(be));
        PostBackManager.saveMessage(e);
        ModelAndView mav = new ModelAndView();
        mav.setViewName(PostBackManager.buildUri(be, be.getTarget(), true));
        mav.addObject(be.getObjectName(), be.getTarget());
        mav.addObject(BindingResult.MODEL_KEY_PREFIX + be.getObjectName(), be);
        return mav;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalLogException(String message, Exception ex) {
        if (L.isTraceEnabled()) {
            L.trace(message, ex);
        }
    }
}
