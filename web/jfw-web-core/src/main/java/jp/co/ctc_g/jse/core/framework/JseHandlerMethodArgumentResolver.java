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

import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import jp.co.ctc_g.jse.core.message.MessageContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * <p>
 * このクラスは、フレームワークが提供するコントローラの引数を解決する機能を提供します。<br/>
 * 具体的に解決する引数は{@link PostBack}と{@link MessageContext}の型が指定された引数です。
 * </p>
 * <h4>{@link PostBack}型の引数の解決</h4>
 * <p>
 * コントローラのハンドラ・メソッドで{@link PostBack}型の引数が指定された場合は、エラー発生時のポストバックであるかにかかわらず
 * {@link PostBack}のインスタンスを該当の引数に設定し、{@code null}を設定するようなことはありません。
 * ポストバックであるかどうかは{@link postBack#isBack()}によって判断して下さい。
 * </p>
 * <h4>{@link MessageContext}型の引数の解決</h4>
 * <p>
 * {@link MessageContext}型の引数はフレームワークワークによって必ず解決されます。設定された{@link MessageContext}のインスタンスを
 * 通してメッセージをストアすることができます。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see HandlerMethodArgumentResolver
 * @see InitializingBean
 */
public class JseHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

    private Set<Class<?>> supportedType = new HashSet<Class<?>>();;

    /**
     * デフォルトコンストラクタです。
     */
    public JseHandlerMethodArgumentResolver() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return supportedType.contains(parameter.getParameterType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType.isAssignableFrom(MessageContext.class)) {
            return resolveMessageContextParameter(request);
        } else if (parameterType.isAssignableFrom(PostBack.class)) {
            return resolvePostBackParameter(request);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        supportedType.addAll(getSupportedTypeSet());
    }

    private Object resolvePostBackParameter(HttpServletRequest request) {

        PostBack postBack = (PostBack) RequestContextHolder.getRequestAttributes().getAttribute(PostBack.POST_BACK_ATTRIBUTE_KEY, RequestAttributes.SCOPE_REQUEST);
        if (postBack != null) {
            return postBack;
        }
        return null;
    }

    private Object resolveMessageContextParameter(HttpServletRequest request) {
        MessageContext messageContext = (MessageContext) request.getAttribute(MessageContext.MESSAGE_CONTEXT_ATTRIBUTE_KEY);
        if (messageContext != null) {
            return messageContext;
        } else {
            return new MessageContext(request);
        }
    }

    private Set<Class<?>> getSupportedTypeSet() {
        Set<Class<?>> supported = new HashSet<Class<?>>();
        supported.add(PostBack.class);
        supported.add(MessageContext.class);
        return supported;
    }
}
