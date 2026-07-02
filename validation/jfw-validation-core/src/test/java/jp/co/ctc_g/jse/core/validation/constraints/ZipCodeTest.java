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
public class ZipCodeTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceZipCodeTest {

        @DataPoints
        public static final String[] NOT_VALIDATION = {
            "", null
        };

        @DataPoints
        public static final String[] INVALID_SEPARATOR = {
            "－", "/", "@"
        };

        @DataPoints
        public static final String[] VALIDS1 = {
            "060", "100"
        };

        @DataPoints
        public static final String[] VALIDS2 = {
            "1234", "6080", "0068"
        };

        @DataPoints
        public static final String[] INVALIDS1 = {
            "009", "   ", "aaa", "あああ"
        };

        @DataPoints
        public static final String[] INVALIDS2 = {
            "aaaa", "    ", "----", "ああああ"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalidZip1(String valid1, String invalid2) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(valid1));
            Assume.assumeThat(Arrays.asList(INVALIDS2), hasItem(invalid2));
            class ZipCodeTargetBean {

                @ZipCode
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid1 + invalid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidZip2(String invalid1, String valid2) {

            Assume.assumeThat(Arrays.asList(INVALIDS1), hasItem(invalid1));
            Assume.assumeThat(Arrays.asList(VALIDS2), hasItem(valid2));
            class ZipCodeTargetBean {

                @ZipCode
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = invalid1 + valid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidSeparator(String invalid1, String invalid2, String separator) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(invalid1));
            Assume.assumeThat(Arrays.asList(VALIDS2), hasItem(invalid2));
            Assume.assumeThat(Arrays.asList(INVALID_SEPARATOR), hasItem(separator));
            class ZipCodeTargetBean {

                @ZipCode(separator = "-")
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = invalid1 + separator + invalid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void validNotValidation(String valid) {

            Assume.assumeThat(Arrays.asList(NOT_VALIDATION), hasItem(valid));
            class ZipCodeTargetBean {

                @ZipCode
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validNonSeparator(String valid1, String valid2) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(valid1));
            Assume.assumeThat(Arrays.asList(VALIDS2), hasItem(valid2));

            class ZipCodeTargetBean {

                @ZipCode
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid1 + valid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validWithSeparator(String valid1, String valid2) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(valid1));
            Assume.assumeThat(Arrays.asList(VALIDS2), hasItem(valid2));

            class ZipCodeTargetBean {

                @ZipCode(separator = "-")
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid1 + "-" + valid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }
    }

    
    public static class ObjectZipCodeTest {

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
            class ZipCodeTargetBean {

                @ZipCode
                public Object value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] VALIDS1 = {
            "060", "100"
        };

        @DataPoints
        public static final String[] INVALIDS2 = {
            "aaaa", "    ", "----", "ああああ"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String valid1, String invalid2) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(valid1));
            Assume.assumeThat(Arrays.asList(INVALIDS2), hasItem(invalid2));
            class ZipCodeTargetBean {

                @ZipCode
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid1 + invalid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "郵便番号形式で入力してください。");
        }

        @Theory
        public void override_message_test(String valid1, String invalid2) {

            Assume.assumeThat(Arrays.asList(VALIDS1), hasItem(valid1));
            Assume.assumeThat(Arrays.asList(INVALIDS2), hasItem(invalid2));
            class ZipCodeTargetBean {

                @ZipCode(message = "郵便番号形式のみ入力可能です。${validatedValue}")
                public String value;
            }
            ZipCodeTargetBean target = new ZipCodeTargetBean();
            target.value = valid1 + invalid2;
            Set<ConstraintViolation<ZipCodeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "郵便番号形式のみ入力可能です。" + target.value);
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
