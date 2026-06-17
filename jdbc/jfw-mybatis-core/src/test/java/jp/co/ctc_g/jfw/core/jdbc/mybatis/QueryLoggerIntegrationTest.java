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

package jp.co.ctc_g.jfw.core.jdbc.mybatis;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.co.ctc_g.jfw.test.unit.DatabaseInitialize;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/jp/co/ctc_g/jfw/core/jdbc/mybatis/QueryLoggerIntegrationTestContext.xml")
public class QueryLoggerIntegrationTest {

    @Autowired
    protected QueryLoggerIntegrationTestBeanMapper mapper;

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private PrintStream original;

    @BeforeEach
    public void readyBuffer() {
        original = System.out;
        System.setOut(new PrintStream(buffer));
    }

    @AfterEach
    public void resetBuffer() {
        buffer.reset();
        System.setOut(original);
    }

    @Test
    @DatabaseInitialize
    public void シンプルクエリが正常にログ出力される() throws Exception {
        String expected = "select id, str, sql_date as sqlDate, tm, ts, utl_date as utlDate from QUERY_LOGGER_INTEGRATION_TEST where id = 1";
        QueryLoggerTestBean result = mapper.findBySimpleQuery();
        assertThat(result.getStr(), is("1"));
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }

    @Test
    @DatabaseInitialize
    public void 単一プレースホルダーのプリペアードクエリが正常にログ出力される() throws Exception {
        String expected = "select id, str, sql_date as sqlDate, tm, ts, utl_date as utlDate from QUERY_LOGGER_INTEGRATION_TEST where id = 1";
        QueryLoggerTestBean bean = new QueryLoggerTestBean();
        bean.setId(1);
        QueryLoggerTestBean result = mapper.findByPreparedQuery(bean);
        assertThat(result.getStr(), is("1"));
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }

    @Test
    @DatabaseInitialize
    public void 単一プレースホルダーのプリペアードクエリのパラエータにnull値設定されたSQLが正常にログ出力される() throws Exception {
        String expected = "select id, str, sql_date as sqlDate, tm, ts, utl_date as utlDate from QUERY_LOGGER_INTEGRATION_TEST where id = null";
        QueryLoggerTestBean bean = new QueryLoggerTestBean();
        bean.setId(null);
        mapper.findByPreparedQuery(bean);
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }

    @Test
    @DatabaseInitialize
    public void 複数プレースホルダーのプリペアードクエリが正常にログ出力される() throws Exception {
        String expected = "insert into QUERY_LOGGER_INTEGRATION_TEST (id, str, sql_date, tm, ts, utl_date) values (nextval('QUERY_LOGGER_INTEGRATION_TEST_SEQUENCE'), '$2\\1', '2011-11-21', '09:00:21', '2011-11-21 09:00:21.0', '2011-12-21')";
        QueryLoggerTestBean bean = new QueryLoggerTestBean();
        bean.setStr("$2\\1");
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 10, 21);
        bean.setSqlDate(new java.sql.Date(cal.getTimeInMillis()));
        cal.set(2011, 10, 21, 9, 00, 21);
        bean.setTm(new java.sql.Time(cal.getTimeInMillis()));
        cal.set(2011, 10, 21, 9, 00, 21);
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        timestamp.setNanos(0);
        bean.setTs(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        bean.setUtlDate(format.parse("2011-12-21"));
        int count = mapper.create(bean);
        String loggingString = new String(buffer.toByteArray());
        assertThat(loggingString, containsString(expected));
        assertThat(count, is(1));
    }

    @Test
    @DatabaseInitialize
    public void プリペアードクエリのパラエータにnull値設定されたSQLが正常にログ出力される() throws Exception {
        String expected = "insert into QUERY_LOGGER_INTEGRATION_TEST (id, str, sql_date, tm, ts, utl_date) values (nextval('QUERY_LOGGER_INTEGRATION_TEST_SEQUENCE'), null, '2011-11-22', '09:00:22', '2011-11-22 09:00:22.0', '2011-12-22')";
        QueryLoggerTestBean bean = new QueryLoggerTestBean();
        bean.setStr(null);
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 10, 22);
        bean.setSqlDate(new java.sql.Date(cal.getTimeInMillis()));
        cal.set(2011, 10, 22, 9, 00, 22);
        bean.setTm(new java.sql.Time(cal.getTimeInMillis()));
        cal.set(2011, 10, 22, 9, 00, 22);
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        timestamp.setNanos(0);
        bean.setTs(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        bean.setUtlDate(format.parse("2011-12-22"));
        int count = mapper.create(bean);
        assertThat(new String(buffer.toByteArray()), containsString(expected));
        assertThat(count, is(1));
    }

    @Test
    @DatabaseInitialize
    public void INSERT用クエリーとINSERT後のキー値検索クエリーの両クエリーがログ出力される() throws Exception {
        String expected = "insert into QUERY_LOGGER_INTEGRATION_TEST (id, str, sql_date, tm, ts, utl_date) values (nextval('QUERY_LOGGER_INTEGRATION_TEST_SEQUENCE'), 'registSelectKey test', '2010-02-01', '13:00:01', '2010-04-01 14:00:00.0', '2010-04-01')";
        QueryLoggerTestBean bean = new QueryLoggerTestBean();
        bean.setStr("registSelectKey test");
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 01, 01);
        bean.setSqlDate(new java.sql.Date(cal.getTimeInMillis()));
        cal.set(2010, 02, 01, 13, 00, 01);
        bean.setTm(new java.sql.Time(cal.getTimeInMillis()));
        cal.set(2010, 03, 01, 14, 00, 00);
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        timestamp.setNanos(0);
        bean.setTs(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        bean.setUtlDate(format.parse("2010-04-01"));
        mapper.createPullGeneratedKey(bean);
        assertThat(new String(buffer.toByteArray()), containsString(expected));
        expected = "select id from QUERY_LOGGER_INTEGRATION_TEST where str = 'registSelectKey test'";
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }

    @Test
    @DatabaseInitialize
    public void コーラブルクエリーが正常にログ出力される() throws Exception {
        String expected = "call upper(\'test\')";
        String result = mapper.procedure("test");
        assertThat(result, is("TEST"));
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }

    @Test
    @DatabaseInitialize
    public void コーラブルクエリーのパラエータにnull値設定されたSQLが正常にログ出力される() throws Exception {
        String expected = "call upper(null)";
        mapper.procedure(null);
        assertThat(new String(buffer.toByteArray()), containsString(expected));
    }
}