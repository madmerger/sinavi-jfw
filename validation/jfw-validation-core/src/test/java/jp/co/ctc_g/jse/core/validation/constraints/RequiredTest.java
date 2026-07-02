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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import jp.co.ctc_g.jse.test.util.Validations;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class RequiredTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceRequiredTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[] INVALIDS = {
            null, "\t", "\n", "\f", "\r", " ", "　", ""
        };

        @Theory
        public void invalid(String data) {

            CharSequenceTargetBean target = new CharSequenceTargetBean();
            target.value = data;
            Set<ConstraintViolation<CharSequenceTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            CharSequenceTargetBean target = new CharSequenceTargetBean();
            target.value = "not empty";
            Set<ConstraintViolation<CharSequenceTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        class CharSequenceTargetBean {

            @Required
            public String value;
        }
    }

    
    public static class ObjectRequiredTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void integerinvalid() {

            class ObjectTargetBean {

                @Required
                public Integer value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void longinvalid() {

            class ObjectTargetBean {

                @Required
                public Long value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void floatinvalid() {

            class ObjectTargetBean {

                @Required
                public Float value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void doubleinvalid() {

            class ObjectTargetBean {

                @Required
                public Double value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void booleaninvalid() {

            class ObjectTargetBean {

                @Required
                public Boolean value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void bigIntegerinvalid() {

            class ObjectTargetBean {

                @Required
                public BigInteger value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void bigDecimalinvalid() {

            class ObjectTargetBean {

                @Required
                public BigDecimal value;
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void custominvalid() {

            class ObjectTargetBean {

                @Required
                public CustomBean bean;

                class CustomBean {

                    @SuppressWarnings("unused")
                    private String name;
                }
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            class ObjectTargetBean {

                @Required
                public CustomBean bean = new CustomBean();

                class CustomBean {

                    @SuppressWarnings("unused")
                    private String name;
                }
            }
            ObjectTargetBean target = new ObjectTargetBean();
            Set<ConstraintViolation<ObjectTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
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
            null, "\t", "\n", "\f", "\r", " ", "　", ""
        };

        @Theory
        public void default_message_test(String data) {

            class DefaultMessageBean {

                @Required
                public String value;
            }
            DefaultMessageBean target = new DefaultMessageBean();
            target.value = data;
            Set<ConstraintViolation<DefaultMessageBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "必須入力です。");
        }

        @Theory
        public void override_message_test(String data) {

            class DefaultMessageBean {

                @Required(message = "入力してください。")
                public String value;
            }
            DefaultMessageBean target = new DefaultMessageBean();
            target.value = data;
            Set<ConstraintViolation<DefaultMessageBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "入力してください。");
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