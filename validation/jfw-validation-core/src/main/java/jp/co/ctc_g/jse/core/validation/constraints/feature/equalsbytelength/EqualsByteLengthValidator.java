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

package jp.co.ctc_g.jse.core.validation.constraints.feature.equalsbytelength;

import java.io.UnsupportedEncodingException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.EqualsByteLength;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link EqualsByteLength}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link EqualsByteLength}バリデータの検証アルゴリズムは、検証対象の文字列を指定された文字コードでエンコードし、バイト配列を取得します。
 * 取得したバイト配列の長さが指定されたバイト長と同じかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see EqualsByteLength
 */
public class EqualsByteLengthValidator implements ConstraintValidator<EqualsByteLength, CharSequence> {

    private int size;
    private String encoding;

    /**
     * デフォルトコンストラクタです。
     */
    public EqualsByteLengthValidator() {}

    /**
     * {@inheritDoc}
     */
    public void initialize(EqualsByteLength constraint) {
        this.size = constraint.value();
        this.encoding = constraint.encoding();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        try {
            return Validators.equalsByteLength(suspect, size, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
