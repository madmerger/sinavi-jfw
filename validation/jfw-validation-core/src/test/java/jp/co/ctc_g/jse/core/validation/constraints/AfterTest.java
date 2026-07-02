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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AfterTest {

    private static Validator VALIDATOR;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @SuppressWarnings("unused")
    @After(from = "from", to = "to")
    class AfterNoSuchExceptionTestBean {

        private Date f;
        private Date t;
    }

    @After(from = "fromDate", to = "toDate")
    public class AfterInvocationTargetExceptionTestBean {

        public Date getFromDate() {

            throw new RuntimeException();
        }

        public Date getToDate() {

            throw new RuntimeException();
        }
    }

    @After(from = "fromDate", to = "toDate")
    public class AfterTestBean {

        private Date fromDate;
        private Date toDate;

        public Date getToDate() {

            return toDate;
        }

        public void setToDate(Date toDate) {

            this.toDate = toDate;
        }

        public Date getFromDate() {

            return fromDate;
        }

        public void setFromDate(Date date) {

            this.fromDate = date;
        }
    }

    @After.List({
        @After(from = "fromDate1", to = "toDate1"), @After(from = "fromDate2", to = "toDate2")
    })
    public class AfterListTestBean {

        private Date fromDate1;
        private Date toDate1;
        private Date fromDate2;
        private Date toDate2;

        public Date getToDate1() {

            return toDate1;
        }

        public void setToDate1(Date toDate) {

            this.toDate1 = toDate;
        }

        public Date getFromDate1() {

            return fromDate1;
        }

        public void setFromDate1(Date date) {

            this.fromDate1 = date;
        }

        public Date getFromDate2() {

            return fromDate2;
        }

        public void setFromDate2(Date fromDate2) {

            this.fromDate2 = fromDate2;
        }

        public Date getToDate2() {

            return toDate2;
        }

        public void setToDate2(Date toDate2) {

            this.toDate2 = toDate2;
        }
    }

    @After.List({
        @After(from = "fromDate1", to = "toDate1", message = "fromDate1はtoDate1よりも後の日付を入力してください。"),
        @After(from = "fromDate2", to = "toDate2", message = "fromDate2はtoDate2よりも{jp.co.ctc_g.jse.core.validation.constraints.After.message}")
    })
    public class AfterListOverrideMessageTestBean {

        private Date fromDate1;
        private Date toDate1;
        private Date fromDate2;
        private Date toDate2;

        public Date getToDate1() {

            return toDate1;
        }

        public void setToDate1(Date toDate) {

            this.toDate1 = toDate;
        }

        public Date getFromDate1() {

            return fromDate1;
        }

        public void setFromDate1(Date date) {

            this.fromDate1 = date;
        }

        public Date getFromDate2() {

            return fromDate2;
        }

        public void setFromDate2(Date fromDate2) {

            this.fromDate2 = fromDate2;
        }

        public Date getToDate2() {

            return toDate2;
        }

        public void setToDate2(Date toDate2) {

            this.toDate2 = toDate2;
        }
    }

    @Before
    public void setup() {

        VALIDATOR = Validations.getValidator();
    }

    @Test
    public void no_such_property_exception_test() {

        thrown.expect(ValidationException.class);
        AfterNoSuchExceptionTestBean t = new AfterNoSuchExceptionTestBean();
        VALIDATOR.validate(t);
    }

    @Test
    public void invocation_target_exception_test() {

        thrown.expect(ValidationException.class);
        AfterInvocationTargetExceptionTestBean t = new AfterInvocationTargetExceptionTestBean();
        VALIDATOR.validate(t);
    }

    @Test
    public void validNullValue() {

        AfterTestBean target = new AfterTestBean();
        Set<ConstraintViolation<AfterTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void valid() throws ParseException {

        AfterTestBean target = new AfterTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/21"));
        Set<ConstraintViolation<AfterTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void validList() throws ParseException {

        AfterListTestBean target = new AfterListTestBean();
        target.setFromDate1(toDate("2013/07/22"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(0));
    }

    @Test
    public void invalid() throws ParseException {

        AfterTestBean target = new AfterTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/23"));
        Set<ConstraintViolation<AfterTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
        target.setToDate(toDate("2013/07/22"));
        errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidListSomeErrors() throws ParseException {

        AfterListTestBean target = new AfterListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/22"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
    }

    @Test
    public void invalidListAllErrors() throws ParseException {

        AfterListTestBean target = new AfterListTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterListTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
    }

    @Test
    public void default_message_test() throws ParseException {

        AfterTestBean target = new AfterTestBean();
        target.setFromDate(toDate("2013/07/22"));
        target.setToDate(toDate("2013/07/23"));
        Set<ConstraintViolation<AfterTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(1));
        assertEqualsErrorMessages(errors, "後の日付を入力してください。");
    }

    @Test
    public void override_message_test() throws ParseException {

        AfterListOverrideMessageTestBean target = new AfterListOverrideMessageTestBean();
        target.setFromDate1(toDate("2013/07/20"));
        target.setToDate1(toDate("2013/07/21"));
        target.setFromDate2(toDate("2012/11/20"));
        target.setToDate2(toDate("2012/11/21"));
        Set<ConstraintViolation<AfterListOverrideMessageTestBean>> errors = VALIDATOR.validate(target);
        assertThat(errors, notNullValue());
        assertThat(errors.size(), is(2));
        assertEqualsErrorMessages(errors, "fromDate1はtoDate1よりも後の日付を入力してください。", "fromDate2はtoDate2よりも後の日付を入力してください。");
        assertEqualsPropertyPath(errors, "fromDate1", "fromDate2");
    }

    private Date toDate(String value) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
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
