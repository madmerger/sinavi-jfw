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
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.CoreMatchers;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

// hamcrest removed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// ExpectedException removed - use assertThrows;

public class BeforeEqualsToTest {

    private static Validator VALIDATOR;
    @BeforeEqualsTo(from = "", to = "")
    class BeforeEqualsToErrorBean {
    }

    @BeforeEqualsTo(from = "fromDate", to = "toDate")
    public class BeforeEqualsToInvocationTargetExceptionTestBean {

        public Date getFromDate() {

            throw new RuntimeException();
        }

        public Date getToDate() {

            throw new RuntimeException();
        }
    }

    @BeforeEqualsTo(from = "fromDate", to = "toDate")
    public class BeforeEqualsToTestBean {

        private java.util.Date fromDate;
        private java.util.Date toDate;

        public java.util.Date getToDate() {

            return toDate;
        }

        public void setToDate(java.util.Date toDate) {

            this.toDate = toDate;
        }

        public java.util.Date getFromDate() {

            return fromDate;
        }

        public void setFromDate(java.util.Date date) {

            this.fromDate = date;
        }
    }

    @BeforeEqualsTo.List({
        @BeforeEqualsTo(from = "fromDate1", to = "toDate1"), @BeforeEqualsTo(from = "fromDate2", to = "toDate2")
    })
    public class BeforeEqualsToListTestBean {

        private java.util.Date fromDate1;
        private java.util.Date toDate1;
        private java.util.Date fromDate2;
        private java.util.Date toDate2;

        public java.util.Date getToDate1() {

            return toDate1;
        }

        public void setToDate1(java.util.Date toDate) {

            this.toDate1 = toDate;
        }

        public java.util.Date getFromDate1() {

            return fromDate1;
        }

        public void setFromDate1(java.util.Date date) {

            this.fromDate1 = date;
        }

        public java.util.Date getFromDate2() {

            return fromDate2;
        }

        public void setFromDate2(java.util.Date fromDate2) {

            this.fromDate2 = fromDate2;
        }

        public java.util.Date getToDate2() {

            return toDate2;
        }

        public void setToDate2(java.util.Date toDate2) {

            this.toDate2 = toDate2;
        }
    }

    @BeforeEqualsTo.List({
        @BeforeEqualsTo(from = "fromDate1", to = "toDate1", message = "fromDate1はtoDate1と{jp.co.ctc_g.jse.core.validation.constraints.BeforeEqualsTo.message}"),
        @BeforeEqualsTo(from = "fromDate2", to = "toDate2", message = "fromDate2はtoDate2と同じかもしくは前の日付を入力してください。")
    })
    public class BeforeEqualsToListOverrideMessageTestBean {

        private java.util.Date fromDate1;
        private java.util.Date toDate1;
        private java.util.Date fromDate2;
        private java.util.Date toDate2;

        public java.util.Date getToDate1() {

            return toDate1;
        }

        public void setToDate1(java.util.Date toDate) {

            this.toDate1 = toDate;
        }

        public java.util.Date getFromDate1() {

            return fromDate1;
        }

        public void setFromDate1(java.util.Date date) {

            this.fromDate1 = date;
        }

        public java.util.Date getFromDate2() {

            return fromDate2;
        }

        public void setFromDate2(java.util.Date fromDate2) {

            this.fromDate2 = fromDate2;
        }

        public java.util.Date getToDate2() {

            return toDate2;
        }

        public void setToDate2(java.util.Date toDate2) {

            this.toDate2 = toDate2;
        }
    }

    @BeforeEach
    public void setup() {

        VALIDATOR = Validations.getValidator();
    }

    @Test
    public void no_such_method_exception_test() {

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            BeforeEqualsToErrorBean target = new BeforeEqualsToErrorBean();
            VALIDATOR.validate(target);
        });
        assertThat(ex.getMessage(), CoreMatchers.containsString("HV000028"));
    }

    @Test
    public void invocation_target_exception_test() {

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            BeforeEqualsToInvocationTargetExceptionTestBean target = new BeforeEqualsToInvocationTargetExceptionTestBean();
            VALIDATOR.validate(target);
        });
        assertThat(ex.getMessage(), CoreMatchers.containsString("HV000028"));
    }

    @Test
    public void validNullValue() {

        BeforeEqualsToTestBean target = new BeforeEqualsToTestBean();
        Set<ConstraintViolation<BeforeEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void valid() throws ParseException {

        BeforeEqualsToTestBean target = new BeforeEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/22"));
        Set<ConstraintViolation<BeforeEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
        target.setToDate(toDate("2013/07/23"));
        errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void validList() throws ParseException {

        BeforeEqualsToListTestBean target = new BeforeEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/22"));
        target.setToDate1(toDate("2013/07/22"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/23"));
        Set<ConstraintViolation<BeforeEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void invalid() throws ParseException {

        BeforeEqualsToTestBean target = new BeforeEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/21"));
        Set<ConstraintViolation<BeforeEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidListSomeErrors() throws ParseException {

        BeforeEqualsToListTestBean target = new BeforeEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<BeforeEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidSomeAllErrors() throws ParseException {

        BeforeEqualsToListTestBean target = new BeforeEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/19"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/19"));
        Set<ConstraintViolation<BeforeEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
    }

    @Test
    public void default_message_test() throws ParseException {

        BeforeEqualsToTestBean target = new BeforeEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/21"));
        Set<ConstraintViolation<BeforeEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
        assertEqualsErrorMessages(errors, "同じかもしくは前の日付を入力してください。");
    }

    @Test
    public void override_message_test() throws ParseException {

        BeforeEqualsToListOverrideMessageTestBean target = new BeforeEqualsToListOverrideMessageTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/19"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/19"));
        Set<ConstraintViolation<BeforeEqualsToListOverrideMessageTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
        assertEqualsErrorMessages(errors, "fromDate1はtoDate1と同じかもしくは前の日付を入力してください。", "fromDate2はtoDate2と同じかもしくは前の日付を入力してください。");
        assertEqualsPropertyPath(errors, "fromDate1", "fromDate2");
    }

    private Date toDate(String value) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(true);
        return sdf.parse(value);
    }

    private static void assertEqualsErrorMessages(Set<? extends ConstraintViolation<?>> errors, String... expectedMessages) {

        List<String> expectedMessagesAsList = Arrays.asList(expectedMessages);
        List<String> actualMessages = new ArrayList<String>();
        for (ConstraintViolation<?> error : errors) {
            actualMessages.add(error.getMessage());
        }
        Collections.sort(expectedMessagesAsList);
        Collections.sort(actualMessages);
        assertThat(actualMessages, is(expectedMessagesAsList));
    }

    private static void assertEqualsPropertyPath(Set<? extends ConstraintViolation<?>> errors, String... expectedPaths) {

        List<String> expectedPathsAsList = Arrays.asList(expectedPaths);
        List<String> actualPaths = new ArrayList<String>();
        for (ConstraintViolation<?> error : errors) {
            actualPaths.add(error.getPropertyPath().toString());
        }
        Collections.sort(expectedPathsAsList);
        Collections.sort(actualPaths);
        assertThat(actualPaths, is(expectedPathsAsList));
    }
}
