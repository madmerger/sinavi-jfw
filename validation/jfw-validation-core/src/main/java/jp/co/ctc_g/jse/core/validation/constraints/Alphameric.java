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

import jp.co.ctc_g.jse.core.validation.constraints.feature.alphameric.AlphamericValidator;

/**
 * <p>
 * このバリデータ注釈は、プロパティがアルファベットもしくはアラビア数字であるかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * ここでのアルファベットとは、大文字のA-Z、小文字のa-zの範囲を指し、アラビア数字とは0-9の範囲を指します。
 * UNICODEのHalfwidth and Fullwidth Forms領域に含まれる文字
 * (全角大文字のＡ-Ｚ、全角小文字のａ-ｚ、全角のアラビア数値０-９)は
 * 含まれませんので、検証に失敗します。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     &#064;Alphameric
 *     private String value;
 *     //&#064;Alphameric 読み込み専用メソッドにも付与することができます。
 *     public String getValue() { return value };
 *     public String setValue(String value) { this.value = value };
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see AlphamericValidator
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AlphamericValidator.class})
public @interface Alphameric {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.Alphameric.message}";

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
     * {@link Alphameric}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        Alphameric[] value();
    }
    
}
