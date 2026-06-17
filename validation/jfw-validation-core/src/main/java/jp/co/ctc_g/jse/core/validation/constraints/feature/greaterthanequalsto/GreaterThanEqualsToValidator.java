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

package jp.co.ctc_g.jse.core.validation.constraints.feature.greaterthanequalsto;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.validation.constraints.GreaterThanEqualsTo;
import jp.co.ctc_g.jse.core.validation.constraints.feature.after.AfterValidator;
import jp.co.ctc_g.jse.core.validation.util.Validators;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * このクラスは、{@link GreaterThanEqualsTo}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * {@link GreaterThanEqualsTo}バリデータの検証アルゴリズムは、プロパティの値が比較対象のプロパティの値よりも大きいかどうかを検証します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see GreaterThanEqualsTo
 */
public class GreaterThanEqualsToValidator implements ConstraintValidator<GreaterThanEqualsTo, Object> {

    private static final Logger L  = LoggerFactory.getLogger(GreaterThanEqualsToValidator.class);
    private static final ResourceBundle R = InternalMessages.getBundle(GreaterThanEqualsToValidator.class);
    
    private GreaterThanEqualsTo greaterThanEqualsTo;

    /**
     * デフォルトコンストラクタです。
     */
    public GreaterThanEqualsToValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(GreaterThanEqualsTo constraint) {
        this.greaterThanEqualsTo = constraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object suspect, ConstraintValidatorContext context) {
        Object f = null;
        Object t = null;
        try {
            f = PropertyUtils.getProperty(suspect, greaterThanEqualsTo.from());
            t = PropertyUtils.getProperty(suspect, greaterThanEqualsTo.to());
        } catch (IllegalAccessException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-GREATER-THAN-EQUALS-TO#0001"), 
                    Maps.hash("from", greaterThanEqualsTo.from()).map("to", greaterThanEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-GREATER-THAN-EQUALS-TO#0001", e);
        } catch (InvocationTargetException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-GREATER-THAN-EQUALS-TO#0002"), 
                    Maps.hash("from", greaterThanEqualsTo.from()).map("to", greaterThanEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-GREATER-THAN-EQUALS-TO#0002", e);
        } catch (NoSuchMethodException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-GREATER-THAN-EQUALS-TO#0003"), 
                    Maps.hash("from", greaterThanEqualsTo.from()).map("to", greaterThanEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-GREATER-THAN-EQUALS-TO#0003", e);
        }
        if (f == null || t == null) return true;
        return Validators.greaterThanEqualsTo(Validators.toBigDecimal(f), Validators.toBigDecimal(t)) ? true : addErrors(context);
    }
    
    private boolean addErrors(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(greaterThanEqualsTo.message()).addPropertyNode(greaterThanEqualsTo.from()).addConstraintViolation();
        return false;
    }
}
