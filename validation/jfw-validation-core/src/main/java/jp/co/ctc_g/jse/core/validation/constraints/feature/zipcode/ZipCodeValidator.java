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

package jp.co.ctc_g.jse.core.validation.constraints.feature.zipcode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.ZipCode;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link ZipCode}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link ZipCode}バリデータの検証アルゴリズムは、以下の２つの正規表現に一致するかどうか検証します。
 * <pre>
 * <code>
 * // 郵便番号の3桁部分のチェック
 * ^(?:1(?:5[012345678]|7[013456789]|9[012345678]|3[01234567]|1[0123456]|4[0123456]|2[01345]|0\d|6\d|8\d)|2(?:9[023456789]|0[12345678]|2[01234567]|6[01234567]|8[23456789]|1[0123456]|3\d|4\d|5\d|7\d)|6(?:2[012345679]|0[01234567]|8[0123459]|1\d|3\d|4\d|5\d|6\d|7\d|9\d)|5(?:4[012345679]|8[012345679]|0\d|1\d|2\d|3\d|5\d|6\d|7\d|9\d)|7(?:2[012356789]|4[012345679]|0\d|1\d|3\d|5\d|6\d|7\d|8\d|9\d)|4(?:9[012345678]|2[01245678]|0\d|1\d|3\d|4\d|5\d|6\d|7\d|8\d)|0(?:3[013456789]|0[1234567]|1\d|2\d|4\d|5\d|6\d|7\d|8\d|9\d)|9(?:0[01234567]|7[01234569]|1\d|2\d|3\d|4\d|5\d|6\d|8\d|9\d)|3(?:0\d|1\d|2\d|3\d|4\d|5\d|6\d|7\d|8\d|9\d)|8(?:0\d|1\d|2\d|3\d|4\d|5\d|6\d|7\d|8\d|9\d))$
 * </code>
 * </pre>
 * <pre>
 * <code>
 * // 郵便番号の4桁部分のチェック
 * ^\d{4}$
 * </code>
 * </pre>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see ZipCode
 */
public class ZipCodeValidator implements ConstraintValidator<ZipCode, CharSequence> {

    private String separator;

    /**
     * デフォルトコンストラクタです。
     */
    public ZipCodeValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ZipCode constraint) {
        separator = constraint.separator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isZipCode(suspect, separator);
    }
}
