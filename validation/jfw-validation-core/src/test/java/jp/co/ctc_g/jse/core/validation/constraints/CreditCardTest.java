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

import jp.co.ctc_g.jse.core.validation.constraints.CreditCard.Type;
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
public class CreditCardTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceCreditCardTest {

        @DataPoints
        public static final String[] VALIDS_VISA = {
            "", null, "4408041234567893", "4417123456789113"
        };

        @DataPoints
        public static final String[] VALIDS_CHINA_UNION_PAY = {
            "", null, "6234567890123456"
        };

        @DataPoints
        public static final String[] VALIDS_LUHN_ONLY = {
            "", null, "4408041234567893", "4417123456789113", "1234567890123452", "414", "6234567890123456"
        };

        @DataPoints
        public static final String[] INVALIDS_VISA = {
            "4408041234567890", "1234567890123452", "414"
        };

        @DataPoints
        public static final String[] INVALIDS_CHINA_UNION_PAY = {
            "623", "62______________"
        };

        @DataPoints
        public static final String[] INVALIDS_LUHN_ONLY = {
            "4408041234567890"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalidVisa(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_VISA), hasItem(invalid));
            class CreditCardTargetBean {

                @CreditCard(value = Type.VISA)
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidChinaUnionPay(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_CHINA_UNION_PAY), hasItem(invalid));
            class CreditCardTargetBean {

                @CreditCard(value = Type.CHINA_UNION_PAY)
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalidLuhnOnly(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_LUHN_ONLY), hasItem(invalid));
            class CreditCardTargetBean {

                @CreditCard
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void validVisa(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS_VISA), hasItem(valid));
            class CreditCardTargetBean {

                @CreditCard(value = Type.VISA)
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = valid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validChinaUnionPay(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS_CHINA_UNION_PAY), hasItem(valid));
            class CreditCardTargetBean {

                @CreditCard(value = Type.CHINA_UNION_PAY)
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = valid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validLuhnOnly(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS_LUHN_ONLY), hasItem(valid));
            class CreditCardTargetBean {

                @CreditCard
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = valid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

    }

    public static class ObjectCreditCardTest {

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
            class CreditCardTargetBean {

                @CreditCard(value = Type.VISA)
                public Object value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] INVALIDS_VISA = {
            "4408041234567890", "1234567890123452", "414"
        };

        @DataPoints
        public static final String[] INVALIDS_LUHN_ONLY = {
            "4408041234567890"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_VISA), hasItem(invalid));
            class CreditCardTargetBean {

                @CreditCard(value = Type.VISA)
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "クレジットカードの形式で入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS_LUHN_ONLY), hasItem(invalid));
            class CreditCardTargetBean {

                @CreditCard(message = "クレジットカード形式のみ入力可能です。${validatedValue}")
                public String value;
            }
            CreditCardTargetBean target = new CreditCardTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<CreditCardTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "クレジットカード形式のみ入力可能です。" + invalid);
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
