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

import java.util.Optional;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * このクラスは、テストの開始と成功・失敗のログを出力するJUnit 5拡張です。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class TestTracer implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger L = LoggerFactory.getLogger(TestTracer.class);
    private static final ResourceBundle R = InternalMessages.getBundle(TestTracer.class);

    /**
     * デフォルトコンストラクタです。
     */
    public TestTracer() {}

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        L.info(Strings.substitute(R.getString("I-TEST#0003"),
            Maps.hash("class", context.getRequiredTestClass().getSimpleName())
                .map("method", context.getRequiredTestMethod().getName())
                .map("package", context.getRequiredTestClass().getPackage().getName())));
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> exception = context.getExecutionException();
        if (exception.isPresent()) {
            L.error(Strings.substitute(R.getString("E-TEST#0023"),
                Maps.hash("class", context.getRequiredTestClass().getSimpleName())
                    .map("method", context.getRequiredTestMethod().getName())
                    .map("package", context.getRequiredTestClass().getPackage().getName())), exception.get());
        } else {
            L.info(Strings.substitute(R.getString("I-TEST#0005"),
                Maps.hash("class", context.getRequiredTestClass().getSimpleName())
                    .map("method", context.getRequiredTestMethod().getName())
                    .map("package", context.getRequiredTestClass().getPackage().getName())));
        }
        L.info(Strings.substitute(R.getString("I-TEST#0004"),
            Maps.hash("class", context.getRequiredTestClass().getSimpleName())
                .map("method", context.getRequiredTestMethod().getName())
                .map("package", context.getRequiredTestClass().getPackage().getName())));
    }

}
