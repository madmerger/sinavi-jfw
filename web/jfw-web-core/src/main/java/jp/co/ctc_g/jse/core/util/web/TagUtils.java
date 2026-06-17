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

package jp.co.ctc_g.jse.core.util.web;


import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.UriUtils;

/**
 * <p>
 * このクラスは、カスタムタグライブラリより呼び出され、アプリケーションのURLを生成します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class TagUtils {

    /**
     * パスのタイプを表現する。
     */
    enum UrlType {
        /**
         * コンテキスト・パスからの相対パス
         */
        CONTEXT_RELATIVE,
        /**
         * 相対パス
         */
        RELATIVE,
        /**
         *  絶対パス
         */
        ABSOLUTE
    }

    // インスタンスの生成を抑止
    private TagUtils() {}

    private static final String URL_TYPE_ABSOLUTE =  "://";

    private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";

    private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

    /**
     * <p>
     * パスのタイプを判定し {@link UrlType} を返却します。
     * パスのタイプの判定は以下の通り行われます。
     * <ul>
     *   <li>絶対パス：{@code ://} を含むURL文字列</li>
     *   <li>コンテキスト相対パス： {@code /} から始まるURL文字列</li>
     *   <li>相対パス：上記以外のURL文字列</li>
     * </ul>
     * </p>
     * @param url パス
     * @return {@link UrlType} パスのタイプ
     */
    public static UrlType getUrlType(String url) {
        if (url.contains(URL_TYPE_ABSOLUTE)) {
            return UrlType.ABSOLUTE;
        } else if (url.startsWith("/")) {
            return UrlType.CONTEXT_RELATIVE;
        } else {
            return UrlType.RELATIVE;
        }
    }

    /**
     * リンクとして出力するURLを生成します。
     * @param url パス
     * @param params パスに付与するパラメータ
     * @param pageContext ページコンテキスト
     * @param isHtmlEscape HTMLの特殊文字をエスケープするかどうか
     * @param isJavaScriptEscape JavaScriptの特殊文字をエスケープするかどうか
     * @return パス
     * @throws JspException 予期しない例外
     */
    public static String createUrl(String url, Map<String, String[]> params, PageContext pageContext, boolean isHtmlEscape, boolean isJavaScriptEscape) throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

        StringBuilder buffer = new StringBuilder();
        UrlType urlType = getUrlType(url);
        if (urlType == UrlType.CONTEXT_RELATIVE) {
            buffer.append(request.getContextPath());
            if (!url.startsWith("/")) {
                buffer.append("/");
            }
        }
        buffer.append(replaceUriTemplateParams(url, params, pageContext));
        buffer.append(createQueryString(params, (url.indexOf("?") == -1), pageContext));

        String urlStr = buffer.toString();
        if (urlType != UrlType.ABSOLUTE) {
            urlStr = response.encodeURL(urlStr);
        }

        urlStr = isHtmlEscape ? HtmlUtils.htmlEscape(urlStr) : urlStr;
        urlStr = isJavaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

        return urlStr;
    }

    /**
     * <p>
     * クエリ文字列を生成します。<br/>
     * </p>
     * @param params パラメータ
     * @param includeQueryStringDelimiter クエリのデリミタ
     * @param pageContext {@link PageContext} インスタンス
     * @return クエリ文字列
     * @throws JspException 不正なエンコーディングが指定された場合
     */
    public static String createQueryString(Map<String, String[]> params, boolean includeQueryStringDelimiter, PageContext pageContext)
            throws JspException {

        String encoding = pageContext.getResponse().getCharacterEncoding();
        StringBuilder qs = new StringBuilder();
        Set<String> sets = params.keySet();
        for (String key : sets) {
            String[] value = params.get(key);
            if (includeQueryStringDelimiter && qs.length() == 0) {
                qs.append("?");
            } else {
                qs.append("&");
            }
            if (value.length == 1) {
                qs.append(UriUtils.encodeQueryParam(key, encoding));
                if (params.get(key) != null) {
                    qs.append("=");
                    qs.append(UriUtils.encodeQueryParam(value[0], encoding));
                }
            } else {
                for (String v : value) {
                    qs.append(UriUtils.encodeQueryParam(key, encoding));
                    if (v != null) {
                        qs.append("=");
                        qs.append(UriUtils.encodeQueryParam(v, encoding));
                    }
                }
            }
        }
        return qs.toString();
    }

    /**
     * <p>
     * パスに含まれる置換文字列を指定したパラメータを用いて置換します。<br/>
     * </p>
     * @param uri 基底のパス
     * @param params パラメータ
     * @param pageContext {@link pageContext} インスタンス
     * @return 置換されたパス
     * @throws JspException 不正なエンコーディングが指定された場合
     */
    protected static String replaceUriTemplateParams(String uri, Map<String, String[]> params, PageContext pageContext)
            throws JspException {

        String encoding = pageContext.getResponse().getCharacterEncoding();
        Set<String> sets = params.keySet();
        for (String key : sets) {
            String template = URL_TEMPLATE_DELIMITER_PREFIX + key + URL_TEMPLATE_DELIMITER_SUFFIX;
            String[] value = params.get(key);
            if ((value.length == 1) && uri.contains(template)) {
                uri = uri.replace(template, Matcher.quoteReplacement(UriUtils.encodePath(value[0], encoding)));
            }
        }
        return uri;
    }

    /**
     * <p>
     * {@code Spring MVC} のパス修飾機構によって指定されたURLパスを修飾します。<br/>
     * </p>
     * @param action パス
     * @param requestContext {@link RequestContext} インスタンス
     * @param pageContext {@link PageContext} インスタンス
     * @return 修飾されたパス
     */
    public static String processAction(String action, RequestContext requestContext, PageContext pageContext) {
        RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        ServletRequest request = pageContext.getRequest();
        if ((processor != null) && (request instanceof HttpServletRequest)) {
//            return processor.processAction((HttpServletRequest) request, action);
            return processor.processAction((HttpServletRequest) request, action, ((HttpServletRequest) request).getMethod());
        }
        return action;
    }

    /**
     * <p>
     * {@code Spring MVC} のパス修飾機構によって指定されたURLパスを修飾します。<br/>
     * </p>
     * @param url パス
     * @param requestContext {@link RequestContext} インスタンス
     * @param pageContext {@link PageContext} インスタンス
     * @return 修飾されたパス
     */
    public static String processUrl(String url, RequestContext requestContext, PageContext pageContext) {
        RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        ServletRequest request = pageContext.getRequest();
        if ((processor != null) && (request instanceof HttpServletRequest)) {
            return processor.processUrl((HttpServletRequest) request, url);
        }
        return url;
    }
}
