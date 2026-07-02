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

package jp.co.ctc_g.jse.core.validation;

import static jp.co.ctc_g.jse.test.util.Validations.getExecutableValitaror;
import static jp.co.ctc_g.jse.test.util.Validations.getValidator;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Payload;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.executable.ValidateOnExecution;

import jp.co.ctc_g.jse.core.validation.constraints.Number;
import jp.co.ctc_g.jse.core.validation.constraints.NumericFormat;
import jp.co.ctc_g.jse.core.validation.constraints.NumericFormat.FormatType;
import jp.co.ctc_g.jse.test.bean.AnnotationTargetTypeBean;


import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class AnnotationTargetTypeTest {
    
    @BeforeClass
    public static void setup() {
        Locale.setDefault(new Locale("ja", "JP"));
    }

    protected static Validator VALIDATOR;
    protected static ExecutableValidator EXECUTABLE_VALIDATOR;

    @RunWith(Theories.class)
    public static class ConstructorTypeTest {

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "a", "A"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "1", "１"
        };

        @DataPoints
        public static final String[][] VALIDS_ARRAY = {
            null, {
                null, "", "a", "A"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS_ARRAY = {
            {
                "1", "１"
            }
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {
            EXECUTABLE_VALIDATOR = getExecutableValitaror();
        }

        @Test
        public void shouldThrowUnexpectedTypeException() throws NoSuchMethodException, SecurityException {
            thrown.expect(UnexpectedTypeException.class);
            thrown.expectMessage(containsString("HV000030"));
            Constructor<AnnotationTargetTypeBean> constructor = AnnotationTargetTypeBean.class.getConstructor();
            EXECUTABLE_VALIDATOR.validateConstructorReturnValue(constructor, new AnnotationTargetTypeBean());
        }

        @Theory
        public void validConstructorParameters(String valid) throws NoSuchMethodException, SecurityException {
            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            Constructor<AnnotationTargetTypeBean> constructor = AnnotationTargetTypeBean.class.getConstructor(String.class);
            Set<ConstraintViolation<AnnotationTargetTypeBean>> errors = EXECUTABLE_VALIDATOR.validateConstructorParameters(constructor, new String[] {
                valid
            });
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalidConstructorParameters(String invalid) throws NoSuchMethodException, SecurityException {
            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            Constructor<AnnotationTargetTypeBean> constructor = AnnotationTargetTypeBean.class.getConstructor(String.class);
            Set<ConstraintViolation<AnnotationTargetTypeBean>> errors = EXECUTABLE_VALIDATOR.validateConstructorParameters(constructor, new String[] {
                invalid
            });
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    @RunWith(Theories.class)
    public static class MethodTypeTest {

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "a", "A"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "1", "１"
        };

        @DataPoints
        public static final String[][] VALIDS_ARRAY = {
            null, {
                null, "", "a", "A"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS_ARRAY = {
            {
                "1", "１"
            }
        };

        @Before
        public void setup() {
            EXECUTABLE_VALIDATOR = getExecutableValitaror();
        }

        @Theory
        public void validParameterValue(String valid) throws NoSuchMethodException, SecurityException {
            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            AnnotationTargetTypeBean target = new AnnotationTargetTypeBean();
            Method method = AnnotationTargetTypeBean.class.getDeclaredMethod("setValue", String.class);
            Set<ConstraintViolation<AnnotationTargetTypeBean>> errors = EXECUTABLE_VALIDATOR.validateParameters(target, method, new String[] {
                valid
            });
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalidParameterValue(String invalid) throws NoSuchMethodException, SecurityException {
            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            AnnotationTargetTypeBean target = new AnnotationTargetTypeBean();
            Method method = AnnotationTargetTypeBean.class.getDeclaredMethod("setValue", String.class);
            Set<ConstraintViolation<AnnotationTargetTypeBean>> errors = EXECUTABLE_VALIDATOR.validateParameters(target, method, new String[] {
                invalid
            });
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "半角アルファベットで入力してください。");
        }

        @Theory
        public void validReturnValue(String valid) throws NoSuchMethodException, SecurityException {
            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            AnnotationTargetTypeBean target = new AnnotationTargetTypeBean();
            Method method = AnnotationTargetTypeBean.class.getMethod("getValue");
            Set<ConstraintViolation<AnnotationTargetTypeBean>> errors = EXECUTABLE_VALIDATOR.validateReturnValue(target, method, valid);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }
    }

    @RunWith(Theories.class)
    public static class AnnotationTypeValidatorTest {

        protected static Validator VALIDATOR;

        @DataPoints
        public static final String[] VALIDS = {
            null, "100,000", "-100,000"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "100000.00", "100,000.00"
        };

        @Before
        public void setup() {
            VALIDATOR = getValidator();
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @Constraint(validatedBy = {})
        @NumericFormat(type = FormatType.NUMBER_WITH_COMMA)
        @Number(precision = 6, signed = true)
        public @interface CustomNumber {

            String message() default "{}";

            Class<?>[] groups() default {};

            Class<? extends Payload>[] payload() default {};
        }

        @Theory
        public void valid(String valid) {
            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class NumberTargetBean {

                @CustomNumber
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = valid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {
            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            @ValidateOnExecution(type = ExecutableType.ALL)
            class NumberTargetBean {

                @CustomNumber
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(2));
            assertEqualsErrorMessages(errors, "6桁の整数値で入力してください。", "数値形式で入力してください。");
        }
    }

    private static void assertEqualsErrorMessages(Set<? extends ConstraintViolation<?>> errors, String... expectedMessages) {
        List<String> expectedMessagesAsList = Arrays.asList(expectedMessages);
        List<String> actualMessages = new ArrayList<String>();
        for (ConstraintViolation<?> error : errors) {
            actualMessages.add(error.getMessage());
        }
        Collections.sort(expectedMessagesAsList);
        Collections.sort(actualMessages);
        assertThat(actualMessages, is(expectedMessagesAsList));
    }
}
