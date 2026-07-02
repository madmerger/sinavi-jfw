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

import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedafter.FixedAfterValidator;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedafter.FixedAfterValidatorForDate;

/**
 * <p>
 * このバリデータ注釈は、プロパティが比較対象の値よりも日付として「後」であるかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * プロパティが比較対象の値よりも日付として「後」であるかどうかを検証します。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * <li>{@link java.util.Date}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     
 *     &#064;FixedAfter(value = "2012/12/31")
 *     private Date birthday;
 *     
 *     // 以下、アクセサ省略...
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see FixedAfterValidator
 * @see FixedAfterValidatorForDate
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FixedAfterValidator.class, FixedAfterValidatorForDate.class})
public @interface FixedAfter {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.FixedAfter.message}";

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
     * 比較対象の日付を文字列で指定します。
     */
    String value();
    
    /**
     * 日付の書式を指定します。
     * {@link java.text.SimpleDateFormat}の日付/時刻パターンで指定します。
     */
    String pattern() default "yyyy/MM/dd";
    
    /**
     * {@link FixedAfter}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        FixedAfter[] value();
    }
    
}