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

import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedlessthanequalsto.FixedLessThanEqualsToValidator;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedlessthanequalsto.FixedLessThanEqualsToValidatorForNumber;

/**
 * <p>
 * このバリデータ注釈は、プロパティの値が指定された値よりも小さいかあるいは同じかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * プロパティは {@link java.math.BigDecimal} オブジェクトに変更してから比較されます。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * <li>{@link java.lang.Number}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     
 *     &#064;FixedLessThanEqualsTo("1234")
 *     private String value;
 *     
 *     //&#064;FixedLessThanEqualsTo("1234") 読み込み専用メソッドにも付与することができます。
 *     public Integer getValue() { return value; }
 *     public Integer setValue(Integer value) { this.value = value; }
 *     
 *     // 以下、アクセサ省略...
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see FixedLessThanEqualsToValidator
 * @see FixedLessThanEqualsToValidatorForNumber
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FixedLessThanEqualsToValidator.class, FixedLessThanEqualsToValidatorForNumber.class})
public @interface FixedLessThanEqualsTo {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.FixedLessThanEqualsTo.message}";

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
     * 比較対象の値を文字列で指定します。
     */
    String value();

    /**
     * {@link FixedLessThanEqualsTo}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        FixedLessThanEqualsTo[] value();
    }

}