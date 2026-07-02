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
public class RequiredsTest {

    protected static Validator VALIDATOR;

    @RunWith(Theories.class)
    public static class CharSequenceArrayRequiredsTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[][] INVALIDS = {
            null, new String[] {

            }, new String[] {
                null
            }, new String[] {
                ""
            }, new String[] {
                "not empty", ""
            }, new String[] {
                "not empty", null
            }, new String[] {
                "not empty", "\t"
            }, new String[] {
                "not empty", " "
            }
        };

        @Theory
        public void invalid(String[] data) {

            CharSequenceArrayTargetBean target = new CharSequenceArrayTargetBean();
            target.values = data;
            Set<ConstraintViolation<CharSequenceArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            CharSequenceArrayTargetBean target = new CharSequenceArrayTargetBean();
            target.values = new String[] {
                "not empty", "not empty"
            };
            Set<ConstraintViolation<CharSequenceArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        public class CharSequenceArrayTargetBean {

            @Requireds
            public String[] values;
        }
    }

    @RunWith(Theories.class)
    public static class CharSequenceCollectionRequiredsTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final String[][] INVALIDS = {
            null, new String[] {
                null
            }, new String[] {
                ""
            }, new String[] {
                "not empty", ""
            }, new String[] {
                "not empty", null
            }, new String[] {
                "not empty", "\t"
            }, new String[] {
                "not empty", " "
            }
        };

        @Theory
        public void invalid(String[] data) {

            CharSequenceCollectionTargetBean target = new CharSequenceCollectionTargetBean();
            if (data != null) target.values = Arrays.asList(data);
            Set<ConstraintViolation<CharSequenceCollectionTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            CharSequenceCollectionTargetBean target = new CharSequenceCollectionTargetBean();
            target.values = Arrays.asList("not empty", "not empty!");
            Set<ConstraintViolation<CharSequenceCollectionTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

        class CharSequenceCollectionTargetBean {

            @Requireds
            public List<String> values;
        }
    }

    @RunWith(Theories.class)
    public static class ObjectArrayRequiredsTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @DataPoints
        public static final Integer[][] INVALIDS = {
            null, new Integer[] {}, new Integer[] {
                null
            }, new Integer[] {
                new Integer(0), null
            }
        };

        @Theory
        public void invalid(Integer[] datas) {

            class ObjectArrayTargetBean {

                @Requireds
                public Integer[] values;
            }
            ObjectArrayTargetBean target = new ObjectArrayTargetBean();
            target.values = datas;
            Set<ConstraintViolation<ObjectArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void custominvalid() {

            class ObjectArrayTargetBean {

                @Requireds
                public CustomBean[] beans = new CustomBean[] {
                    null
                };

                class CustomBean {

                    @SuppressWarnings("unused")
                    private String name;
                }
            }
            ObjectArrayTargetBean target = new ObjectArrayTargetBean();
            Set<ConstraintViolation<ObjectArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            class ObjectArrayTargetBean {

                @Requireds
                public Integer[] values;
            }
            ObjectArrayTargetBean target = new ObjectArrayTargetBean();
            target.values = new Integer[] {
                Integer.valueOf(1), Integer.valueOf(10)
            };
            Set<ConstraintViolation<ObjectArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }
    }

    public static class ObjectCollectionRequiredsTest {

        @Before
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void invalid() {

            class ObjectCollectionTargetBean {

                @Requireds
                public List<Integer> values;
            }
            ObjectCollectionTargetBean target = new ObjectCollectionTargetBean();
            Set<ConstraintViolation<ObjectCollectionTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));

            target.values = Collections.<Integer> emptyList();
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            List<Integer> elementEmpty = new ArrayList<Integer>();
            elementEmpty.add(null);
            target.values = elementEmpty;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Test
        public void valid() {

            class ObjectCollectionTargetBean {

                @Requireds
                public List<Integer> values;
            }
            ObjectCollectionTargetBean target = new ObjectCollectionTargetBean();
            target.values = Arrays.asList(new Integer[] {
                Integer.valueOf(1), Integer.valueOf(10)
            });
            Set<ConstraintViolation<ObjectCollectionTargetBean>> errors = VALIDATOR.validate(target);
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
        public static final String[][] INVALIDS = {
            null, new String[] {
                null
            }, new String[] {
                ""
            }, new String[] {
                "not empty", ""
            }, new String[] {
                "not empty", null
            }, new String[] {
                "not empty", "\t"
            }, new String[] {
                "not empty", " "
            }
        };

        @Theory
        public void default_message_test(String[] data) {

            class CharSequenceArrayTargetBean {

                @Requireds
                public String[] values;
            }
            CharSequenceArrayTargetBean target = new CharSequenceArrayTargetBean();
            target.values = data;
            Set<ConstraintViolation<CharSequenceArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "すべて必須入力です。");
        }

        @Theory
        public void override_message_test(String[] data) {

            class CharSequenceArrayTargetBean {

                @Requireds(message = "すべて入力してください。")
                public String[] values;
            }
            CharSequenceArrayTargetBean target = new CharSequenceArrayTargetBean();
            target.values = data;
            Set<ConstraintViolation<CharSequenceArrayTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "すべて入力してください。");
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