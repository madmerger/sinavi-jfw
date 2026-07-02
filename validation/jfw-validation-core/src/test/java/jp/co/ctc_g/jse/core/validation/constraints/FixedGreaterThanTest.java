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
public class FixedGreaterThanTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceFixedGreaterThanTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "0.11", "foo"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "0.09", "0.1"
        };

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.1")
                public String value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = valid;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.1")
                public String value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    public static class NumberFixedGreaterThanTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.5")
                public BigDecimal value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validByte() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("126")
                public Byte value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Byte.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validShort() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("32766")
                public Short value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Short.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validInteger() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("2147483646")
                public Integer value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Integer.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validLong() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("9223372036854775806")
                public Long value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Long.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigInteger() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("9223372036854775808")
                public BigInteger value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new BigInteger("9223372036854775809");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validDouble() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.001")
                public Double value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new Double("0.0011");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigDecimal() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.001")
                public BigDecimal value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new BigDecimal("0.0011");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalidByte() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("127")
                public Byte value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Byte.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Byte.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidShort() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("32767")
                public Short value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Short.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Short.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidInteger() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("2147483647")
                public Integer value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Integer.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Integer.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidLong() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("9223372036854775807")
                public Long value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = Long.MAX_VALUE;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Long.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidBigInteger() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("9223372036854775809")
                public BigInteger value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new BigInteger("9223372036854775809");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new BigInteger("9223372036854775808");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidDouble() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.0011")
                public Double value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new Double("0.0011");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new Double("0.001");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidBigDecimal() {

            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.0011")
                public BigDecimal value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = new BigDecimal("0.0011");
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new BigDecimal("0.0011");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPattern() {

            thrown.expect(ValidationException.class);
            thrown.expectMessage(containsString("HV000032"));
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("foo")
                public BigDecimal value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            VALIDATOR.validate(target);
        }
    }

    
    public static class ObjectFixedGreaterThanTest {

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
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.5")
                public Object value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
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
            "0.09", "0.1"
        };

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan("0.1")
                public String value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1よりも大きな値を入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedGreaterThanTargetBean {

                @FixedGreaterThan(value = "0.1", message = "{value}を超える値のみ入力可能です。${validatedValue}")
                public String value;
            }
            FixedGreaterThanTargetBean target = new FixedGreaterThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedGreaterThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1を超える値のみ入力可能です。" + invalid);
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
