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

import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidator;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForBigDecimal;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForBigInteger;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForDate;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForDouble;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForFloat;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForInteger;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto.FixedEqualsToValidatorForLong;

/**
 * <p>
 * このバリデータ注釈は、プロパティと指定された値との等価性を検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * このバリデータが等価性を検証する際には、{@link Object#equals(Object)} メソッドを利用します。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * <li>{@link java.util.Date}</li>
 * <li>{@link java.math.BigDecimal}</li>
 * <li>{@link java.math.BigInteger}</li>
 * <li>{@link java.lang.Double}</li>
 * <li>{@link java.lang.Float}</li>
 * <li>{@link java.lang.Integer}</li>
 * <li>{@link java.lang.Long}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     
 *     &#064;FixedEqualsTo("12345")
 *     private String value;
 *     
 *     //&#064;FixedEqualsTo("12345") 読み込み専用メソッドにも付与することができます。
 *     public String getValue() { return value; }
 *     public String setValue(String value) { this.value = value; }
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
    FixedEqualsToValidator.class, FixedEqualsToValidatorForDate.class, FixedEqualsToValidatorForBigDecimal.class,
    FixedEqualsToValidatorForBigInteger.class, FixedEqualsToValidatorForDouble.class, FixedEqualsToValidatorForFloat.class,
    FixedEqualsToValidatorForInteger.class, FixedEqualsToValidatorForLong.class
})
public @interface FixedEqualsTo {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.FixedEqualsTo.message}";

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
     * 日付の書式を指定します。
     * {@link java.text.SimpleDateFormat}の日付/時刻パターンで指定します。
     * この属性値はプロパティの型が{@link java.util.Date}のときにのみ有効になります。
     */
    String datePattern() default "yyyy/MM/dd";

    /**
     * {@link FixedEqualsTo}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {

        FixedEqualsTo[] value();
    }

}