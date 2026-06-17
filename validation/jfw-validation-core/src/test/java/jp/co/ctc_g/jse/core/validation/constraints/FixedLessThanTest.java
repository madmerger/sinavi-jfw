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
public class FixedLessThanTest {

    protected static Validator VALIDATOR;

    
    @RunWith(Theories.class)
    public static class CharSequenceFixedLessThanTest {

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "0.09", "foo"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "0.1", "0.11"
        };

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class FixedLessThanTargetBean {

                @FixedLessThan("0.1")
                public String value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = valid;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanTargetBean {

                @FixedLessThan("0.1")
                public String value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

    }

    public static class NumberFixedLessThanTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedLessThanTargetBean {

                @FixedLessThan("0.5")
                public BigDecimal value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validByte() {

            class FixedLessThanTargetBean {

                @FixedLessThan("127")
                public Byte value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Byte.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validShort() {

            class FixedLessThanTargetBean {

                @FixedLessThan("32767")
                public Short value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Short.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validInteger() {

            class FixedLessThanTargetBean {

                @FixedLessThan("2147483647")
                public Integer value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Integer.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validLong() {

            class FixedLessThanTargetBean {

                @FixedLessThan("9223372036854775807")
                public Long value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Long.MAX_VALUE - 1;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigInteger() {

            class FixedLessThanTargetBean {

                @FixedLessThan("9223372036854775809")
                public BigInteger value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new BigInteger("9223372036854775808");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validDouble() {

            class FixedLessThanTargetBean {

                @FixedLessThan("0.0011")
                public Double value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new Double("0.001");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void validBigDecimal() {

            class FixedLessThanTargetBean {

                @FixedLessThan("0.0011")
                public BigDecimal value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new BigDecimal("0.001");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalidByte() {

            class FixedLessThanTargetBean {

                @FixedLessThan("126")
                public Byte value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Byte.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Byte.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidShort() {

            class FixedLessThanTargetBean {

                @FixedLessThan("32766")
                public Short value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Short.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Short.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidInteger() {

            class FixedLessThanTargetBean {

                @FixedLessThan("2147483646")
                public Integer value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Integer.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Integer.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidLong() {

            class FixedLessThanTargetBean {

                @FixedLessThan("9223372036854775806")
                public Long value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = Long.MAX_VALUE;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = Long.MAX_VALUE - 1;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidBigInteger() {

            class FixedLessThanTargetBean {

                @FixedLessThan("9223372036854775808")
                public BigInteger value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new BigInteger("9223372036854775809");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new BigInteger("9223372036854775808");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidDouble() {

            class FixedLessThanTargetBean {

                @FixedLessThan("0.001")
                public Double value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new Double("0.0011");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new Double("0.001");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));

        }

        @Test
        public void invalidBigDecimal() {

            class FixedLessThanTargetBean {

                @FixedLessThan("0.001")
                public BigDecimal value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = new BigDecimal("0.0011");
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = new BigDecimal("0.001");
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidPattern() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedLessThanTargetBean {
    
                    @FixedLessThan("foo")
                    public BigDecimal value;
                }
                FixedLessThanTargetBean target = new FixedLessThanTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    
    public static class ObjectFixedLessThanTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void shouldThrowUnexpectedTypeException() {

            UnexpectedTypeException ex = assertThrows(UnexpectedTypeException.class, () -> {
                class FixedLessThanTargetBean {
    
                    @FixedLessThan("0.5")
                    public Object value;
                }
                FixedLessThanTargetBean target = new FixedLessThanTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000030"));
        }
    }

    
    @RunWith(Theories.class)
    public static class MessageTest {

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] INVALIDS = {
            "0.1", "0.11"
        };

        @Theory
        public void default_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanTargetBean {

                @FixedLessThan("0.1")
                public String value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1より小さい値を入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class FixedLessThanTargetBean {

                @FixedLessThan(value = "0.1", message = "{value}未満の値のみ入力可能です。${validatedValue}")
                public String value;
            }
            FixedLessThanTargetBean target = new FixedLessThanTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<FixedLessThanTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "0.1未満の値のみ入力可能です。" + invalid);
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
