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

import jp.co.ctc_g.jfw.core.util.Arrays;

import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>
 * {@link HandlerInterceptorAdapter}インタフェースの実装です。
 * {@code Spring MVC} のコントローラにおけるフレームワーク拡張機能の事前処理・事後処理を行います。<br/>
 * 従って、このインターセプタはどのインターセプタよりも先に実行される必要があります。<br/>
 * </p>
 * @see HandlerInterceptorAdapter
 * @author ITOCHU Techno-Solutions Corporation.
 * @see HandlerInterceptorAdapter
 */
public class JseHandlerInterceptor implements HandlerInterceptor {

    private final ControllerFqcnPrefixingSessionAttributeStore sessionAttributeStore = new ControllerFqcnPrefixingSessionAttributeStore();

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if (Controllers.isHandlerMethod(handler)) {
            PostBackManager.begin(request, (HandlerMethod)handler);
        }
        
        return true;
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        PostBack postBack = PostBackManager.getCurrentPostBack();
        if (postBack != null && postBack.isPostBackRequest()) {
            if (postBack.getException() instanceof BindException)
                modelAndView.addObject(BindingResult.MODEL_KEY_PREFIX + postBack.getModelName(), postBack.getBindingResult());
        }
        
        
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex)
                    throws Exception {

        if (handler instanceof HandlerMethod) {
            PostBackManager.end();
            clearSessionAttributes(new ServletWebRequest(request, response), (HandlerMethod)handler);
        }
        
        
    }

    private void clearSessionAttributes(ServletWebRequest request, HandlerMethod handler) {
        if (Controllers.isSessionAttributeComplete(handler.getMethod())) {
            String[] keys = Arrays.merge(Controllers.findClearSessionAttributeNames(handler.getMethod()),
                    convertToStringArray(Controllers.findClearSessionAttributeTypes(handler.getMethod())));
            if (!Arrays.isEmpty(keys)) {
                cleanSessionAttributes(request, keys);
            } else {
                keys = Arrays.merge(Controllers.findSessionAttributeNames(handler.getBeanType()),
                        convertToStringArray(Controllers.findSessionAttributeTypes(handler.getBeanType())));
                cleanSessionAttributes(request, keys);
            }
        }
    }

    private void cleanSessionAttributes(ServletWebRequest request, String[] sessionAttributeNames) {
        if (!Arrays.isEmpty(sessionAttributeNames)) {
            for (String attributeName : sessionAttributeNames) {
                sessionAttributeStore.cleanupAttribute(request, attributeName);
            }
        }
    }

    private String[] convertToStringArray(Class<?>[] types) {
        if (!Arrays.isEmpty(types)) {
            String[] converted = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                converted[i] = ClassUtils.getShortNameAsProperty(types[i]);
            }
            return converted;
        }
        return null;
    }
}
