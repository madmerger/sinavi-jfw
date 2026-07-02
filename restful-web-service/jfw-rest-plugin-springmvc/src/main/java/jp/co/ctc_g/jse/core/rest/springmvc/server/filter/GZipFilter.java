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

package jp.co.ctc_g.jse.core.rest.springmvc.server.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <p>
 * このクラスは、サーブレットのレスポンス内容をgzip圧縮転送するためのフィルタです。 
 * </p>
 * <p>
 * gzip圧縮はrfc2068に基づきクライアントが「accept-encoding」に 「gzip」を指定している場合にレスポンスを圧縮転送します。
 * このクラスはフィルタによりクライアントの「accept-encoding」 を解析し、もしgzip圧縮に対応したクライアントであった場合レスポンスを圧縮します。
 * </p>
 * <p>
 * 以下に設定例を示します。
 * <pre class="brush:java">
 *  &lt;filter&gt;
 *   &lt;filter-name&gt;gzipFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;jp.co.ctc_g.jse.core.rest.springmvc.server.filter.GZipFilter&lt;/filter-class&gt;
 *  &lt;/filter&gt;
 *  &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;gzipFilter&lt;/filter-name&gt;
 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 * </pre>
 * </p>
 * @see OncePerRequestFilter
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class GZipFilter extends OncePerRequestFilter {

    /**
     * デフォルトコンストラクタです。
     */
    public GZipFilter() {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String acceptEncoding = request.getHeader(GZips.ACCEPT_ENCODING);
        if (acceptEncoding != null && acceptEncoding.contains(GZips.ENCODING_GZIP)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            GZipResponseWrapper responseWrapper = new GZipResponseWrapper(httpResponse);
            chain.doFilter(request, responseWrapper);
            responseWrapper.close();
        } else {
            chain.doFilter(request, response);
        }
    }
}
