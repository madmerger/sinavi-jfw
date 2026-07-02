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

package jp.co.ctc_g.jse.core.validation.constraints;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

import jp.co.ctc_g.jse.test.util.Validations;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class DecimalTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceDecimalTest {

        @DataPoints
        public static String[] SIGN = {
            "", "-", "－"
        };

        @DataPoints
        public static String[] PRECISION = {
            "1", "10", "100", "1,000", "１", "１０", "１００", "１，０００", "0100"
        };

        @DataPoints
        public static String[] SCALE = {
            "", ".1", ".01", ".001", "．１", "．０１", "．００１"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() > 2);
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPrecision() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class DecimalTargetBean {

                @Decimal(precision = -1, scale = 0)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            VALIDATOR.validate(target);
        }

        @Test
        public void invalidScale() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = -1)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            VALIDATOR.validate(target);
        }

        @Test
        public void valid() {

            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = null;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void valid(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            class DecimalTargetBean {

                @Decimal(precision = 7, scale = 3)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validWithSigned(String sign, String precision, String scale) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            class DecimalTargetBean {

                @Decimal(precision = 7, scale = 3, signed = true)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = sign + precision + scale;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }
    }

    @RunWith(Theories.class)
    public static class NumberDecimalTest {

        @DataPoints
        public static String[] SIGN = {
            "", "-"
        };

        @DataPoints
        public static String[] PRECISION = {
            "1", "10", "100", "1000"
        };

        @DataPoints
        public static String[] SCALE = {
            "", ".1", ".01", ".001"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String sign, String precision, String scale) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() > 2);

            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = new BigDecimal(sign + precision + scale);
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPrecision() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class DecimalTargetBean {

                @Decimal(precision = -1, scale = 0)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            VALIDATOR.validate(target);
        }

        @Test
        public void invalidScale() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = -1)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            VALIDATOR.validate(target);
        }

        @Test
        public void valid() {

            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = null;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void valid(String sign, String precision, String scale) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(sign.length() == 0);
            Assume.assumeTrue(precision.length() < 3);
            Assume.assumeTrue(scale.length() == 0);

            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = new BigDecimal(sign + precision + scale);
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validWithSigned(String sign, String precision, String scale) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));

            class DecimalTargetBean {

                @Decimal(precision = 7, scale = 3, signed = true)
                public BigDecimal value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = new BigDecimal(sign + precision + scale);
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }
    }

    public static class ObjectDecimalTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void shouldThrowUnexpectedTypeException() {

            thrown.expect(UnexpectedTypeException.class);
            thrown.expectMessage(containsString("HV000030"));
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public Object value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static String[] SIGN = {
            "", "-", "－"
        };

        @DataPoints
        public static String[] PRECISION = {
            "1", "10", "100", "1,000", "１", "１０", "１００", "１，０００", "0100"
        };

        @DataPoints
        public static String[] SCALE = {
            "", ".1", ".01", ".001", "．１", "．０１", "．００１"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() > 2);
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0)
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "2桁(小数点以下0桁)の小数値で入力してください。");
        }

        @Theory
        public void override_message_test(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() > 2);
            class DecimalTargetBean {

                @Decimal(precision = 2, scale = 0, message = "小数値のみ入力可能です。整数部:{precision} 小数部:{scale}${validatedValue}")
                public String value;
            }
            DecimalTargetBean target = new DecimalTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<DecimalTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "小数値のみ入力可能です。整数部:2 小数部:0" + precision + scale);
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
}
