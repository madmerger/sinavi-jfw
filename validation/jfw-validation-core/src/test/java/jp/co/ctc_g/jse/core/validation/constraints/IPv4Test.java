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
public class IPv4Test {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceIPv4Test {

        @DataPoints
        public static final String[] VALIDS = {
            null, "0.0.0.0", "255.255.255.255"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "255.255.255.256", "10.0.0.0/8", "...", "a.a.a.a", "0-0-0-0", "::1", "10.0.0.1.", " 10.01.01.01", " "
        };

        @DataPoints
        public static final String[] ONLY_VALIDS = {
            null, "192.168.0.0", "192.168.0.255", "172.16.0.0", "172.16.255.255", "10.0.0.0", "10.255.255.255"
        };

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class IPv4TargetBean {

                @IPv4
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = invalid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidOnly() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class IPv4TargetBean {

                @IPv4(only = {
                    "foo", "bar"
                })
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            VALIDATOR.validate(target);
        }

        @Test
        public void invalidCidr() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class IPv4TargetBean {

                @IPv4(only = {
                    "192.168.0.0"
                })
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            VALIDATOR.validate(target);
        }

        @Theory
        public void invalidIPv4Only(String valid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(valid));
            class IPv4TargetBean {

                @IPv4(only = {
                    "192.168.0.0/24", "172.16.0.0/16", "10.0.0.0/8"
                })
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = valid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class IPv4TargetBean {

                @IPv4
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = valid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void validIPv4Only(String valid) {

            Assume.assumeThat(Arrays.asList(ONLY_VALIDS), hasItem(valid));
            class IPv4TargetBean {

                @IPv4(only = {
                    "192.168.0.0/24", "172.16.0.0/16", "10.0.0.0/8"
                })
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = valid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

    }

    
    public static class ObjectIPv4Test {

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
            class IPv4TargetBean {

                @IPv4
                public Object value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] INVALIDS = {
            "255.255.255.256", "10.0.0.0/8", "...", "a.a.a.a", "0-0-0-0", "::1", "10.0.0.1.", " 10.01.01.01", " "
        };

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class IPv4TargetBean {

                @IPv4
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = invalid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "IPv4の形式で入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class IPv4TargetBean {

                @IPv4(message = "IPv4形式のみ入力可能です。${validatedValue}")
                public String value;
            }
            IPv4TargetBean target = new IPv4TargetBean();
            target.value = invalid;
            Set<ConstraintViolation<IPv4TargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "IPv4形式のみ入力可能です。" + invalid);
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
