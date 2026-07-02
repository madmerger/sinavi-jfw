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

package jp.co.ctc_g.jse.core.amqp.internal;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages.DelegateResourceBundle;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;

/**
 * J-Frameworkの内部向けAMQPユーティリティです。
 * 各クラス用の設定を読み込むための {@link Config} 取得処理を提供します。
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class AmqpInternals {
    
    private AmqpInternals() {}

    private static final String FRAMEWORK_RESOURCES_PROPERTIES = "jp.co.ctc_g.jse.core.amqp.FrameworkResources";

    private static final String USER_FRAMEWORK_RESOURCES_PROPERTIES;

    static {
        String setting = System.getProperty("jp.co.ctc_g.setting");
        if (setting == null || "".equals(setting)) {
            USER_FRAMEWORK_RESOURCES_PROPERTIES = "FrameworkResources";
        } else {
            USER_FRAMEWORK_RESOURCES_PROPERTIES = setting;
        }
    }

    /**
     * 指定されたクラスに関連付けられた{@link Config}を返却します。 指定されたクラスと同じパッケージ内に、
     * 指定されたクラスと同じ名前のプロパティファイルが見つからない場合、 このメソッドは{@link InternalException}
     * を例外コード{@code E-INTERNAL#0001}で送出します。 よって、このメソッドの引数に渡されるクラスは、
     * 関連するデフォルトプロパティファイルが存在しなければなりません。 指定されたクラスが{@code null}であることは許可されません。
     *
     * @param clazz コンフィギュレーションに対応するクラス
     * @return コンフィギュレーション
     * @throws InternalException
     *             プロパティファイルが存在しない場合(E-INTERNAL#0001)
     */
    public static Config getConfig(Class<?> clazz) {
        Args.checkNotNull(clazz);
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(FRAMEWORK_RESOURCES_PROPERTIES);
            ResourceBundle user = ResourceBundle.getBundle(USER_FRAMEWORK_RESOURCES_PROPERTIES);
            resourceBundle = new DelegateResourceBundle(user, resourceBundle);
            return new Config(clazz.getCanonicalName(), resourceBundle);
        } catch (MissingResourceException e) {
            Map<String, String> m = Maps.hash("setting", USER_FRAMEWORK_RESOURCES_PROPERTIES);
            throw new InternalException(AmqpInternals.class, "E-INTERNAL#0001", m);
        }
    }

}
