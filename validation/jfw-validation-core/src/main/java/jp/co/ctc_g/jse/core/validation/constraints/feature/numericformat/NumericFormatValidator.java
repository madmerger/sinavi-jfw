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

package jp.co.ctc_g.jse.core.validation.constraints.feature.numericformat;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.NumericFormat;
import jp.co.ctc_g.jse.core.validation.constraints.NumericFormat.FormatType;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link NumericFormat}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * 数値形式フォーマットかどうかを検証するのみですので、{@link jp.co.ctc_g.jse.core.validation.constraints.Number}バリデータや
 * {@link jp.co.ctc_g.jse.core.validation.constraints.Decimal}バリデータと必ず併用してください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see NumericFormat
 */
public class NumericFormatValidator implements ConstraintValidator<NumericFormat, CharSequence> {

    private String pattern;
    private FormatType type;

    /**
     * デフォルトコンストラクタです。
     */
    public NumericFormatValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NumericFormat constraint) {
        validateParameters(constraint);
        pattern = constraint.pattern();
        type = constraint.type();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isMatches(getPattern(), suspect);
    }

    private String getPattern() {
        if (type.equals(FormatType.OTHER)) { return pattern; }
        return type.getPattern();
    }

    private void validateParameters(NumericFormat constraint) {
        if (constraint.type().equals(FormatType.OTHER) && Validators.isEmpty(constraint.pattern())) { 
            throw new IllegalArgumentException();
        }
    }
}
