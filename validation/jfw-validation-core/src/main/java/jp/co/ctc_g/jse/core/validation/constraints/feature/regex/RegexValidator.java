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

package jp.co.ctc_g.jse.core.validation.constraints.feature.regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.Regex;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link Regex}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link Regex}バリデータの検証アルゴリズムは、指定された正規表現にマッチするかどうかを検証します。
 * 指定される正規表現は内部でキャッシュされます。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see Regex
 */
public class RegexValidator implements ConstraintValidator<Regex, CharSequence> {

    private Pattern pattern;

    /**
     * デフォルトコンストラクタです。
     */
    public RegexValidator() {}

    /**
     * {@inheritDoc}
     */
    public void initialize(Regex constraint) {
        pattern = compile(constraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isMatches(pattern, suspect);
    }

    private Pattern compile(Regex p) {
        try {
            return Pattern.compile(p.value());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
