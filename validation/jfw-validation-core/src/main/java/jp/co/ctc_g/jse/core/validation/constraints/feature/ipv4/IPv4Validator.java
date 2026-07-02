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

package jp.co.ctc_g.jse.core.validation.constraints.feature.ipv4;

import java.util.regex.Matcher;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jse.core.validation.constraints.IPv4;
import jp.co.ctc_g.jse.core.validation.util.Validators;

/**
 * <p>
 * このクラスは、{@link IPv4}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link IPv4}バリデータの検証アルゴリズムは、以下の正規表現に一致するかどうか検証します。
 * <pre>
 * <code>
 * ^(
 * (?:1(?:0\d?|1\d?|2\d?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?)?|2(?:[6789]|5[0-5]?|0\d?|1\d?|2\d?|3\d?|4\d?)?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?|0)\.
 * (?:1(?:0\d?|1\d?|2\d?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?)?|2(?:[6789]|5[0-5]?|0\d?|1\d?|2\d?|3\d?|4\d?)?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?|0)\.
 * (?:1(?:0\d?|1\d?|2\d?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?)?|2(?:[6789]|5[0-5]?|0\d?|1\d?|2\d?|3\d?|4\d?)?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?|0)\.
 * (?:1(?:0\d?|1\d?|2\d?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?)?|2(?:[6789]|5[0-5]?|0\d?|1\d?|2\d?|3\d?|4\d?)?|3\d?|4\d?|5\d?|6\d?|7\d?|8\d?|9\d?|0))
 * (?:/([0-9]|[12]\d|3[0-2])
 * )?$
 * </code>
 * </pre>
 * only属性が指定されている場合は、検証対象の文字列がネットワークアドレス・ブロードキャストアドレスかどうかの検証を行います。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see IPv4
 */
public class IPv4Validator implements ConstraintValidator<IPv4, CharSequence> {

    private String[] only;

    /**
     * デフォルトコンストラクタです。
     */
    public IPv4Validator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(IPv4 constraint) {
        validateParameters(constraint);
        this.only = constraint.only();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(CharSequence suspect, ConstraintValidatorContext context) {
        if (Validators.isEmpty(suspect)) return true;
        return Validators.isIPv4(suspect, only);
    }

    private void validateParameters(IPv4 constraint) {
        for (String network : constraint.only()) {
            Matcher m = Validators.isIPv4(network);
            if (!m.matches()) { throw new IllegalArgumentException(); }
            String netaddr = m.group(1);
            String cidr = m.group(2);
            if (netaddr == null || cidr == null) { throw new IllegalArgumentException(); }
        }
    }
}
