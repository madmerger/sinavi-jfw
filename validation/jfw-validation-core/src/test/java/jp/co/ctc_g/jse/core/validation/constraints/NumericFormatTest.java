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

import jp.co.ctc_g.jse.core.validation.constraints.NumericFormat.FormatType;
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
public class NumericFormatTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceNumericFormatTest {

        @DataPoints
        public static String[] NOT_VALIDATIONS = {
            null, ""
        };

        @DataPoints
        public static String[] NUMBERS = {
            "0", "1234567890", "１２３４５６７８９０", "-1234567890", "－１２３４５６７８９０"
        };

        @DataPoints
        public static String[] INVALID_NUMBERS = {
            "abc", "ABC", "あ", "ア", "+1234567890", "+１２３４５６７８９０", "\\1234567890", "+", "\\"
        };

        @DataPoints
        public static String[] COMMA_NUMBERS = {
            "0", "999", "1,000", "1，000", "12,000", "123,000", "１，０００", "-999", "-1,000", "-1，000", "-12,000", "-123,000", "－１，０００"
        };

        @DataPoints
        public static String[] INVALID_COMMA_NUMBERS = {
            "abc", "ABC", "あ", "ア", ",999", "10,00", "100,0", "1000,", "，999", "010,000", "-,999"
        };

        @DataPoints
        public static String[] DECIMALS = {
            ".00", "．00"
        };

        @DataPoints
        public static String[] INVALID_DECIMALS = {
            ".", ".0.0", "．", "．０．０", "．0.0"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void notValidation(String notValidation) {

            Assume.assumeThat(Arrays.asList(NOT_VALIDATIONS), hasItem(notValidation));
            class NumericFormatTargetBean {

                @NumericFormat
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = notValidation;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validNumeric(String valid) {

            Assume.assumeThat(Arrays.asList(NUMBERS), hasItem(valid));
            class NumericFormatTargetBean {

                @NumericFormat
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = valid;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validNumericWithComma(String commaNumber) {

            Assume.assumeThat(Arrays.asList(COMMA_NUMBERS), hasItem(commaNumber));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.NUMBER_WITH_COMMA)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = commaNumber;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validDecimal(String numeric, String decimal) {

            Assume.assumeThat(Arrays.asList(NUMBERS), hasItem(numeric));
            Assume.assumeThat(Arrays.asList(DECIMALS), hasItem(decimal));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.DECIMAL)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = numeric + decimal;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validDecimalWithComma(String comma, String decimal) {

            Assume.assumeThat(Arrays.asList(COMMA_NUMBERS), hasItem(comma));
            Assume.assumeThat(Arrays.asList(DECIMALS), hasItem(decimal));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.DECIMAL_WITH_COMMA)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = comma + decimal;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validOther() {

            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.OTHER, pattern = "^[-]?([0]|[1-9][0-9]*)$")
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = "123456";
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalidNumber(String invalidNumber) {

            Assume.assumeThat(Arrays.asList(INVALID_NUMBERS), hasItem(invalidNumber));
            class NumericFormatTargetBean {

                @NumericFormat
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = invalidNumber;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidNumberWithComma(String invalidComma) {

            Assume.assumeThat(Arrays.asList(INVALID_COMMA_NUMBERS), hasItem(invalidComma));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.NUMBER_WITH_COMMA)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = invalidComma;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidDecimal(String number, String invalidDecimal) {

            Assume.assumeThat(Arrays.asList(NUMBERS), hasItem(number));
            Assume.assumeThat(Arrays.asList(INVALID_DECIMALS), hasItem(invalidDecimal));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.DECIMAL)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = number + invalidDecimal;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidDecimal2(String invalidNumber, String decimal) {

            Assume.assumeThat(Arrays.asList(INVALID_NUMBERS), hasItem(invalidNumber));
            Assume.assumeThat(Arrays.asList(DECIMALS), hasItem(decimal));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.DECIMAL)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = invalidNumber + decimal;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidDecimalWithComma(String commaNumber, String invalidDecimal) {

            Assume.assumeThat(Arrays.asList(COMMA_NUMBERS), hasItem(commaNumber));
            Assume.assumeThat(Arrays.asList(INVALID_DECIMALS), hasItem(invalidDecimal));
            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.DECIMAL_WITH_COMMA)
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = commaNumber + invalidDecimal;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidOther() {

            class NumericFormatTargetBean {

                @NumericFormat(type = FormatType.OTHER)
                public String value;
            }
            thrown.expect(ValidationException.class);
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            VALIDATOR.validate(target);
        }

    }

    public static class ObjectNumericFormatTest {

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
            class NumericFormatTargetBean {

                @NumericFormat
                public Object value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static String[] INVALID_NUMBERS = {
            "abc", "ABC", "あ", "ア", "+1234567890", "+１２３４５６７８９０", "\\1234567890", "+", "\\"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String invalidNumber) {

            Assume.assumeThat(Arrays.asList(INVALID_NUMBERS), hasItem(invalidNumber));
            class NumericFormatTargetBean {

                @NumericFormat
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = invalidNumber;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "数値形式で入力してください。");
        }

        @Theory
        public void override_message_test(String invalidNumber) {

            Assume.assumeThat(Arrays.asList(INVALID_NUMBERS), hasItem(invalidNumber));
            class NumericFormatTargetBean {

                @NumericFormat(message = "数値形式のみ入力可能です。${validatedValue}")
                public String value;
            }
            NumericFormatTargetBean target = new NumericFormatTargetBean();
            target.value = invalidNumber;
            Set<ConstraintViolation<NumericFormatTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "数値形式のみ入力可能です。" + invalidNumber);
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
