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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class MaxSizeTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceArrayMaxSizeTest {

        @DataPoints
        public static final String[][] VALIDS = {
            null, {
                null, "", "1234567890", "abcdefghij", "ABCDEFGHIJ"
            }, {
                "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", " ", "　", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ", "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ", "亜衣兎絵御課記句家子"
            }
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(String[] valid) throws UnsupportedEncodingException {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class MaxSizeTargetBean {

                @MaxSize(5)
                public String[] value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = valid;
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxSizeTargetBean {

                @MaxSize(5)
                public String[] value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    @RunWith(Theories.class)
    public static class CharSequenceCollectionMaxSizeTest {

        @DataPoints
        public static final String[][] VALIDS = {
            null, {
                null, "", "1234567890", "abcdefghij", "ABCDEFGHIJ"
            }, {
                "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", " ", "　", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ", "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ", "亜衣兎絵御課記句家子"
            }
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(String[] valid) throws UnsupportedEncodingException {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class MaxSizeTargetBean {

                @MaxSize(5)
                public List<String> value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            if (valid != null) target.value = Arrays.asList(valid);
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

        }

        @Theory
        public void invalid(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxSizeTargetBean {

                @MaxSize(5)
                public List<String> value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = Arrays.asList(invalid);
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    
    public static class MapMaxSizeTest {

        private Map<String, String> map;

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
            map = new HashMap<String, String>();
            map.put("foo", "bar");
            map.put("bar", "foo");
        }

        @Test
        public void valid() throws UnsupportedEncodingException {

            class MaxSizeTargetBean {

                @MaxSize(3)
                public Map<String, String> value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = map;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            map.put("foo.bar", "bar.foo");
            target.value = map;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class MaxSizeTargetBean {

                @MaxSize(1)
                public Map<String, String> value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = map;
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    
    public static class ObjectMaxSizeTest {

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
            class MaxSizeTargetBean {

                @MaxSize(5)
                public Object value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", " ", "　", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ", "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ", "亜衣兎絵御課記句家子"
            }
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxSizeTargetBean {

                @MaxSize(5)
                public String[] value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "5個以内で入力してください。");
        }

        @Theory
        public void override_message_test(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MaxSizeTargetBean {

                @MaxSize(value = 5, message = "最大サイズを超えています。最大:{value}")
                public String[] value;
            }
            MaxSizeTargetBean target = new MaxSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MaxSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "最大サイズを超えています。最大:5");
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
