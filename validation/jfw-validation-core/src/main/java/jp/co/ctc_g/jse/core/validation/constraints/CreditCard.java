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
import java.util.regex.Pattern;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import jp.co.ctc_g.jse.core.validation.constraints.feature.creditcard.CreditCardValidator;

/**
 * <p>
 * このバリデータ注釈は、クレジットカード番号が適切であるかを検証します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * クレジットカード番号として期待する入力は、ハイフンやスペースで区切られていない文字列です。
 * ハイフンやスペースで区切られている場合は、検証に失敗します。
 * クレジットカードの検証は次の２つの方法で行ないます。
 * <ul>
 * <li>Luhnアルゴリズムに基づくチェックサムが正しいか。（中国銀聯カードのときには検証しません）</li>
 * <li>（カードタイプが指定されているときのみ）カード番号のプレフィックスと長さが正しいか。</li>
 * </ul>
 * </p>
 * <p>
 * 本バリデータ注釈で提供しているクレジットカードの検証は、形式的なものにすぎません。
 * 正しいクレジットカード番号であるかどうかを検証するには、オーソリ処理が必要であることに注意してください。
 * </p>
 * <p>サポートしているタイプ：</p>
 * <ul>
 * <li>{@link java.lang.CharSequence}</li>
 * </ul>
 * <h4>利用方法</h4>
 * <p>
 * 使用例は下記の通りです。
 * 下記の例では、Luhnアルゴリズムに基づくチェックサムが正しいかを検証します。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     &#064;CreditCard // value属性を省略するとLuhnアルゴリズムにもとづくチェックサムが正しいかを検証します。
 *     private String value;
 *     //&#064;CreditCard 読み込み専用メソッドにも付与することができます。
 *     public String getValue() { return value };
 *     public String setValue(String value) { this.value = value };
 * </pre>
 * <p>
 * type要素に受け付けるカードを指定することができます。例えば、VISAとMasterCardを受け付ける場合には
 * 下記のように指定します。
 * </p>
 * <pre class="brush:java">
 * public class Domain {
 *     &#064;CreditCard(value = {CreditCard.Type.VISA, CreditCard.Type.MASTER_CARD})
 *     private String value;
 * </pre>
 * <p>
 * カードのプレフィックス情報は2011年9月現在での
 * <a href="http://en.wikipedia.org/wiki/Bank_card_number#Issuer_Identification_Number_.28IIN.29">
 * Wikipediaの記載
 * </a>
 * に基づいています。この情報が将来的にも正しいものであるとは限らないため、利用にあたって注意してください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see CreditCardValidator
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CreditCardValidator.class})
public @interface CreditCard {
  
    /**
     * <p>
     * この列挙型はクレジットカードの種類を表します。
     * </p>
     */
    enum Type {
        /**
         * アメリカンエクスプレス
         */
        AMERICAN_EXPRESS("^(34|37)", 15),

        /**
         * China UnionPay
         */
        CHINA_UNION_PAY("^62", 16),

        /**
         * Diners Club Carte Blanche
         */
        DINERS_CLUB_CARTE_BLANCHE("^30[0-5]", 14),

        /**
         * Diners Club International
         */
        DINERS_CLUB_INTERNATIONAL("^36", 14),

        /**
         * Diners Club United States & Canada
         */
        DINERS_CLUB_US_CANADA("^(54|55)", 16),

        /**
         * Discover Card
         */
        DISCOVER_CARD("^(6011|622(12[6-9]|[1-8][0-9][0-9]|9[0-1][0-9]|92[0-5])|64[4-9]|65)", 16),

        /**
         * InstaPayment
         */
        INSTA_PAYMENT("^63[7-9]", 16),

        /**
         * JCB
         */
        JCB("^35(2[8-9]|3[0-5])", 16),

        /**
         * Laser
         */
        LASER("^(6304|6706|6771|6709)", new int[] {
            16, 17, 18, 19
        }),

        /**
         * Maestro
         */
        MAESTRO("^(5018|5020|5038|6304|6759|676[1-3])", new int[] {
            12, 13, 14, 15, 16, 17, 18, 19
        }),

        /**
         * MasterCard
         */
        MASTER_CARD("^5[1-5]", 16),

        /**
         * Solo
         */
        SOLO("^6334|6767", new int[] {
            16, 18, 19
        }),

        /**
         * Switch
         */
        SWITCH("^(4903|4905|4911|4936|564182|633110|6333|6759)", new int[] {
            16, 18, 19
        }),

        /**
         * Visa
         */
        VISA("^4", 16),

        /**
         * Visa Electron
         */
        VISA_ELECTRON("^(4026|417500|4508|4844|4913|4917)", 16);

        private final Pattern prefixPattern;

        private final int[] lengths;

        private Type(String prefix, int length) {

            this(prefix, new int[] {
                length
            });
        }

        private Type(String prefix, int[] lengths) {

            this.prefixPattern = Pattern.compile(prefix + "\\d+$");
            this.lengths = lengths;
        }

        /**
         * クレジットカードのプレフィックスパターンを取得します。
         * @return プレフィックスパターン
         */
        public Pattern getPrefixPattern() {

            return prefixPattern;
        }

        /**
         * クレジットカードの長さを取得します。
         * @return クレジットカードの長さ
         */
        public int[] getLengths() {
            if (lengths == null) {
                return null;
            }
            return lengths.clone();
        }
    }
    
    /**
     * エラーメッセージのキーを指定します。
     */
    String message() default "{jp.co.ctc_g.jse.core.validation.constraints.CreditCard.message}";

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
     * クレジットカードの種類を指定します。
     */
    Type[] value() default {};
    
    /**
     * {@link CreditCard}の配列を指定します。
     * この制約を複数指定したい場合に利用します。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Documented
    @interface List {
        CreditCard[] value();
    }
    
}
