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

package jp.co.ctc_g.jse.core.validation.constraints.feature.ascii;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.ASCII;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link ASCII}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link ASCII}バリデータの検証アルゴリズムは文字コードが'\u0020'から'\u007e'の範囲であるかどうか検証します。
 * この文字コードの範囲には半角スペースが含まれますので、ご注意ください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see ASCII
 */
public class ASCIIValidator implements ConstraintValidator<ASCII, CharSequence> {

    /**
     * デフォルトコンストラクタです。
     */
    public ASCIIValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ASCII constraint) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isBlank(suspect)) return true;
        return Validators.isASCII(suspect);
    }

}
