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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

import jp.co.ctc_g.jse.test.util.Validations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// ExpectedException removed - use assertThrows;
// RunWith removed - use @ExtendWith or @Nested;

// @Nested classes used instead of Enclosed
public class FixedEqualsToTest {

    protected static Validator VALIDATOR;

    public static class CharSequenceFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("foo")
                public String value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("foo")
                public String value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = "foo";
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("foo")
                public String value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = "bar";
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }
    }

    public static class DateFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2013/07/05")
                public Date value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() throws ParseException {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2013/07/05")
                public Date value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            target.value = sdf.parse("2013/07/05");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() throws ParseException {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2013/07/05")
                public Date value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            target.value = sdf.parse("2013/07/06");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public Date value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class IntegerFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2147483647")
                public Integer value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2147483647")
                public Integer value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Integer.MAX_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("2147483647")
                public Integer value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Integer.MIN_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public Integer value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class LongFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775807")
                public Long value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775807")
                public Long value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Long.MAX_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775807")
                public Long value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Long.MIN_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public Long value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class BigIntegerFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775808")
                public BigInteger value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775808")
                public BigInteger value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = new BigInteger("9223372036854775808");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("9223372036854775807")
                public BigInteger value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = new BigInteger("9223372036854775809");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public BigInteger value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class BigDecimalFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("0.0001")
                public BigDecimal value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("0.0001")
                public BigDecimal value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = new BigDecimal("0.0001");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("0.0001")
                public BigDecimal value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = new BigDecimal("0.00011");
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public BigDecimal value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class DoubleFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("1.7976931348623157E308")
                public Double value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("1.7976931348623157E308")
                public Double value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Double.MAX_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("1.7976931348623157E308")
                public Double value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Double.MIN_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public Double value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class FloatFixedEqualsToTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void notValidation() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("3.4028235E38")
                public Float value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void valid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("3.4028235E38")
                public Float value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Float.MAX_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        @Test
        public void invalid() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("3.4028235E38")
                public Float value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = Float.MIN_VALUE;
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void invalidParameter() {

            ValidationException ex = assertThrows(ValidationException.class, () -> {
                class FixedEqualsToTargetBean {
    
                    @FixedEqualsTo("foo")
                    public Float value;
                }
                FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000032"));
        }
    }

    public static class MessageTest {

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void default_message_test() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo("foo")
                public String value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = "bar";
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "fooと同じ値を入力してください。");
        }

        @Test
        public void override_message_test() {

            class FixedEqualsToTargetBean {

                @FixedEqualsTo(value = "foo", message = "{value}と一致しません。${validatedValue}")
                public String value;
            }
            FixedEqualsToTargetBean target = new FixedEqualsToTargetBean();
            target.value = "bar";
            Set<ConstraintViolation<FixedEqualsToTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "fooと一致しません。" + target.value);
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
