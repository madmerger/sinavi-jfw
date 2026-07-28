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

package jp.co.ctc_g.jfw.xlsbeans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import jp.co.ctc_g.jfw.xlsbeans.bean.field.BadFieldTypeTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.field.VerticalFieldRecordsTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.field.VerticalFieldTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.setter.BadArgmentTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.setter.BadTypeTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.setter.VerticalNotFoundColumnsTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.setter.VerticalRecordsTestBean;
import jp.co.ctc_g.jfw.xlsbeans.bean.setter.VerticalTestBean;
import jp.co.ctc_g.jse.core.excel.JxXLSBeans;
import com.github.takezoe.xlsbeans.XLSBeansException;
import com.github.takezoe.xlsbeans.xssfconverter.WorkbookFinder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JxVerticalRecordsProcessorTest {

    protected InputStream in = null;

    @Before
    public void setup() {
        in = JxVerticalRecordsProcessorTest.class.getResourceAsStream("mapping.xls");
    }

    @After
    public void teardown() throws Exception {
        in.close();
    }

    @Test(expected = XLSBeansException.class)
    public void setterメソッドの引数が1以上の場合にエラーが発生するかどうか() throws Exception {
        new JxXLSBeans().loadMultiple(in, BadArgmentTestBean.class, WorkbookFinder.TYPE_HSSF);
    }

    @Test(expected = XLSBeansException.class)
    public void setterメソッドの型がListや配列以外の場合に例外が発生するかどうか() throws Exception {
        new JxXLSBeans().loadMultiple(in, BadTypeTestBean.class, WorkbookFinder.TYPE_HSSF);
    }

    @Test(expected = XLSBeansException.class)
    public void publicプロパティの型がListや配列以外の場合に例外が発生するかどうか() throws Exception {
        new JxXLSBeans().loadMultiple(in, BadFieldTypeTestBean.class, WorkbookFinder.TYPE_HSSF);
    }

    @Test
    public void setterメソッドに付与したアノテーション通りマッピングされるかどうか() throws Exception {
        VerticalTestBean result = new JxXLSBeans().load(in, VerticalTestBean.class, WorkbookFinder.TYPE_HSSF);
        assertThat(result.getList(), notNullValue());
        assertThat(result.getList().size(), is(5));
        assertThat(result.getArray(), notNullValue());
        assertThat(result.getArray().length, is(5));
    }

    @Test
    public void publicプロパティに付与したアノテーション通りマッピングされるかどうか() throws Exception {
        VerticalFieldTestBean result = new JxXLSBeans().load(in, VerticalFieldTestBean.class, WorkbookFinder.TYPE_HSSF);
        assertThat(result.list, notNullValue());
        assertThat(result.list.size(), is(5));
        assertThat(result.array, notNullValue());
        assertThat(result.array.length, is(5));
    }

    @Test
    public void publicプロパティにカラムとマッピングカラムが指定されたアノテーション通りにマッピングされるかどうか() throws Exception {
        VerticalFieldRecordsTestBean result = new JxXLSBeans().load(in, VerticalFieldRecordsTestBean.class, WorkbookFinder.TYPE_HSSF);
        assertThat(result.records, notNullValue());
        assertThat(result.records.get(0).column, is("a"));
        assertThat(result.records.get(0).mapping.size(), is(5));
        assertThat(result.records.get(0).mapping.get("1"), is("a1"));
        assertThat(result.records.get(0).mapping.get("2"), is("a2"));
        assertThat(result.records.get(0).mapping.get("3"), is("a3"));
        assertThat(result.records.get(0).mapping.get("4"), is("a4"));
        assertThat(result.records.get(0).mapping.get("5"), is("a5"));
        assertThat(result.records.get(1).column, is("b"));
        assertThat(result.records.get(1).mapping.size(), is(5));
        assertThat(result.records.get(1).mapping.get("1"), is("b1"));
        assertThat(result.records.get(1).mapping.get("2"), is("b2"));
        assertThat(result.records.get(1).mapping.get("3"), is("b3"));
        assertThat(result.records.get(1).mapping.get("4"), is("b4"));
        assertThat(result.records.get(1).mapping.get("5"), is("b5"));
        assertThat(result.records.get(2).column, is("c"));
        assertThat(result.records.get(2).mapping.size(), is(5));
        assertThat(result.records.get(2).mapping.get("1"), is("c1"));
        assertThat(result.records.get(2).mapping.get("2"), is("c2"));
        assertThat(result.records.get(2).mapping.get("3"), is("c3"));
        assertThat(result.records.get(2).mapping.get("4"), is("c4"));
        assertThat(result.records.get(2).mapping.get("5"), is("c5"));
        assertThat(result.records.get(3).column, is("d"));
        assertThat(result.records.get(3).mapping.size(), is(5));
        assertThat(result.records.get(3).mapping.get("1"), is("d1"));
        assertThat(result.records.get(3).mapping.get("2"), is("d2"));
        assertThat(result.records.get(3).mapping.get("3"), is("d3"));
        assertThat(result.records.get(3).mapping.get("4"), is("d4"));
        assertThat(result.records.get(3).mapping.get("5"), is("d5"));
        assertThat(result.records.get(4).column, is("e"));
        assertThat(result.records.get(4).mapping.size(), is(5));
        assertThat(result.records.get(4).mapping.get("1"), is("e1"));
        assertThat(result.records.get(4).mapping.get("2"), is("e2"));
        assertThat(result.records.get(4).mapping.get("3"), is("e3"));
        assertThat(result.records.get(4).mapping.get("4"), is("e4"));
        assertThat(result.records.get(4).mapping.get("5"), is("e5"));
    }

    @Test
    public void setterプロパティにカラムとマッピングカラムが指定されたアノテーション通りにマッピングされるかどうか() throws Exception {
        VerticalRecordsTestBean result = new JxXLSBeans().load(in, VerticalRecordsTestBean.class, WorkbookFinder.TYPE_HSSF);
        assertThat(result.getRecords(), notNullValue());
        assertThat(result.getRecords().get(0).getColumn(), is("a"));
        assertThat(result.getRecords().get(0).getMapping().size(), is(5));
        assertThat(result.getRecords().get(0).getMapping().get("1"), is("a1"));
        assertThat(result.getRecords().get(0).getMapping().get("2"), is("a2"));
        assertThat(result.getRecords().get(0).getMapping().get("3"), is("a3"));
        assertThat(result.getRecords().get(0).getMapping().get("4"), is("a4"));
        assertThat(result.getRecords().get(0).getMapping().get("5"), is("a5"));
        assertThat(result.getRecords().get(1).getColumn(), is("b"));
        assertThat(result.getRecords().get(1).getMapping().size(), is(5));
        assertThat(result.getRecords().get(1).getMapping().get("1"), is("b1"));
        assertThat(result.getRecords().get(1).getMapping().get("2"), is("b2"));
        assertThat(result.getRecords().get(1).getMapping().get("3"), is("b3"));
        assertThat(result.getRecords().get(1).getMapping().get("4"), is("b4"));
        assertThat(result.getRecords().get(1).getMapping().get("5"), is("b5"));
        assertThat(result.getRecords().get(2).getColumn(), is("c"));
        assertThat(result.getRecords().get(2).getMapping().size(), is(5));
        assertThat(result.getRecords().get(2).getMapping().get("1"), is("c1"));
        assertThat(result.getRecords().get(2).getMapping().get("2"), is("c2"));
        assertThat(result.getRecords().get(2).getMapping().get("3"), is("c3"));
        assertThat(result.getRecords().get(2).getMapping().get("4"), is("c4"));
        assertThat(result.getRecords().get(2).getMapping().get("5"), is("c5"));
        assertThat(result.getRecords().get(3).getColumn(), is("d"));
        assertThat(result.getRecords().get(3).getMapping().size(), is(5));
        assertThat(result.getRecords().get(3).getMapping().get("1"), is("d1"));
        assertThat(result.getRecords().get(3).getMapping().get("2"), is("d2"));
        assertThat(result.getRecords().get(3).getMapping().get("3"), is("d3"));
        assertThat(result.getRecords().get(3).getMapping().get("4"), is("d4"));
        assertThat(result.getRecords().get(3).getMapping().get("5"), is("d5"));
        assertThat(result.getRecords().get(4).getColumn(), is("e"));
        assertThat(result.getRecords().get(4).getMapping().size(), is(5));
        assertThat(result.getRecords().get(4).getMapping().get("1"), is("e1"));
        assertThat(result.getRecords().get(4).getMapping().get("2"), is("e2"));
        assertThat(result.getRecords().get(4).getMapping().get("3"), is("e3"));
        assertThat(result.getRecords().get(4).getMapping().get("4"), is("e4"));
        assertThat(result.getRecords().get(4).getMapping().get("5"), is("e5"));
    }

    @Test(expected = XLSBeansException.class)
    public void VerticalRecordsにColumnアノテーションがない場合に例外が発生するかどうか() throws Exception {
        new JxXLSBeans().load(in, VerticalNotFoundColumnsTestBean.class, WorkbookFinder.TYPE_HSSF);
    }

}
