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

package jp.co.ctc_g.jfw.core.internal;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

/**
 * <p>
 * このクラスは、J-Frameworkの内部処理方式に関するユーティリティクラスです。
 * J-Frameworkを利用する開発者のみなさんに とりたてて重要な機能を提供することはありませんが、
 * J-Framework 内部でのメッセージ管理の方式を理解する手助けにはなります。
 * </p>
 * <p>
 * このクラスが現在管理している内部処理方式は、
 * </p>
 * <ul>
 * <li>コンフィギュレーション管理方式</li>
 * </ul>
 * <p>
 * です。
 * </p>
 * <h4>メッセージ管理方式</h4>
 * <p>
 * J-Frameworkでは、 フレームワーク内部メッセージを含む全てのメッセージを
 * ローカライズすることが可能であるように設計されています。 フレームワーク内部メッセージはJava標準のローカライズメカニズムに則り、
 * プロパティファイルにて管理されています。 メッセージファイル(プロパティファイル)は、
 * 1つのパッケージにつき1つ作成され、Messagesという名称です。
 * また、<strong>全てのメッセージには特定可能なID(メッセージコード)が付与されています</strong>。
 * このIDは、以下のようなパターンで作成されます。 <div
 * class="inline_caption">[メッセージ種別]-[パッケージ]#[シーケンス]</div>
 * 例えば、このクラスで発生する警告種別のメッセージがあるとすると、 <div
 * class="inline_caption">W-INTERNAL#0001</div> となります。メッセージ種別は以下の通りです。
 * </p>
 * <table>
 * <thead>
 * <tr>
 * <th>略号</th>
 * <th>正記号</th>
 * <th>説明</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>E</td>
 * <td>ERROR</td>
 * <td>エラーメッセージ</td>
 * </tr>
 * <tr>
 * <td>W</td>
 * <td>WARN</td>
 * <td>警告メッセージ</td>
 * </tr>
 * <tr>
 * <td>I</td>
 * <td>INFO</td>
 * <td>インフォメーションメッセージ</td>
 * </tr>
 * <tr>
 * <td>D</td>
 * <td>DEBUG</td>
 * <td>デバッグメッセージ</td>
 * </tr>
 * <tr>
 * <td>O</td>
 * <td>OTHER</td>
 * <td>上記区分に分類できないメッセージ</td>
 * </tr>
 * </tbody>
 * <table>
 * <p>
 * メッセージが格納されているリソースバンドルを取得するには、
 * </p>
 *
 * <pre>
 * ResourceBundle bundle = Internals.getBundle(Foo.class);
 * </pre>
 *
 * <p>
 * と、クラスオブジェクトを渡します。
 * </p>
 *
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class InternalMessages {

    /**
     * コンストラクタです。
     * インスタンスの生成を抑止します。
     */
    private InternalMessages() {}

    /**
     * <p>
     * リソースバンドルを拡張したクラスです。
     * </p>
     */
    public static class DelegateResourceBundle extends ResourceBundle {

        private static final int INITIAL_CAPACITY = 128;
        private final ResourceBundle messageBundle;

        /**
         * コンストラクタです。
         * @param messageBundle リソースバンドル
         * @see ResourceBundle
         */
        public DelegateResourceBundle(ResourceBundle messageBundle) {
            this.messageBundle = messageBundle;
        }

        /**
         * コンストラクタです。
         * @param messageBundle リソースバンドル
         * @param parent マージ対象のリソースバンドル
         */
        public DelegateResourceBundle(ResourceBundle messageBundle, ResourceBundle parent) {
            this.messageBundle = messageBundle;
            setParent(parent);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public synchronized Enumeration<String> getKeys() {
            HashSet<String> set = new HashSet<String>(INITIAL_CAPACITY);

            addAllElements(set, messageBundle.getKeys());
            if (parent != null) {
                addAllElements(set, parent.getKeys());
            }
            return new IteratorEnumeration(set.iterator());
        }

        private void addAllElements(HashSet<String> set, Enumeration<String> e) {
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object handleGetObject(String key) {
            try {
                return messageBundle.getObject(key);
            } catch (MissingResourceException e) {
                if (parent != null) {
                    return parent.getObject(key);
                } else {
                    throw e;
                }
            }
        }
    }

    private static final String MESSAGE_FILE = ".Messages";

    /**
     * 指定されたクラスに関連付けられたメッセージリソースバンドルを返却します。
     * このメソッドは、そのクラスが所属するパッケージ内部に存在するメッセージファイルを読み込みます。 ファイルが存在しない場合、
     * {@link MissingResourceException}が発生します。
     *
     * @param clazz
     *            メッセージリソースバンドルを取得したいクラス
     * @return メッセージが定義されたリソースバンドル
     * @throws MissingResourceException
     *             リソースが見つからない場合
     * @see ResourceBundle
     */
    public static ResourceBundle getBundle(Class<?> clazz) {
        return ResourceBundle.getBundle(clazz.getPackage().getName() + MESSAGE_FILE);
    }

}
