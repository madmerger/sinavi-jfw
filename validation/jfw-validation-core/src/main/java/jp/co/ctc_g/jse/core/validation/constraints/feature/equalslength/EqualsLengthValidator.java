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

package jp.co.ctc_g.jse.core.validation.constraints.feature.equalslength;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.EqualsLength;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link EqualsLength}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link EqualsLength}バリデータの検証アルゴリズムは、検証対象の文字列の長さが指定された桁数と同じかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see EqualsLength
 */
public class EqualsLengthValidator implements ConstraintValidator<EqualsLength, CharSequence> {

    private int size;

    /**
     * デフォルトコンストラクタです。
     */
    public EqualsLengthValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(EqualsLength constraint) {
        this.size = constraint.value();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        String target = suspect.toString();
        return target.length() == size;
    }
}
