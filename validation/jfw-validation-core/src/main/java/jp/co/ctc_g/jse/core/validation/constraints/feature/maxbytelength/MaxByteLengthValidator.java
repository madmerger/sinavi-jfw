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

package jp.co.ctc_g.jse.core.validation.constraints.feature.maxbytelength;

import java.io.UnsupportedEncodingException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.MaxByteLength;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link MaxByteLength}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link MaxByteLength}バリデータの検証アルゴリズムは、検証対象の文字列を指定された文字コードでエンコードし、バイト配列を取得します。
 * 取得したバイト配列の長さが指定されたバイト長以内かどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see MaxByteLength
 */
public class MaxByteLengthValidator implements ConstraintValidator<MaxByteLength, CharSequence> {

    private int size;
    private String encoding;

    /**
     * デフォルトコンストラクタです。
     */
    public MaxByteLengthValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(MaxByteLength constraint) {
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
            return Validators.maxByteLength(suspect, size, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
