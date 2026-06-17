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

package jp.co.ctc_g.jse.core.rest.jersey.resolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.ParameterNameProvider;
import jakarta.validation.Validation;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ContextResolver;

import jp.co.ctc_g.jse.core.rest.jersey.i18n.LocaleContextKeeper;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

/**
 * <p>
 * このクラスは、バリデーションエラー発生時のメッセージ取得やPath解決処理などの設定を行います。
 * </p>
 * <p>
 * この設定を行う場合は
 * <pre class="brush:java">
 * public class ShowcaseApplication extends ResourceConfig {
 *    public ShowcaseApplication() {
 *        packages(ShowcaseApplication.class.getPackage().getName());
 *        register(ValidationConfigurationContextResolver.class);
 *    }
 * }
 * </pre>
 * のように登録してください。
 * </p>
 * @see ContextResolver
 * @see ValidationConfig
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

    @Context
    private ResourceContext resourceContext;

    /**
     * デフォルトコンストラクタです。
     */
    public ValidationConfigurationContextResolver() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationConfig getContext(final Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class));
        config.parameterNameProvider(new CustomParameterNameProvider());
        config.messageInterpolator(new DefaultMessageInterpolator(Validation.buildDefaultValidatorFactory().getMessageInterpolator()));
        return config;
    }

    /**
     * <p>
     * このクラスは、バリデーションエラーメッセージをプロパティファイルより取得します。
     * </p>
     * @see MessageInterpolator
     * @author ITOCHU Techno-Solutions Corporation.
     */
    private static class DefaultMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator defaultInterpolator;

        public DefaultMessageInterpolator(MessageInterpolator interpolator) {
            this.defaultInterpolator = interpolator;
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return internal(messageTemplate, context, LocaleContextKeeper.getLocale() != null ? LocaleContextKeeper.getLocale() : Locale.getDefault());
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return internal(messageTemplate, context, locale);
        }

        private String internal(String messageTemplate, Context context, Locale locale) {
            return defaultInterpolator.interpolate(messageTemplate, context, locale);
        }

    }

    /**
     * <p>
     * このクラスは、バリデーションエラー発生時にpathプロパティの値にクラス名やメソッド名を付与します。
     * </p>
     * <p>
     * デフォルトを利用した場合はバリデーションエラー発生時のpathプロパティは
     * <code>arg0.id</code>のようになり、
     * どのクラスのどのプロパティでエラーになったのかが分かりにくくなります。
     * そこで、このクラスではエラーとなったプロパティのクラス名や
     * 実行したメソッド名を付与するように{@link ParameterNameProvider}を実装しています。
     * </p>
     * @see ParameterNameProvider
     * @author ITOCHU Techno-Solutions Corporation.
     */
    private static class CustomParameterNameProvider implements ParameterNameProvider {

        private final ParameterNameProvider nameProvider;

        /**
         * コンストラクタです。
         */
        public CustomParameterNameProvider() {
            this.nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return this.nameProvider.getParameterNames(constructor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getParameterNames(final Method method) {
            Class<?>[] types = method.getParameterTypes();
            List<String> ts = new ArrayList<String>();
            for (Class<?> type : types) {
                ts.add(type.getSimpleName());
            }
            return ts;
        }
    }
}
