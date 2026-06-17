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

import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedbefore.FixedBeforeValidator;
import jp.co.ctc_g.jse.core.validation.constraints.feature.fixedbefore.FixedBeforeValidatorForDate;

/**
 * <p>
 * このバリデータ注釈は、プロパティが指定された値よりも日付として「前」であるかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * プロパティが指定された値よりも日付として「前」であるかどうかを検証します。
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
 *     &#064;FixedBefore(value = "2012/12/31")
 *     private Date birthday;
 *     
 *     // 以下、アクセサ省略...
 * </pre>
 * <h4>複数条件の指定</h4>
 * <p>
 * Listを指定して複数の条件を指定することが可能です。<br/>
 * これは、コンテキストによって比較する値が変わる場合に有効です。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     
 *     &#064;Before.List({
 *         &#064;Before(value = "2012/11/30", groups = Group1.class),
 *         &#064;Before(value = "2012/12/31", groups = Group2.class)
 *     })
 *     private Date birthday;
 *
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see FixedBeforeValidator
 * @see FixedBeforeValidatorForDate
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FixedBeforeValidator.class, FixedBeforeValidatorForDate.class})
public @interface FixedBefore {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.FixedBefore.message}";

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
     * {@link FixedBefore}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        FixedBefore[] value();
    }
    
}