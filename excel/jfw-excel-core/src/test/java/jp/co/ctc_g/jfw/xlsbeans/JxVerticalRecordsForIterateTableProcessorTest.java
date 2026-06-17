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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;

import jp.co.ctc_g.jfw.xlsbeans.bean.IterateTableTestBean;
import jp.co.ctc_g.jse.core.excel.JxXLSBeans;
import net.java.amateras.xlsbeans.xssfconverter.WorkbookFinder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JxVerticalRecordsForIterateTableProcessorTest {

    protected InputStream in = null;

    @BeforeEach
    public void setup() {
        in = JxVerticalRecordsProcessorTest.class.getResourceAsStream("iterate-table.xls");
    }

    @AfterEach
    public void teardown() throws Exception {
        in.close();
    }

    @Test
    public void 複数表のデータを読み込めるかどうか() throws Exception {
        IterateTableTestBean result = new JxXLSBeans().load(in, IterateTableTestBean.class, WorkbookFinder.TYPE_HSSF);
        assertThat(result, notNullValue());
    }

}
