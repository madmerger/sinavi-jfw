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

package jp.co.ctc_g.jse.core.amqp.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;

/**
 * <p>
 * このクラスは、信頼できるパッケージのみからクラスのデシリアライズを許可する{@link ClassMapper}の実装です。
 * </p>
 * <p>
 * AMQPメッセージの{@code __TypeId__}ヘッダに基づいて任意のクラスがインスタンス化されることを防ぎ、
 * 安全でないデシリアライゼーション（CWE-502）を防止します。
 * </p>
 * <p>
 * クラスのロード前に{@code __TypeId__}ヘッダの値を信頼済みパッケージに対して検証するため、
 * 悪意あるクラスのstatic initializerが実行されることも防止します。
 * </p>
 * <p>
 * デフォルトでは以下のパッケージが信頼済みとして設定されています：
 * <ul>
 *   <li>{@code jp.co.ctc_g} - フレームワークおよびアプリケーションのパッケージ</li>
 *   <li>{@code java.util} - 標準コレクション型</li>
 *   <li>{@code java.lang} - 標準型（String等）</li>
 * </ul>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class TrustedClassMapper implements ClassMapper {

    private static final String[] DEFAULT_TRUSTED_PACKAGES = {
        "jp.co.ctc_g",
        "java.util",
        "java.lang"
    };

    private final DefaultClassMapper delegate;
    private final Set<String> trustedPackages;

    /**
     * デフォルトの信頼済みパッケージで{@link TrustedClassMapper}を生成します。
     */
    public TrustedClassMapper() {
        this(DEFAULT_TRUSTED_PACKAGES);
    }

    /**
     * 指定された信頼済みパッケージで{@link TrustedClassMapper}を生成します。
     * @param trustedPackages 許可するパッケージプレフィックスの配列
     */
    public TrustedClassMapper(String... trustedPackages) {
        this.delegate = new DefaultClassMapper();
        try {
            this.delegate.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DefaultClassMapper", e);
        }
        this.trustedPackages = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList(trustedPackages)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * メッセージの{@code __TypeId__}ヘッダの値を信頼済みパッケージに対して検証してから
     * クラスをロードします。信頼済みパッケージに属さないクラスが指定された場合は
     * {@link SecurityException}をスローし、クラスのロード自体を行いません。
     * </p>
     */
    @Override
    public Class<?> toClass(MessageProperties properties) {
        String typeId = retrieveTypeId(properties);
        if (typeId != null) {
            validateTypeId(typeId);
        }
        return delegate.toClass(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fromClass(Class<?> clazz, MessageProperties properties) {
        delegate.fromClass(clazz, properties);
    }

    private String retrieveTypeId(MessageProperties properties) {
        Map<String, Object> headers = properties.getHeaders();
        if (headers == null) {
            return null;
        }
        Object typeIdObj = headers.get(DefaultClassMapper.DEFAULT_CLASSID_FIELD_NAME);
        return typeIdObj != null ? typeIdObj.toString() : null;
    }

    private void validateTypeId(String typeId) {
        for (String trustedPackage : trustedPackages) {
            if (typeId.startsWith(trustedPackage + ".")) {
                return;
            }
        }
        throw new SecurityException(
            "Untrusted deserialization type: " + typeId
            + ". Allowed packages: " + trustedPackages);
    }

    /**
     * 現在の信頼済みパッケージの一覧を返します。
     * @return 信頼済みパッケージの{@link Set}
     */
    public Set<String> getTrustedPackages() {
        return trustedPackages;
    }
}
