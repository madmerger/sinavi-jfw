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

package jp.co.ctc_g.jse.core.validation.constraints.feature.beforeequalsto;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.validation.constraints.BeforeEqualsTo;
import jp.co.ctc_g.jse.core.validation.constraints.feature.after.AfterValidator;
import jp.co.ctc_g.jse.core.validation.util.Validators;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  このクラスは、{@link BeforeEqualsTo}バリデータの検証アルゴリズムを実装しています。
 * </p>
 * <p>
 * プロパティが比較対象のプロパティの値よりも日付として「前」あるいは「同じ」であるかどうかを検証します。 
 * </@>
 * @see BeforeEqualsTo
 */
public class BeforeEqualsToValidator implements ConstraintValidator<BeforeEqualsTo, Object> {

    private static final Logger L  = LoggerFactory.getLogger(BeforeEqualsToValidator.class);
    private static final ResourceBundle R = InternalMessages.getBundle(BeforeEqualsToValidator.class);
    
    private BeforeEqualsTo beforeEqualsTo;

    /**
     * デフォルトコンストラクタです。
     */
    public BeforeEqualsToValidator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(BeforeEqualsTo constraint) {
        this.beforeEqualsTo = constraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object suspect, ConstraintValidatorContext context) {
        Object f = null;
        Object t = null;
        try {
            f = PropertyUtils.getProperty(suspect, beforeEqualsTo.from());
            t = PropertyUtils.getProperty(suspect, beforeEqualsTo.to());
        } catch (IllegalAccessException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-BEFORE-EQUALS-TO#0001"), 
                    Maps.hash("from", beforeEqualsTo.from()).map("to", beforeEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-BEFORE-EQUALS-TO#0001", e);
        } catch (InvocationTargetException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-BEFORE-EQUALS-TO#0002"), 
                    Maps.hash("from", beforeEqualsTo.from()).map("to", beforeEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-BEFORE-EQUALS-TO#0002", e);
        } catch (NoSuchMethodException e) {
            if (L.isDebugEnabled()) {
                L.debug(Strings.substitute(R.getString("D-VALIDATOR-BEFORE-EQUALS-TO#0003"), 
                    Maps.hash("from", beforeEqualsTo.from()).map("to", beforeEqualsTo.to()).map("target", suspect.getClass().getSimpleName())));
            }
            throw new InternalException(AfterValidator.class, "E-VALIDATOR-BEFORE-EQUALS-TO#0003", e);
        }
        if (f == null || t == null) return true;
        return Validators.beforeEqualsTo(Validators.toDate(f, beforeEqualsTo.pattern()), Validators.toDate(t, beforeEqualsTo.pattern())) ? true : addErrors(context);
    }
    
    private boolean addErrors(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(beforeEqualsTo.message()).addPropertyNode(beforeEqualsTo.from()).addConstraintViolation();
        return false;
    }
}
