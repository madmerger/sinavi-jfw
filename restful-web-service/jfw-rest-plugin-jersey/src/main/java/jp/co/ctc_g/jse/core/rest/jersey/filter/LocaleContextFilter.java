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

package jp.co.ctc_g.jse.core.rest.jersey.filter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;

import jp.co.ctc_g.jse.core.rest.jersey.i18n.LocaleContextKeeper;

/**
 * <p>
 * このクラスは、HttpHeaderに設定されているロケール情報をスレッドローカルに設定するフィルタです。
 * </p>
 * <p>
 * ResourceBundleより読み込むロケールを変更したい場合などがあれば、
 * HttpヘッダーのAccept-Languageに指定された値をスレッドローカルに格納します。
 * </p>
 * <p>
 * フィルタを設定する場合は
 * <pre class="brush:java">
 * public class ShowcaseApplication extends ResourceConfig {
 *    public ShowcaseApplication() {
 *        packages(ShowcaseApplication.class.getPackage().getName());
 *        register(LocaleContextFilter.class);
 *    }
 * }
 * </pre>
 * のように登録してください。
 * </p>
 * <p>
 * もし、このフィルタが設定されていて、HttpヘッダーのAccept-Languageが指定されなかった場合は
 * {@link java.util.Locale#getDefault()}をスレッドローカルに格納します。
 * </p>
 * <p>
 * また、このフィルタはサーバで動作するように以下のクラスを実装しています。
 * <ul>
 * <li>{@link javax.ws.rs.container.ContainerRequestFilter}</li>
 * <li>{@link javax.ws.rs.container.ContainerResponseFilter}</li>
 * </ul>
 * </p>
 * <p>
 * なお、JerseyのUriConnegFilterを併用する場合は
 * 必ずUriConnegFilterを本フィルタよりも先に登録してください。
 * <pre class="brush:java">
 * public class ShowcaseApplication extends ResourceConfig {
 *    public ShowcaseApplication() {
 *        packages(ShowcaseApplication.class.getPackage().getName());
 *        register(UriConnegFilter.class, 1);
 *        register(LocaleContextFilter.class, 2);
 *    }
 * }
 * </pre>
 * </p>
 * @see LocaleContextKeeper
 * @see ContainerRequestFilter
 * @see ContainerResponseFilter
 * @author ITOCHU Techno-Solutions Corporation.
 */
@PreMatching
@Priority(Priorities.USER)
public class LocaleContextFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * デフォルトコンストラクタです。
     */
    public LocaleContextFilter() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Locale locale = Locale.getDefault();
        List<Locale> accepts = requestContext.getAcceptableLanguages();
        Locale acceptLanguage = accepts.get(0);
        if (!"*".equals(acceptLanguage.getLanguage())) {
            locale = acceptLanguage;
        }
        LocaleContextKeeper.setLocale(locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LocaleContextKeeper.remove();
    }

}
