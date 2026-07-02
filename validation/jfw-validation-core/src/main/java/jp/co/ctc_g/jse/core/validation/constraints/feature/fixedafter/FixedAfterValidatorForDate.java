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

package jp.co.ctc_g.jse.core.validation.constraints.feature.fixedafter;

import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.FixedAfter;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 *  このクラスは、{@link FixedAfter}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * プロパティが比較対象の固定値よりも「後」であるかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see FixedAfter
 */
public class FixedAfterValidatorForDate implements ConstraintValidator<FixedAfter, Date> {

    private Date target;

    /**
     * デフォルトコンストラクタです。
     */
    public FixedAfterValidatorForDate() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(FixedAfter constraint) {
        target = Validators.toDate(constraint.value(), constraint.pattern(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Date suspect, ConstraintValidatorContext context) {
        if (suspect == null) return true;
        return suspect.after(target);
    }
}
