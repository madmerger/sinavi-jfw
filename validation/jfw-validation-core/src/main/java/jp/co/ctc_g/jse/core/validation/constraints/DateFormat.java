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

import jp.co.ctc_g.jse.core.validation.constraints.feature.dateformat.DateFormatValidator;

/**
 * <p>
 * このバリデータ注釈は、プロパティの文字列が指定された書式に従った日付であるかどうかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * この検証は、{@link org.apache.commons.validator.GenericValidator}に移譲されます。
 * 日付の書式は、<code>pattern</code>要素に対して{@link java.text.SimpleDateFormat}の日付/時刻パターンを指定します。
 * <code>pattern</code>要素のデフォルト値は、<code>yyyy/MM/dd</code>になっています。
 * また、<code>pattern</code>要素の解釈に失敗したときには、常に検証が失敗します。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * 下記のコードでは文字列がISO 8601形式の日付形式であるかを検証しています。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     &#064;Date(pattern = "yyyy/MM/dd")
 *     private String value;
 *     //&#064;Date 読み込み専用メソッドにも付与することができます。
 *     public String getValue() { return value };
 *     public String setValue(String value) { this.value = value };
 * </pre>
 * <h4>注意事項</h4>
 * <p>
 * <code>strict</code>属性に<code>false</code>を指定すると、形式のチェックを厳密に行ないません。
 * 例えば、<code>pattern</code>属性が<code>yyyy/MM/dd</code>のとき、
 * <code>strict</code>属性が<code>true</code>であれば
 * <code>2011/12/1</code>という値は検証に失敗しますが、
 * <code>pattern</code>属性が<code>false</code>であれば検証に成功します。
 * 形式の厳密なチェックが行われないと<code>2011/12/1aa</code>などのように日付形式の文字列の後に
 * 不正な文字列が含まれている場合でも検証は成功してしまいます。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see DateFormatValidator
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateFormatValidator.class})
public @interface DateFormat {

    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.DateFormat.message}";

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
     * 日付の書式を指定します。
     * {@link java.text.SimpleDateFormat}の日付/時刻パターンで指定します。
     */
    String pattern() default "yyyy/MM/dd";

    /**
     * 厳密に日付書式を検証するかを指定します。
     * 値が<code>true</code>であれば、日付書式と文字長が等しいかどうかを検証します。
     */
    boolean strict() default true;
    
    /**
     * {@link DateFormat}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        DateFormat[] value();
    }
    
}
