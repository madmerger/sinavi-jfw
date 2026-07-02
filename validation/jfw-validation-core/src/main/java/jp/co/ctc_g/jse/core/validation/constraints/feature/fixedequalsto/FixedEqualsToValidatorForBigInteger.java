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

package jp.co.ctc_g.jse.core.validation.constraints.feature.fixedequalsto;

import java.math.BigInteger;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.FixedEqualsTo;

/**
 * <p>
 * このクラスは、{@link FixedEqualsTo}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link FixedEqualsTo}バリデータの検証アルゴリズムは、プロパティの値が比較対象の固定値と等しいかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see FixedEqualsTo
 */
public class FixedEqualsToValidatorForBigInteger implements ConstraintValidator<FixedEqualsTo, BigInteger> {

    private BigInteger value;

    /**
     * デフォルトコンストラクタです。
     */
    public FixedEqualsToValidatorForBigInteger() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(FixedEqualsTo constraint) {
        try {
            this.value = new BigInteger(constraint.value());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(BigInteger suspect, ConstraintValidatorContext context) {
        if (suspect == null) return true;
        return suspect.equals(value);

    }
}
