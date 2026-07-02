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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * このクラスは、{@link jp.co.ctc_g.jfw.core.exception.SystemException}やその他の例外を捕捉し、指定されたViewへ遷移する例外ハンドラです。
 * </p>
 * <p>
 * {@link RestController}の注釈が付与されているときはこの例外ハンドラを経由せずに
 * {@link org.springframework.web.bind.annotation.ControllerAdvice}の注釈が付与されている例外ハンドラへ委譲します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see JseExceptionHandler
 */
public class SystemExceptionHandler
        extends AbstractJseExceptionHandler
        implements JseExceptionHandler {

    private static final int ORDER = Ordered.LOWEST_PRECEDENCE;

    private String viewName;

    /**
     * デフォルトコンストラクタです。
     */
    public SystemExceptionHandler() {}

    /**
     * 遷移先するJSPを設定します。
     * @param viewName 遷移先のJSP
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

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
    public ModelAndView internalResolveException(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        ModelAndView mav = new ModelAndView(viewName);
        return mav;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supported(Object handler, Exception ex) {
        RestController rest = ((HandlerMethod) handler).getBean().getClass().getAnnotation(RestController.class);
        if (rest == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalLogException(String message, Exception ex) {
        if (L.isErrorEnabled()) {
            L.error(message, ex);
        }
    }
}
