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
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.apache.ibatis.mapping.StatementType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QueryBuilderFactoryTest {

    private static QueryBuilderFactory queryBuilderFactory;

    @BeforeAll
    public static void setup() {
        queryBuilderFactory = new QueryBuilderFactory(true);
    }

    @Test
    public void StatementTypeのSTATEMENTに一致するQueryBuilderのインスタンスを取得できる() throws Exception {
        QueryBuilder builder = queryBuilderFactory.getBuilder(StatementType.STATEMENT);
        assertThat(builder, is(instanceOf(SimpleQueryBuilder.class)));
    }

    @Test
    public void StatementTypeのPREPAREDに一致するQueryBuilderのインスタンスを取得できる() throws Exception {
        QueryBuilder builder = queryBuilderFactory.getBuilder(StatementType.PREPARED);
        assertThat(builder, is(instanceOf(PreparedQueryBuilder.class)));
    }

    @Test
    public void StatementTypeのCALLABLEに一致するQueryBuilderのインスタンスを取得できる() throws Exception {
        QueryBuilder builder = queryBuilderFactory.getBuilder(StatementType.CALLABLE);
        assertThat(builder, is(instanceOf(CallableQueryBuilder.class)));
    }

    @Test
    public void 引数がNULLであることを表明() throws Exception {
        assertThrows(InternalException.class, () -> {
            queryBuilderFactory.getBuilder(null);
        });
    }
}
