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

package jp.co.ctc_g.jse.core.validation.constraints.feature.creditcard;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.CreditCard;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link CreditCard}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link CreditCard}バリデータの検証アルゴリズムは次の２つの方法があります。
 * <ul>
 * <li>Luhnアルゴリズムに基づくチェックサムが正しいか。（中国銀聯カードのときには検証しません）</li>
 * <li>（カードタイプが指定されているときのみ）カード番号のプレフィックスと長さが正しいか。</li>
 * </ul>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see CreditCard
 */
public class CreditCardValidator implements ConstraintValidator<CreditCard, CharSequence> {

    private CreditCard.Type[] types;

    /**
     * デフォルトコンストラクタです。
     */
    public CreditCardValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(CreditCard constraint) {
        types = constraint.value();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return isCreditCard(suspect);
    }

    private boolean isCreditCard(CharSequence suspect) {
        CreditCard.Type type = null;
        if (types.length > 0) {
            type = detectType(suspect);
            if (type == null) return false;
        }
        if (CreditCard.Type.CHINA_UNION_PAY.getPrefixPattern().matcher(suspect).matches()) { return true; }
        return checkLuhn(suspect);
    }

    private boolean checkLuhn(CharSequence suspect) {
        int sum = 0;
        boolean even = false;
        for (int i = suspect.length() - 1; i >= 0; i--) {
            int n = Character.digit(suspect.charAt(i), 10);
            if (n < 0) { return false; }
            if (even) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            even = !even;
        }
        return (sum % 10) == 0;
    }

    private CreditCard.Type detectType(CharSequence suspect) {
        CreditCard.Type type = null;
        for (CreditCard.Type t : types) {
            if (t.getPrefixPattern().matcher(suspect).matches()) {
                type = t;
                break;
            }
        }
        if (type == null) return null;
        if (Arrays.binarySearch(type.getLengths(), suspect.length()) > -1) {
            return type;
        } else {
            return null;
        }
    }
}
