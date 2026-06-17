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

package jp.co.ctc_g.jse.core.validation.constraints.feature.dateformat;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.DateFormat;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link DateFormat}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link DateFormat}バリデータの検証アルゴリズムは、{@link Validators#isDate(CharSequence, String, boolean)}を利用しています。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see DateFormat
 */
public class DateFormatValidator implements ConstraintValidator<DateFormat, CharSequence> {

    private boolean strict;
    private String pattern;

    /**
     * デフォルトコンストラクタです。
     */
    public DateFormatValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(DateFormat constraint) {
        this.strict = constraint.strict();
        this.pattern = constraint.pattern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isDate(suspect, pattern, strict);
    }

}
