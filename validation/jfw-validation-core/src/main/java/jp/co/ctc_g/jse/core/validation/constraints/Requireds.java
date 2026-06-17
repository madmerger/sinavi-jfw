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
import jakarta.validation.ReportAsSingleViolation;

import jp.co.ctc_g.jse.core.validation.constraints.feature.requireds.RequiredValidatorForArraysOfCharSequence;
import jp.co.ctc_g.jse.core.validation.constraints.feature.requireds.RequiredValidatorForArraysOfObject;
import jp.co.ctc_g.jse.core.validation.constraints.feature.requireds.RequiredValidatorForCollection;

/**
 * <p>
 * このバリデータ注釈は、配列またはコレクションの要素が<code>null</code>、もしくはブランク文字列ではないかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * ここでのブランク文字とは、\t, \n, \x, 0B, \f, \r, 半角スペース, 全角スペースのことを指します。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.util.Collection}</li>
 * <li>{@link java.lang.reflect.Array}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     &#064;Requireds
 *     private String[] value;
 *     //&#064;Requireds 読み込み専用メソッドにも付与することができます。
 *     public String[] getValue() { return value };
 *     public String[] setValue(String[] value) { this.value = value };
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see RequiredValidatorForArraysOfCharSequence
 * @see RequiredValidatorForCollection
 * @see RequiredValidatorForArraysOfObject
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Constraint(validatedBy = {RequiredValidatorForArraysOfCharSequence.class, RequiredValidatorForCollection.class, RequiredValidatorForArraysOfObject.class})
public @interface Requireds {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.Requireds.message}";

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
     * {@link Requireds}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        Requireds[] value();
    }
}
