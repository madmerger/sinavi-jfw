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

public class AfterEqualsToTest {

    private static Validator VALIDATOR;
    @BeforeEach
    public void setup() {

        VALIDATOR = Validations.getValidator();
    }

    @AfterEqualsTo(from = "", to = "")
    class AfterEqualsToErrorTestBean {
    }

    @AfterEqualsTo(from = "fromDate", to = "toDate")
    public class AfterEqualsToInvocationTargetExceptionTestBean {

        public Date getFromDate() {

            throw new RuntimeException();
        }

        public Date getToDate() {

            throw new RuntimeException();
        }
    }

    @AfterEqualsTo(from = "fromDate", to = "toDate")
    public class AfterEqualsToTestBean {

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

    @AfterEqualsTo.List({
        @AfterEqualsTo(from = "fromDate1", to = "toDate1"), @AfterEqualsTo(from = "fromDate2", to = "toDate2")
    })
    public class AfterEqualsToListTestBean {

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

    @AfterEqualsTo.List({
        @AfterEqualsTo(from = "fromDate1", to = "toDate1", message = "fromDate1はtoDate1と同じかもしくは後の日付を入力してください。"),
        @AfterEqualsTo(from = "fromDate2", to = "toDate2", message = "fromDate2はtoDate2と{jp.co.ctc_g.jse.core.validation.constraints.AfterEqualsTo.message}")
    })
    public class AfterEqualsToListOverrideMessageTestBean {

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

    @Test
    public void no_such_method_exception_test() {
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            AfterEqualsToErrorTestBean target = new AfterEqualsToErrorTestBean();
            VALIDATOR.validate(target);
        });
        assertThat(ex.getMessage(), CoreMatchers.containsString("HV000028"));
    }

    @Test
    public void invocation_target_exception_test() {

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            AfterEqualsToInvocationTargetExceptionTestBean target = new AfterEqualsToInvocationTargetExceptionTestBean();
            VALIDATOR.validate(target);
        });
        assertThat(ex.getMessage(), CoreMatchers.containsString("HV000028"));
    }

    @Test
    public void validNullValue() {

        AfterEqualsToTestBean target = new AfterEqualsToTestBean();
        Set<ConstraintViolation<AfterEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void valid() throws ParseException {

        AfterEqualsToTestBean target = new AfterEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/21"));
        Set<ConstraintViolation<AfterEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void validList() throws ParseException {

        AfterEqualsToListTestBean target = new AfterEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/22"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/22"));
        Set<ConstraintViolation<AfterEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void invalid() throws ParseException {

        AfterEqualsToTestBean target = new AfterEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/23"));
        Set<ConstraintViolation<AfterEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidListSomeErrors() throws ParseException {

        AfterEqualsToListTestBean target = new AfterEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidListAllErrors() throws ParseException {

        AfterEqualsToListTestBean target = new AfterEqualsToListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterEqualsToListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
    }

    @Test
    public void default_message_test() throws ParseException {

        AfterEqualsToTestBean target = new AfterEqualsToTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/23"));
        Set<ConstraintViolation<AfterEqualsToTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors.size(), is(1));
        assertEqualsErrorMessages(errors, "同じかもしくは後の日付を入力してください。");
    }

    @Test
    public void override_message_test() throws ParseException {

        AfterEqualsToListOverrideMessageTestBean target = new AfterEqualsToListOverrideMessageTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterEqualsToListOverrideMessageTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
        assertEqualsErrorMessages(errors, "fromDate1はtoDate1と同じかもしくは後の日付を入力してください。", "fromDate2はtoDate2と同じかもしくは後の日付を入力してください。");
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
