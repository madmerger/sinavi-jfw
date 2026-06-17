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


import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * <p>
 * 例外ハンドラ―のインタフェースを既定します。<br/>
 * このインタフェースを実装した例外ハンドラ―は、{@link JseHandlerExceptionResolverComposite}に 登録することにより有効になります。<br/>
 * </p>
 * <h4>例外ハンドラ―の登録</h4>
 * <p>
 * 例外ハンドラ―の登録は、{@code Spring MVC}の{@code Servlet Context定義ファイル}に下記の通り定義することにより行います。
 * </p>
 * <pre>
 * 
 * </pre>
 * <h4>例外ハンドラ―の動作フロー</h4>
 * <p>
 * 例外発生時は、{@link JseHandlerExceptionResolverComposite}が各ハンドラ―の{@code getOrder()}が
 * 返却する優先度に準じて{@code supported(Object, Exception)}を実行し、発生した例外に該当するハンドラ―を検索します。
 * 実際の例外処理は、検索されたハンドラ―の{@code logException(Exception, HttpServletRequest)}を実行することにより
 * 行われます。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see JseHandlerExceptionResolverComposite
 * @see HandlerExceptionResolver
 * @see Orderd
 */
public interface JseExceptionHandler
        extends HandlerExceptionResolver, Ordered {

    /**
     * <p>
     * 引数を解釈しこの例外ハンドラ―が処理を行うかを決定します。<br/>
     * {@code true} を返却すると、{@link HandlerExceptionResolver} の {@code resolveException(HttpServletRequest, HttpServletResponse, Object, Exception)}を
     * オーバーライドして定義される例外ハンドリングロジックが実行されます。
     * </p>
     * @param handler ハンドラ―情報
     * @param ex 例外インスタンス
     * @return true を返却した場合、{@code resolveException(HttpServletRequest, HttpServletResponse, Object, Exception)}
     * が実行されます。
     */
    boolean supported(Object handler, Exception ex);

    /**
     * <p>
     * ログ出力を行います。<br/>
     * </p>
     * @param ex 例外インスタンス
     * @param request {@HttpServletRequest}インスタンス
     */
    void logException(Exception ex, HttpServletRequest request);
}
