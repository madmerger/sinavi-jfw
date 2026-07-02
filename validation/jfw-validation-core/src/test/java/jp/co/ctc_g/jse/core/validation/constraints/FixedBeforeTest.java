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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
public class FixedBeforeTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceFixedBeforeTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "2013/07/04", "foo"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "2013/07/05", "2013/07/06"
        };

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            target.value = valid;
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validPattern(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class FixedBeforeTargetBean {

                @FixedBefore(value = "07/05/2013", pattern = "MM/dd/yyyy")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            target.value = valid;
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPattern() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class FixedBeforeTargetBean {

                @FixedBefore("2013-07-04")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            VALIDATOR.validate(target);
        }

    }

    public static class DateFixedBeforeTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() throws ParseException {

            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public Date value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() throws ParseException {

            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public Date value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            target.value = sdf.parse("2013/07/04");
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validPattern() throws ParseException {

            class FixedBeforeTargetBean {

                @FixedBefore(value = "07/05/13", pattern = "MM/dd/yy")
                public Date value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            target.value = sdf.parse("2013/07/04");
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() throws ParseException {

            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public Date value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            target.value = sdf.parse("2013/07/05");
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPattern() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class FixedBeforeTargetBean {

                @FixedBefore("2013-07-04")
                public Date value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            VALIDATOR.validate(target);
        }
    }

    
    public static class ObjectFixedBeforeTest {

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
            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public Object value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] INVALIDS = {
            "2013/07/05", "2013/07/06"
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedBeforeTargetBean {

                @FixedBefore("2013/07/05")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "2013/07/05よりも前の日付を入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedBeforeTargetBean {

                @FixedBefore(value = "2013/07/05", message = "{value}より前の日付のみ入力可能です。${validatedValue}")
                public String value;
            }
            FixedBeforeTargetBean target = new FixedBeforeTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedBeforeTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "2013/07/05より前の日付のみ入力可能です。" + invalid);
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
