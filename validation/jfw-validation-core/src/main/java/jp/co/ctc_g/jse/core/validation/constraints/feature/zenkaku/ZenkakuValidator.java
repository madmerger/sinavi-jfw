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

package jp.co.ctc_g.jse.core.validation.constraints.feature.zenkaku;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.Zenkaku;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link Zenkaku}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link Zenkaku}バリデータの検証アルゴリズムは、検証対象の文字列を"Windows-31J"でバイト長に変換し、
 * 取得したバイト長が検証対象の文字列の長さの×2倍であるかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see Zenkaku
 */
public class ZenkakuValidator implements ConstraintValidator<Zenkaku, CharSequence> {

    /**
     * デフォルトコンストラクタです。
     */
    public ZenkakuValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Zenkaku constraint) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isZenkaku(suspect);
    }

}
