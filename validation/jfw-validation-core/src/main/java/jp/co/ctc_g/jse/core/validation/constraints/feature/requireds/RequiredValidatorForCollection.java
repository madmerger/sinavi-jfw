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

package jp.co.ctc_g.jse.core.validation.constraints.feature.requireds;

import java.util.Collection;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.Requireds;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link Requireds}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link Requireds}バリデータの検証アルゴリズムは、{@link Validators#isBlank(CharSequence)}を利用しています。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see Requireds
 */
public class RequiredValidatorForCollection implements ConstraintValidator<Requireds, Collection<?>> {

    /**
     * デフォルトコンストラクタです。
     */
    public RequiredValidatorForCollection() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Requireds constraint) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Collection<?> suspects, ConstraintValidatorContext context) {
        if (suspects == null || suspects.isEmpty()) return false;
        for (Object obj : suspects) {
            if (Validators.isNull(obj)) return false;
            if (String.class.isAssignableFrom(obj.getClass())) {
                if (Validators.isBlank(obj.toString())) return false;
            }
        }
        return true;
    }

}
