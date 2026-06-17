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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LiteralConvertorRegistoryTest {

    private LiteralConvertorRegistory registory;

    @BeforeEach
    public void instantiate() {
        registory = LiteralConvertorRegistory.getInstance();
    }

    @Test
    public void 指定したJava型にマッチするLiteralConvertorが存在する() {
        assertThat(registory.hasConvertor(String.class), is(true));
        assertThat(registory.hasConvertor(java.sql.Date.class), is(true));
        assertThat(registory.hasConvertor(java.sql.Time.class), is(true));
        assertThat(registory.hasConvertor(java.sql.Timestamp.class), is(true));
        assertThat(registory.hasConvertor(Date.class), is(true));
        assertThat(registory.hasConvertor(null), is(true));
    }

    @Test
    public void 指定したJava型にマッチするLiteralConvertorが無い() {
        assertThat(registory.hasConvertor(Long.class), is(false));
        assertThat(registory.hasConvertor(BigInteger.class), is(false));
    }

    @Test
    public void 指定したJava型にマッチするLiteralConvertorを返却する() {
        assertThat(registory.getConverter(String.class), is(instanceOf(StringLiteralConvertor.class)));
        assertThat(registory.getConverter(java.sql.Date.class), is(instanceOf(SqlDateLiteralConvertor.class)));
        assertThat(registory.getConverter(java.sql.Time.class), is(instanceOf(TimeLiteralConvertor.class)));
        assertThat(registory.getConverter(java.sql.Timestamp.class), is(instanceOf(TimestampLiteralConvertor.class)));
        assertThat(registory.getConverter(Date.class), is(instanceOf(DateLiteralConvertor.class)));
        assertThat(registory.getConverter(null), is(instanceOf(DefaultLiteralConvertor.class)));
    }

    @Test
    public void 指定したJava型にマッチするLiteralConvertorが無い場合にDefaultLiteralConvertorを返却する() {
        assertThat(registory.getConverter(Long.class), is(instanceOf(DefaultLiteralConvertor.class)));
    }

    @Test
    public void 単一LiteralConvertorを登録する() {
        registory.regist(new BooleanLiteralConvertor());
        assertThat(registory.getConverter(Boolean.class), is(instanceOf(BooleanLiteralConvertor.class)));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void 複数LiteralConvertorを登録する() {
        List<LiteralConvertor> l = new ArrayList<LiteralConvertor>();
        l.add(new BooleanLiteralConvertor());
        l.add(new BigDecimalLiteralConvertor());
        registory.regist(l);
        assertThat(registory.getConverter(Boolean.class), is(instanceOf(BooleanLiteralConvertor.class)));
        assertThat(registory.getConverter(BigDecimal.class), is(instanceOf(BigDecimalLiteralConvertor.class)));
    }

    @Test
    public void LiteralConvertorを上書きする() {
        registory.regist(new StringLiteralTestConvertor());
        assertThat(registory.getConverter(String.class), is(instanceOf(StringLiteralTestConvertor.class)));
        registory.regist(new StringLiteralConvertor());
        assertThat(registory.getConverter(String.class), is(instanceOf(StringLiteralConvertor.class)));
    }

    @SuppressWarnings("rawtypes")
    class BooleanLiteralConvertor implements LiteralConvertor {

        @Override
        public String convert(Object target) {
            return target.toString();
        }

        @Override
        public Class getJavaType() {
            return Boolean.class;
        }
    }

    @SuppressWarnings("rawtypes")
    class BigDecimalLiteralConvertor implements LiteralConvertor {

        @Override
        public String convert(Object target) {
            return target.toString();
        }

        @Override
        public Class getJavaType() {
            return BigDecimal.class;
        }
    }

    @SuppressWarnings("rawtypes")
    class StringLiteralTestConvertor implements LiteralConvertor {

        @Override
        public String convert(Object target) {
            return target.toString();
        }

        @Override
        public Class getJavaType() {
            return String.class;
        }
    }
}
