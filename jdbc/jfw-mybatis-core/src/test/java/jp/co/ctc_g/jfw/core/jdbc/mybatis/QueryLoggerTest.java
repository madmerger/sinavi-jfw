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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryLoggerTest {

    private QueryLogger queryLogger;

    @BeforeEach
    public void instantiate() {
        queryLogger = new QueryLogger(true);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void コンバーターを追加する() throws Exception {
        LiteralConvertor[] literalConvertor = {
            new BooleanLiteralConvertor()
        };
        queryLogger.setLiteralConvertor(Arrays.asList(literalConvertor));
        assertThat(true, is(LiteralConvertorRegistory.getInstance().hasConvertor(Boolean.class)));
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
}
