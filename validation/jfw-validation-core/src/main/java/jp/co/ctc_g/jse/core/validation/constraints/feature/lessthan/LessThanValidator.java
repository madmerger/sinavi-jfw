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

package jp.co.ctc_g.jse.core.validation.constraints.feature.lessthan;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.validation.constraints.LessThan;
import jp.co.ctc_g.jse.core.validation.constraints.feature.after.AfterValidator;
import jp.co.ctc_g.jse.core.validation.util.Validators;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * このクラスは、{@link LessThan}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link LessThan}バリデータの検証アルゴリズムは、プロパティの値が比較対象のプロパティの値よりも小さいかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see LessThan
 */
public class LessThanValidator implements ConstraintValidator<LessThan, Object> {

    private static final Logger L  = LoggerFactory.getLogger(LessThanValidator.class);
    private static final ResourceBundle R = InternalMessages.getBundle(LessThanValidator.class);
    
    private LessThan lessThan;

    /**
     * デフォルトコンストラクタです。
     */
    public LessThanValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(LessThan constraint) {
        this.lessThan = constraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object suspect, ConstraintValidatorContext context) {
        Object f = null;
        Object t = null;
        try {
            f = PropertyUtils.getProperty(suspect, lessThan.from());
            t = PropertyUtils.getProperty(suspect, lessThan.to());
        } catch (IllegalAccessException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-LESS-THAN#0001"), 
                    Maps.hash("from", lessThan.from()).map("to", lessThan.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-LESS-THAN#0001", e);
        } catch (InvocationTargetException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-LESS-THAN#0002"), 
                    Maps.hash("from", lessThan.from()).map("to", lessThan.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-LESS-THAN#0002", e);
        } catch (NoSuchMethodException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-LESS-THAN#0003"), 
                    Maps.hash("from", lessThan.from()).map("to", lessThan.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-LESS-THAN#0003", e);
        }
        if (f == null || t == null) return true;
        return Validators.lessThan(Validators.toBigDecimal(f), Validators.toBigDecimal(t)) ? true : addErrors(context);
    }
    
    private boolean addErrors(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(lessThan.message()).addPropertyNode(lessThan.from()).addConstraintViolation();
        return false;
    }
}
