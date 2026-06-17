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

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;

import java.util.Comparator;

/**
 * <p>
 * このクラスは、テストの実行順に依存したテストを行うために必要なJUnit 5メソッドオーダーです。
 * {@link Order}アノテーションに基づいてテストメソッドの実行順序を制御します。
 * </p>
 * <p>
 * 使用方法:
 * <pre class="brush:java">
 * &#064;TestMethodOrder(OrderedRunner.class)
 * public class FooTest {
 *     &#064;Test
 *     &#064;Order(order = 1)
 *     public void firstTest() { ... }
 * }
 * </pre>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class OrderedRunner implements MethodOrderer {

    /**
     * デフォルトコンストラクタです。
     */
    public OrderedRunner() {}

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(Comparator.comparingInt(md -> {
            Order order = md.getMethod().getAnnotation(Order.class);
            return order != null ? order.order() : Integer.MAX_VALUE;
        }));
    }
}
