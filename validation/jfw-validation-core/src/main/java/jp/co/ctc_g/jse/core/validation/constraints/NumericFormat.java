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

import jp.co.ctc_g.jse.core.validation.constraints.feature.numericformat.NumericFormatValidator;

/**
 * <p>
 * このバリデータ注釈は、プロパティの文字列が数値フォーマットかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * この検証は、type属性で指定された数値フォーマットかどうかを検証します。
 * type属性には以下のパターンが指定可能です。
 * <ul>
 * <li>{@link FormatType#NUMBER}</li>
 * <li>{@link FormatType#NUMBER_WITH_COMMA}</li>
 * <li>{@link FormatType#DECIMAL}</li>
 * <li>{@link FormatType#DECIMAL_WITH_COMMA}</li>
 * <li>{@link FormatType#OTHER}</li>
 * </ul>
 * 符号は半角・全角の区別はありません。
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
 *     &#064;NumericFormat(type = FormatType.DECIMAL)
 *     &#064;Decimal(precision = 10, scale = 2)
 *     private String value;
 *     //&#064;Number 読み込み専用メソッドにも付与することができます。
 *     public String getValue() { return value };
 *     public String setValue(String value) { this.value = value };
 * </pre>
 * <p>ゼロ埋めフォーマットの場合は、下記のような定義となります。</p>
 * <pre class="brush:java">
 * class Zero {
 *     static final String ZERO_PATTERN = "^[-－]?([0０]|[0-9０-９]*)$";
 * }
 * class NumericFormatBean {
 *     &#064;NumericFormat(pattern = Zero.ZERO_PATTERN, type = FormatType.OTHER)
 *     private String numeric;
 * }
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see NumericFormatValidator
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NumericFormatValidator.class})
public @interface NumericFormat {

    /**
     * 数値フォーマットのパターンを表す列挙型です。
     * FormatType.OTHERを指定したときにはpattern属性値を必ず指定してください。
     */
    enum FormatType {

        /**
         * 整数フォーマット：^[-－]?([0０]|[1-9１-９][0-9０-９]*)$
         */
        NUMBER("^[-－]?([0０]|[1-9１-９][0-9０-９]*)$"),
        /**
         * カンマを含む整数フォーマット：^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$
         */
        NUMBER_WITH_COMMA("^[-－]?([0０]|([1-9１-９][0-9０-９]{0,2}([,，][0-9０-９]{3})*))$"),
        /**
         * 小数フォーマット：^[-－]?([0０]|[0-9０-９]*[.．][0-9０-９]+)$
         */
        DECIMAL("^[-－]?([0-9０-９]*[.．][0-9０-９]+)$"),
        /**
         * カンマを含む小数フォーマット：^[-－]?([0０]|[0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$
         */
        DECIMAL_WITH_COMMA("^[-－]?([0-9０-９]{0,3}([,，][0-9０-９]{3})*[.．][0-9０-９]+)$"),
        /**
         * その他数値フォーマット(OTHERを指定したときはpattern属性を必ず指定してください。)
         */
        OTHER("");

        private String pattern;

        private FormatType(String pattern) {

            this.pattern = pattern;
        }

        public String getPattern() {

            return pattern;
        }
    }
     
    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.NumericFormat.message}";

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
     * 数値フォーマットのパターンを表す列挙型を指定します。
     * デフォルトは{@link FormatType#NUMBER}です。
     */
    FormatType type() default FormatType.NUMBER;
    
    /**
     * 数値フォーマットのパターンを指定します。
     * この属性値はtype属性にFormatType.OTHERが指定されたときに有効になります。
     */
    String pattern() default "";
    
    /**
     * {@link NumericFormat}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        NumericFormat[] value();
    }
    
}
