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
public class NumberTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceNumberTest {

        @DataPoints
        public static String NOT_VALIDS[] = {
            null, "", "+100", "abcde"
        };

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
            ".1", ".01", ".001", "．１", "．０１", "．００１"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void notValid(String notValid) {

            Assume.assumeThat(Arrays.asList(NOT_VALIDS), hasItem(notValid));
            class NumberTargetBean {

                @Number(precision = 4)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = notValid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void valid(String precision) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            class NumberTargetBean {

                @Number(precision = 4)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = precision;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validWithSign(String sign, String precision) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            class NumberTargetBean {

                @Number(precision = 4, signed = true)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = sign + precision;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() < 3);
            class NumberTargetBean {

                @Number(precision = 4)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidWithSign(String sign, String precision, String scale) {

            Assume.assumeThat(Arrays.asList(SIGN), hasItem(sign));
            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            class NumberTargetBean {

                @Number(precision = 4, signed = true)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = sign + precision + scale;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPrecision() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class NumberTargetBean {

                @Number(precision = -1)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            VALIDATOR.validate(target);
        }

    }

    @RunWith(Theories.class)
    public static class NumberNumberTest {

        @DataPoints
        public static Integer[] VALIDS = {
            null, 0, 1, 10, 100, 1000
        };

        @DataPoints
        public static Integer[] VALIDS_WITH_SIGN = {
            null, 0, 1, 10, 100, 1000, -1, -10, -100, -1000
        };

        @DataPoints
        public static Integer[] INVALIDS = {
            10000, -10000
        };

        @DataPoints
        public static Integer[] INVALIDS_WITH_SIGN = {
            10000, -10000
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(Integer valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class NumberTargetBean {

                @Number(precision = 4)
                public Integer value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = valid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validWithSign(Integer valid) {

            Assume.assumeThat(Arrays.asList(VALIDS_WITH_SIGN), hasItem(valid));
            class NumberTargetBean {

                @Number(precision = 4, signed = true)
                public Integer value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = valid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(Integer invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class NumberTargetBean {

                @Number(precision = 4)
                public Integer value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidWithSign(Integer invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_WITH_SIGN), hasItem(invalid));
            class NumberTargetBean {

                @Number(precision = 4, signed = true)
                public Integer value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPrecision() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class NumberTargetBean {

                @Number(precision = -1)
                public Integer value;
            }
            NumberTargetBean target = new NumberTargetBean();
            VALIDATOR.validate(target);
        }
    }

    
    public static class ObjectNumberTest {

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
            class NumberTargetBean {

                @Number(precision = 2)
                public Object value;
            }
            NumberTargetBean target = new NumberTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static String[] PRECISION = {
            "1", "10", "100", "1,000", "１", "１０", "１００", "１，０００", "0100"
        };

        @DataPoints
        public static String[] SCALE = {
            ".1", ".01", ".001", "．１", "．０１", "．００１"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() < 3);
            class NumberTargetBean {

                @Number(precision = 4)
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "4桁の整数値で入力してください。");
        }

        @Theory
        public void override_message_test(String precision, String scale) {

            Assume.assumeThat(Arrays.asList(PRECISION), hasItem(precision));
            Assume.assumeThat(Arrays.asList(SCALE), hasItem(scale));
            Assume.assumeTrue(precision.length() < 3);
            class NumberTargetBean {

                @Number(precision = 4, message = "{precision}桁の整数値のみ入力可能です。${validatedValue}")
                public String value;
            }
            NumberTargetBean target = new NumberTargetBean();
            target.value = precision + scale;
            Set<ConstraintViolation<NumberTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "4桁の整数値のみ入力可能です。" + target.value);
        }

        private static void assertEqualsErrorMessages(Set<? extends ConstraintViolation<?>> errors,
            String... expectedMessages) {

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
