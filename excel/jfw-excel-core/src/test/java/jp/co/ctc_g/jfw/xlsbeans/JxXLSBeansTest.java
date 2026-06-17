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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;

import jp.co.ctc_g.jse.core.excel.JxIterateTableProcessor;
import jp.co.ctc_g.jse.core.excel.JxVerticalRecords;
import jp.co.ctc_g.jse.core.excel.JxVerticalRecordsProcessor;
import jp.co.ctc_g.jse.core.excel.JxXLSBeans;
import net.java.amateras.xlsbeans.annotation.IterateTables;
import net.java.amateras.xlsbeans.processor.FieldProcessorFactory;

import org.junit.jupiter.api.Test;

public class JxXLSBeansTest {

    @Test
    public void インスタンス生成後にFieldProcessorFactoryにProcessorが登録されているかどうか() throws Exception {
        class TestBeans {

            @JxVerticalRecords(recordClass = String.class)
            public String vertical;

            @IterateTables(tableClass = String.class, tableLabel = "")
            public String it;
        }

        Class<?> clazz = TestBeans.class;
        Field v = clazz.getField("vertical");
        JxVerticalRecords jvr = v.getAnnotation(JxVerticalRecords.class);
        Field it = clazz.getField("it");
        IterateTables ait = it.getAnnotation(IterateTables.class);
        new JxXLSBeans();
        assertThat(FieldProcessorFactory.getProcessor(jvr), instanceOf(JxVerticalRecordsProcessor.class));
        assertThat(FieldProcessorFactory.getProcessor(ait), instanceOf(JxIterateTableProcessor.class));
    }

}
