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

package jp.co.ctc_g.jse.core.validation.constraints.feature.decimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.Decimal;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、検証対象の文字列が小数値であるかどうかの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * このフィーチャの検証アルゴリズムは{@link java.math.BigDecimal}に変換した後に有効桁数や小数点桁数を検証します。
 * 数値形式以外の文字列の場合は検証に成功するようになっているため、
 * {@link jp.co.ctc_g.jse.core.validation.constraints.NumericFormat}バリデータと必ず併用してください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see Decimal
 */
public class DecimalValidator implements ConstraintValidator<Decimal, CharSequence> {

    private boolean signed;
    private int precision;
    private int scale;

    /**
     * デフォルトコンストラクタです。
     */
    public DecimalValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Decimal constraint) {
        Decimals.validate(constraint);
        signed = constraint.signed();
        precision = constraint.precision();
        scale = constraint.scale();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isDecimal(suspect, signed, precision, scale);
    }

}
