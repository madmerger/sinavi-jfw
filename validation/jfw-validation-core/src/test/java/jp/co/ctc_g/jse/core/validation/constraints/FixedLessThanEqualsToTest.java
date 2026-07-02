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
import java.math.BigInteger;
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
public class FixedLessThanEqualsToTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceFixedLessThanEqualsToTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "0.09", "0.1", "foo"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "0.11"
        };

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.1")
                public String value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = valid;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.1")
                public String value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    public static class NumberFixedLessThanEqualsToTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.5")
                public BigDecimal value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validByte() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("127")
                public Byte value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Byte.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = Byte.MAX_VALUE;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validShort() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("32767")
                public Short value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Short.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = Short.MAX_VALUE;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validInteger() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("2147483647")
                public Integer value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Integer.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = Integer.MAX_VALUE;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validLong() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("9223372036854775807")
                public Long value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Long.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = Long.MAX_VALUE;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigInteger() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("9223372036854775809")
                public BigInteger value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new BigInteger("9223372036854775808");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = new BigInteger("9223372036854775809");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validDouble() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.0011")
                public Double value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new Double("0.001");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = new Double("0.0011");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigDecimal() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.0011")
                public BigDecimal value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new BigDecimal("0.001");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));

            target.value = new BigDecimal("0.0011");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalidByte() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("126")
                public Byte value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Byte.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidShort() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("32766")
                public Short value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Short.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidInteger() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("2147483646")
                public Integer value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Integer.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidLong() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("9223372036854775806")
                public Long value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = Long.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidBigInteger() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("9223372036854775808")
                public BigInteger value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new BigInteger("9223372036854775809");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidDouble() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.001")
                public Double value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new Double("0.0011");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidBigDecimal() {

            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.001")
                public BigDecimal value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = new BigDecimal("0.0011");
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPattern() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("foo")
                public BigDecimal value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            VALIDATOR.validate(target);
        }
    }

    
    public static class ObjectFixedLessThanEqualsToTest {

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
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.5")
                public Object value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            VALIDATOR.validate(target);
        }
    }

    @RunWith(Theories.class)
    public static class MessageTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] INVALIDS = {
            "0.11"
        };

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo("0.1")
                public String value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1と同じかもしくは小さな値を入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanEqualsToTargetBean {

                @FixedLessThanEqualsTo(value = "0.1", message = "{value}以下の値のみ入力可能です。${validatedValue}")
                public String value;
            }
            FixedLessThanEqualsToTargetBean target = new FixedLessThanEqualsToTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1以下の値のみ入力可能です。" + invalid);
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
