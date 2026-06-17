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

package jp.co.ctc_g.jse.vid;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jse.core.framework.Controllers;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>
 * このクラスは、{@code Spring MVC}の{@code Controller}呼出しをインターセプトし、
 * {@code Controller}のハンドラ―・メソッドに付与された注釈{@link ViewIdConstraint}の
 * 属性{@code allow}、{@code except}の値に 従い画面遷移の可否を判定します。
 * なお、画面遷移の可否判定の具象的な実装は、{@link ViewTransitionKeeper}が保有しています。
 * </p>
 * <h4>設定方法</h4>
 * <p>
 * 同クラスの設定は、DispacherServletのコンテキストにたいして行います。
 * </p>
 * <pre>
 * &lt;mvc:interceptors&gt;
 *   &lt;bean class="jp.co.ctc_g.jfw.vid.ViewIdConstraintHandlerInterceptorAdapter"/&gt;
 * &lt;/mvc:interceptors&gt;
 * </pre>
 *
 * @author ITOCHU Techno-Solutions Corporation.
 * @see HandlerInterceptor
 * @see ViewTransitionKeeper
 */
public class ViewIdConstraintHandlerInterceptor
        implements HandlerInterceptor {

    /**
     * デフォルトコンストラクタです。
     */
    public ViewIdConstraintHandlerInterceptor() {}

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
        	constraintCheck(request, (HandlerMethod)handler);
        }
        return true;
    }

    private void constraintCheck(HttpServletRequest request, HandlerMethod handlerMethod) {
        ViewIdConstraint constraint = handlerMethod.getMethodAnnotation(ViewIdConstraint.class);
        if (constraint != null) {
            ViewTransitionKeeper viewTransitionKeeper = new ViewTransitionKeeper(handlerMethod.getMethod());

            if (viewTransitionKeeper != null && viewTransitionKeeper.isCheckRequired()) {
                String scope = viewTransitionKeeper.getScope();
                ViewId vid = null;
                if (scope.equals(Controllers.SCOPE_SESSION)) {
                    vid = ViewId.current(request);
                } else {
                    throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0006");
                }
                if (vid != null) {
                    viewTransitionKeeper.check(vid);
                } else {
                    throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0007");
                }
            }
        }
    }
}
