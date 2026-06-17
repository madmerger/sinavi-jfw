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

package jp.co.ctc_g.jfw.test.unit;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * このクラスは、J-Frameworkのテスト関連の機能を利用するために必要なテストランナーです。
 * 以下の場合、このクラスをランナーとして指定する必要があります。
 * </p>
 * <ul>
 *  <li>テストケースにインジェクション等、DIコンテナの機能を利用する場合</li>
 *  <li>{@link DatabaseInitialize}によるRDBMS内データ操作をする場合</li>
 * </ul>
 * <p>
 *  また、このクラスは{@link SpringJUnit4ClassRunner}を継承しているため、
 *  {@link SpringJUnit4ClassRunner}が提供する機能は全て有効です。
 *  例えば、{@link org.springframework.test.context.ContextConfiguration}でアプリケーションコンテキストファイルを指定することができます。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class J2Unit4ClassRunner extends SpringJUnit4ClassRunner {

    /**
     * 指定されたクラスオブジェクトを利用して、このクラスのインスタンスを生成します。
     * @param clazz テスト対象クラス
     * @throws InitializationError 特になし
     */
    public J2Unit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        registerTestExecutionListeners();
    }

    /**
     * テストリスナーを登録します。
     */
    protected void registerTestExecutionListeners() {
        getTestContextManager().registerTestExecutionListeners(new TestDatabaseKeeper());
    }

}
