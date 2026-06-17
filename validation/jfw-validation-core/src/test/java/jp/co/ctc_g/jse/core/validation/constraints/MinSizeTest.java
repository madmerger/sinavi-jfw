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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
import org.junit.experimental.theories.DataPoints;

import org.junit.experimental.theories.Theory;
// ExpectedException removed - use assertThrows;
// RunWith removed - use @ExtendWith or @Nested;

// @Nested classes used instead of Enclosed
public class MinSizeTest {

    protected static Validator VALIDATOR;

    
    @RunWith(Theories.class)
    public static class CharSequenceArrayMinSizeTest {

        @DataPoints
        public static final String[][] VALIDS = {
            null, {
                null, "", "1234567890", "abcdefghij", "ABCDEFGHIJ"
            }, {
                "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ", " ", "　"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ"
            }
        };

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(String[] valid) throws UnsupportedEncodingException {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class MinSizeTargetBean {

                @MinSize(5)
                public String[] value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = valid;
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MinSizeTargetBean {

                @MinSize(5)
                public String[] value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    
    @RunWith(Theories.class)
    public static class CharSequenceCollectionMinSizeTest {

        @DataPoints
        public static final String[][] VALIDS = {
            null, {
                null, "", "1234567890", "abcdefghij", "ABCDEFGHIJ"
            }, {
                "ＡＢＣＤＥＦＧＨＩＪ", "あいうえおかきくけこ", "アイウエオカキクケコ", "ｱｲｳｴｵｶｷｸｹｺ", " ", "　"
            }
        };

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ"
            }
        };

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void valid(String[] valid) throws UnsupportedEncodingException {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class MinSizeTargetBean {

                @MinSize(5)
                public List<String> value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            if (valid != null) target.value = Arrays.asList(valid);
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MinSizeTargetBean {

                @MinSize(5)
                public List<String> value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = Arrays.asList(invalid);
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    
    public static class MapMinSizeTest {

        private Map<String, String> map;

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
            map = new HashMap<String, String>();
            map.put("foo", "bar");
            map.put("bar", "foo");
        }

        @Test
        public void valid() throws UnsupportedEncodingException {

            class MinSizeTargetBean {

                @MinSize(2)
                public Map<String, String> value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
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

            class MinSizeTargetBean {

                @MinSize(3)
                public Map<String, String> value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = map;
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    
    public static class ObjectMinSizeTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void shouldThrowUnexpectedTypeException() {

            UnexpectedTypeException ex = assertThrows(UnexpectedTypeException.class, () -> {
                class MinSizeTargetBean {
    
                    @MinSize(5)
                    public Object value;
                }
                MinSizeTargetBean target = new MinSizeTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000030"));
        }
    }

    
    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[][] INVALIDS = {
            {
                null, "", "1234567890", "abcdefghij",
            }, {
                "ABCDEFGHIJ"
            }
        };

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MinSizeTargetBean {

                @MinSize(5)
                public String[] value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "5個以上で入力してください。");
        }

        @Theory
        public void override_message_test(String[] invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class MinSizeTargetBean {

                @MinSize(value = 5, message = "サイズが不足しています。最小:{value}")
                public String[] value;
            }
            MinSizeTargetBean target = new MinSizeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<MinSizeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "サイズが不足しています。最小:5");
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