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

package jp.co.ctc_g.jse.core.validation.constraints.feature.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.Email;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link Email}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link Email}バリデータの検証アルゴリズムは、{@link Validators#isEmail(CharSequence)}を利用しています。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see Email
 */
public class EmailValidator implements ConstraintValidator<Email, CharSequence> {

    /**
     * デフォルトコンストラクタです。
     */
    public EmailValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Email constraint) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        if (Validators.containBlank(suspect)) return false;
        return Validators.isEmail(suspect);
    }

}
