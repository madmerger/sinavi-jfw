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

package jp.co.ctc_g.jfw.core.jdbc;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JxSqlSessionFactoryTest {

    protected JxSqlSessionFactory factory;

    @BeforeEach
    public void instantiate() {
        factory = new JxSqlSessionFactory(new DefaultSqlSessionFactory(null));
    }

    @Test
    public void SqlSessionをJxSqlSessionに加工して返却する() {
        Configuration config = new Configuration();
        SqlSession session = new DefaultSqlSession(config, null);
        SqlSession wrapped = factory.wrap(session);
        assertThat(wrapped, is(instanceOf(JxSqlSession.class)));
        assertThat(wrapped.getConfiguration(), is(config)); // delegate 確認
    }
}
