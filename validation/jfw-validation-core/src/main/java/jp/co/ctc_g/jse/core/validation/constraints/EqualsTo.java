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

package jp.co.ctc_g.jse.core.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import jp.co.ctc_g.jse.core.validation.constraints.feature.equalsto.EqualsToValidator;

/**
 * <p>
 * このバリデータ注釈は、比較元のプロパティの値と、比較対象のプロパティの値との等価性を検証します。
 * なお、固定値と比較したい場合は、{@link FixedEqualsTo}バリデータを利用して下さい。
 * </p>
 * <h4>概要</h4>
 * <p>
 * このバリデータが等価性を検証する際には、{@link java.lang.Object#equals(Object)} メソッドを利用します。
 * 任意のタイプに指定可能です。
 * </p>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * &#064;EqualsTo(from = "from" , to = "to")
 * public class Domain {
 *     
 *     private String from;
 *     
 *     private String to;
 *
 *     // 以下、アクセサ省略...
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see EqualsToValidator
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EqualsToValidator.class})
public @interface EqualsTo {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.EqualsTo.message}";

    /**
     * 検証グループを指定します。
     */
    Class<?>[] groups() default {};

    /**
     * ペイロードを指定します。
     * デフォルトの検証プロセスでは利用されません。
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 比較元のプロパティを指定します。
     */
    String from();
    
    /**
     * 比較対象のプロパティを指定します。
     */
    String to();

    /**
     * {@link EqualsTo}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Documented
    @interface List {
        EqualsTo[] value();
    }

}