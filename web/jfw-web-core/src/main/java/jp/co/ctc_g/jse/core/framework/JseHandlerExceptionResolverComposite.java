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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * このクラスは、例外ハンドラ―のコンポジットです。<br/>
 * 例外発生時に、例外ハンドラ―の {@code getOrder()} で表明されるオーダー順に従って
 *  {@code supported(Object, Exception)} を実行し、最初に {@code true} を返却した
 *  例外ハンドラ―の{@code logException(Exception, HttpServletRequest)} を実行します。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see HandlerExceptionResolver
 * @see Ordered
 * @see InitializingBean
 */
public class JseHandlerExceptionResolverComposite
        implements HandlerExceptionResolver, Ordered, InitializingBean {

    private static final String DEFAULT_EXCEPTION_ATTRIBUTE_KEY = "exception";

    private String exceptionAttributeKey = DEFAULT_EXCEPTION_ATTRIBUTE_KEY;

    private List<JseExceptionHandler> exceptionHandler;

    private List<JseExceptionHandler> customExceptionHandlers;

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    /**
     * デフォルトコンストラクタです。
     */
    public JseHandlerExceptionResolverComposite() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * <p>例外ハンドラーによって処理される例外をバインドする際のキー文字列を設定します。</p>
     * @param exceptionAttributeKey キー文字
     */
    public void setExceptionAttributeKey(String exceptionAttributeKey) {
        this.exceptionAttributeKey = exceptionAttributeKey;
    }

    /**
     * <p>例外ハンドラーによって処理される例外をバインドする際のキー文字列を返却します。</p>
     * @return キー文字列
     */
    public String getExceptionAttributeKey() {
        return this.exceptionAttributeKey;
    }

    /**
     * <p>カスタム例外ハンドラーをフレームワークに登録します。</p>
     * @param customExceptionResolvers {@link JseExceptionHandler}インタフェースを実装したカスタム例外ハンドラーのリスト
     */
    public void setCustomExceptionHandlers(List<JseExceptionHandler> customExceptionResolvers) {
        Collections.sort(customExceptionResolvers, new JseHandlerExceptionResolverComparator());
        this.customExceptionHandlers = customExceptionResolvers;
    }

    /**
     * <p>フレームワークに登録されているカスタム例外ハンドラのリストを返却します。</p>
     * @return フレームワークに登録されているカスタム例外ハンドラのリスト
     */
    public List<JseExceptionHandler> getCustomExceptionHandlers() {
        return Collections.unmodifiableList(customExceptionHandlers);
    }

    /**
     * <p>フレームワークに登録されている例外ハンドラのリストを返却します。{@code getCustomeExceptionHandlers()}と異なり
     * フレームワークで登録されている例外ハンドラーも返却されたリストに含まれます。</p>
     * @return フレームワークに登録されているカスタム例外ハンドラのリスト
     */
    public List<JseExceptionHandler> getExceptionHandlers() {
        return Collections.unmodifiableList(exceptionHandler);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        if (Controllers.isHandlerMethod(handler) && exceptionHandler != null) {
            for (JseExceptionHandler r : exceptionHandler) {
                if (r.supported(handler, ex)) {
                    ModelAndView mav = r.resolveException(request, response, handler, ex);
                    if (mav != null) {
                        if (exceptionAttributeKey != null) {
                            mav.addObject(exceptionAttributeKey, ex);
                        }
                        return mav;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void afterPropertiesSet() throws Exception {
        exceptionHandler = new ArrayList<JseExceptionHandler>();
        exceptionHandler.addAll(getDefaultExceptionHandler());
        if (customExceptionHandlers != null && !customExceptionHandlers.isEmpty())
            exceptionHandler.addAll(customExceptionHandlers);
    }

    private class JseHandlerExceptionResolverComparator
            implements Comparator<JseExceptionHandler> {

        @Override
        public int compare(JseExceptionHandler handler1, JseExceptionHandler handler2) {
            if (handler1.getOrder() == handler2.getOrder()) {
                return 0;
            } else if (handler1.getOrder() > handler2.getOrder()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private ArrayList<JseExceptionHandler> getDefaultExceptionHandler() {
        ArrayList<JseExceptionHandler> defaultExcpetionHandler =
                new ArrayList<JseExceptionHandler>();
        defaultExcpetionHandler.add(new PostBackExceptionHandler());
        return defaultExcpetionHandler;
    }

}
