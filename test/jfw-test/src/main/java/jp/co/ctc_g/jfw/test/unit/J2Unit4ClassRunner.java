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

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * <p>
 * このクラスは、J-Frameworkのテスト関連の機能を利用するためのJUnit 5拡張クラスです。
 * JUnit 5では{@code @ExtendWith(SpringExtension.class)}を利用してください。
 * </p>
 * <p>
 * このクラスは後方互換性のために残されていますが、
 * 新規テストでは直接{@code @ExtendWith(SpringExtension.class)}の使用を推奨します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @deprecated JUnit 5では{@code @ExtendWith(SpringExtension.class)}を使用してください。
 */
@Deprecated
public class J2Unit4ClassRunner {

    /**
     * デフォルトコンストラクタです。
     */
    public J2Unit4ClassRunner() {}

}
