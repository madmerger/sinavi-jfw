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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class KatakanaTest {

    protected static Validator VALIDATOR;

    
    @RunWith(Theories.class)
    public static class CharSequenceKatakanaTest {

        @DataPoints
        public static final String[] VALIDS = {
            null, "", "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン", "ガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポァィゥェォャュョッ"
        };

        @DataPoints
        public static final String[] INVALIDS = {
            "ゐ", "ゑ", "ゔ", "!\"#$%&'()=~|`{+*}<>?_-^\\@[;:]./", "！”＃＄％＆’（）＝￣｜‘｛＋＊｝＜＞？＿－＾￥￥＠［；：］．／",
            "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん", "がぎぐげござじずぜぞだぢづでどばびぶべぼ", "ぱぴぷぺぽ", "ぁぃぅぇぉっゃゅょー",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝﾞﾟｧｨｩｪｫｬｭｮｯ", " 　\t", "\n\r", String.valueOf('\u3040'),
            String.valueOf('\u308e'), String.valueOf('\u3090'), String.valueOf('\u3091'), String.valueOf('\u3094'),
            String.valueOf('\u3095'), String.valueOf('\u3096'), String.valueOf('\u3097'), String.valueOf('\u3098'),
            String.valueOf('\u3099'), String.valueOf('\u309a'), String.valueOf('\u309b'), String.valueOf('\u309d'),
            String.valueOf('\u309e'), String.valueOf('\u309f')
        };

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class KatakanaTargetBean {

                @Katakana
                public String value;
            }
            KatakanaTargetBean target = new KatakanaTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<KatakanaTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void invalid(String valid, String invalid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class KatakanaTargetBean {

                @Katakana
                public String value;
            }
            KatakanaTargetBean target = new KatakanaTargetBean();
            target.value = valid + invalid;
            Set<ConstraintViolation<KatakanaTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            target.value = invalid + valid;
            errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
        }

        @Theory
        public void valid(String valid) {

            Assume.assumeThat(Arrays.asList(VALIDS), hasItem(valid));
            class KatakanaTargetBean {

                @Katakana
                public String value;
            }
            KatakanaTargetBean target = new KatakanaTargetBean();
            target.value = valid;
            Set<ConstraintViolation<KatakanaTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(0));
        }

    }

    
    public static class ObjectKatakanaTest {
        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Test
        public void shouldThrowUnexpectedTypeException() {

            UnexpectedTypeException ex = assertThrows(UnexpectedTypeException.class, () -> {
                class KatakanaTargetBean {
    
                    @Katakana
                    public Object value;
                }
                KatakanaTargetBean target = new KatakanaTargetBean();
                VALIDATOR.validate(target);
            });
            assertThat(ex.getMessage(), containsString("HV000030"));
        }
    }

    
    @RunWith(Theories.class)
    public static class MessageTest {

        @DataPoints
        public static final String[] INVALIDS = {
            "ゐ", "ゑ", "ゔ", "!\"#$%&'()=~|`{+*}<>?_-^\\@[;:]./", "！”＃＄％＆’（）＝￣｜‘｛＋＊｝＜＞？＿－＾￥￥＠［；：］．／",
            "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん", "がぎぐげござじずぜぞだぢづでどばびぶべぼ", "ぱぴぷぺぽ", "ぁぃぅぇぉっゃゅょー",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝﾞﾟｧｨｩｪｫｬｭｮｯ", " 　\t", "\n\r", String.valueOf('\u3040'),
            String.valueOf('\u308e'), String.valueOf('\u3090'), String.valueOf('\u3091'), String.valueOf('\u3094'),
            String.valueOf('\u3095'), String.valueOf('\u3096'), String.valueOf('\u3097'), String.valueOf('\u3098'),
            String.valueOf('\u3099'), String.valueOf('\u309a'), String.valueOf('\u309b'), String.valueOf('\u309d'),
            String.valueOf('\u309e'), String.valueOf('\u309f')
        };

        @BeforeEach
        public void setup() {

            VALIDATOR = Validations.getValidator();
        }

        @Theory
        public void invalid(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class KatakanaTargetBean {

                @Katakana
                public String value;
            }
            KatakanaTargetBean target = new KatakanaTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<KatakanaTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "全角カタカナで入力してください。");
        }

        @Theory
        public void override_message_test(String invalid) {

            Assume.assumeThat(Arrays.asList(INVALIDS), hasItem(invalid));
            class KatakanaTargetBean {

                @Katakana(message = "全角カナのみ入力可能です。${validatedValue}")
                public String value;
            }
            KatakanaTargetBean target = new KatakanaTargetBean();
            target.value = invalid;
            Set<ConstraintViolation<KatakanaTargetBean>> errors = VALIDATOR.validate(target);
            assertThat(errors, notNullValue());
            assertThat(errors.size(), is(1));
            assertEqualsErrorMessages(errors, "全角カナのみ入力可能です。" + invalid);
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
