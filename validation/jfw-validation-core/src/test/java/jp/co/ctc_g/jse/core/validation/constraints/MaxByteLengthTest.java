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
public class MaxByteLengthTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceMaxByteLengthTest {

        @DataPoints
        public static final String[] VALIDS = {
            "", null, "あいうえお", "abcdefghijklnmo", "123456789012345", "アイウエオ", "ｱｲｳｴｵ", "！”＃＄％", "!\"#$%'()=~|@{}+", "①㈱㈲〒～"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "あいうえおか", "abcdefghijklnmop", "1234567890123456", "アイウエオカ", "ｱｲｳｴｵｶ", "!\"#$%'()=~|@{}+/", "①㈱㈲〒～亜"
        };

        @DataPoints
        public static final String[] SJIS_VALIDS = {
            "", null, "エスジス", "ｴｽｼﾞｽ", "SHIFT_JIS", "123456789012345", "！”＃＄％｛｝", "!\"#$%'()=~|@{}+", "①㈱㈲〒～"
        };

        @DataPoints
        public static final String[] SJIS_INVALIDS = {
            "エスジスダメダメ", "ｴｽｼﾞｽﾀﾞﾒﾀﾞﾒﾀﾞﾒﾀﾞﾒ", "INVALID SHIFT_JIS!", "1234567890123456", "！”＃＄％｛｝？＠", "!\"#$%'()=~|@{}+?"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(15)
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = valid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(15)
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void validShiftJIS(String valid) {

            Assume.assumeThat(Arrays.asList(SJIS_VALIDS), hasItem(valid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(value = 15, encoding = "Shift_JIS")
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = valid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalidShiftJIS(String invalid) {

            Assume.assumeThat(Arrays.asList(SJIS_INVALIDS), hasItem(invalid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(value = 15, encoding = "Shift_JIS")
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000028"));
            class MaxByteLengthTargetBean {

                @MaxByteLength(value = 15, encoding = "INVALID")
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = "foo";
            VALIDATOR.validate(target);
        }

    }

    public static class ObjectMaxByteLengthTest {

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
            class MaxByteLengthTargetBean {

                @MaxByteLength(100)
                public Object value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] INVALIDS = {
            "あいうえおか", "abcdefghijklnmop", "1234567890123456", "アイウエオカ", "ｱｲｳｴｵｶ", "!\"#$%'()=~|@{}+/", "①㈱㈲〒～亜"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(15)
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "15バイト長以内で入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxByteLengthTargetBean {

                @MaxByteLength(value = 15, message = "入力可能なバイト数を超えています。最大:{value}${validatedValue}")
                public String value;
            }
            MaxByteLengthTargetBean target = new MaxByteLengthTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxByteLengthTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "入力可能なバイト数を超えています。最大:15" + invalid);
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
