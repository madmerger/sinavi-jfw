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

package jp.co.ctc_g.jfw.core.jdbc.mybatis.type;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;

import jp.co.ctc_g.jfw.test.unit.DatabaseInitialize;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/jp/co/ctc_g/jfw/core/jdbc/mybatis/type/BigIntegerTypeHandlerTestContext.xml")
public class BigIntegerTypeHandlerTest {

    @Autowired
    private BigIntegerTypeHandlerTestBeanDao dao;
    
    @Test
    @DatabaseInitialize
    public void 検索時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = dao.findById(Integer.valueOf(1));
        assertThat(bean, is(notNullValue()));
        assertThat(bean.getNum19(), is(new BigInteger("1234567890123456789", 10)));
        assertThat(bean.getNum25(), is(new BigInteger("1234567890123456789012345", 10)));
        assertThat(bean.getNum30(), is(new BigInteger("123456789012345678901234567890", 10)));
    }

    @Test
    @DatabaseInitialize
    public void 登録時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = new BigIntegerTypeHandlerTestBean();
        bean.setNum19(new BigInteger("1234567890123456789", 10));
        bean.setNum25(new BigInteger("1234567890123456789012345", 10));
        bean.setNum30(new BigInteger("123456789012345678901234567890", 10));
        int count = dao.create(bean);
        assertThat(count, is(1));
        BigIntegerTypeHandlerTestBean resultBean = dao.findById(4);
        assertThat(resultBean, is(notNullValue()));
        assertThat(resultBean.getNum19(), is(new BigInteger("1234567890123456789", 10)));
        assertThat(resultBean.getNum25(), is(new BigInteger("1234567890123456789012345", 10)));
        assertThat(resultBean.getNum30(), is(new BigInteger("123456789012345678901234567890", 10)));
    }

    @Test
    @DatabaseInitialize
    public void 更新時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = new BigIntegerTypeHandlerTestBean();
        bean.setId(1);
        bean.setNum19(new BigInteger("9876543210987654321", 10));
        bean.setNum25(new BigInteger("5432109876543210987654321", 10));
        bean.setNum30(new BigInteger("998765432109876543210987654321", 10));
        int count = dao.update(bean);
        assertThat(count, is(1));
        BigIntegerTypeHandlerTestBean resultBean = dao.findById(1);
        assertThat(resultBean.getNum19(), is(new BigInteger("9876543210987654321", 10)));
        assertThat(resultBean.getNum25(), is(new BigInteger("5432109876543210987654321", 10)));
        assertThat(resultBean.getNum30(), is(new BigInteger("998765432109876543210987654321", 10)));
    }

    @Test
    @DatabaseInitialize
    public void null値の検索時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = dao.findById(Integer.valueOf(2));
        assertThat(bean, is(notNullValue()));
        assertThat(bean.getNum19(), is(nullValue()));
        assertThat(bean.getNum25(), is(nullValue()));
        assertThat(bean.getNum30(), is(nullValue()));
    }

    @Test
    @DatabaseInitialize
    public void null値の登録時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = new BigIntegerTypeHandlerTestBean();
        bean.setNum19(null);
        bean.setNum25(null);
        bean.setNum30(null);
        int count = dao.create(bean);
        assertThat(count, is(1));
        BigIntegerTypeHandlerTestBean resultBean = dao.findById(4);
        assertThat(resultBean, is(notNullValue()));
        assertThat(resultBean.getNum19(), is(nullValue()));
        assertThat(resultBean.getNum25(), is(nullValue()));
        assertThat(resultBean.getNum30(), is(nullValue()));
    }

    @Test
    @DatabaseInitialize
    public void null値で更新時にタイプ処理が正常に行われる() throws Exception {
        BigIntegerTypeHandlerTestBean bean = new BigIntegerTypeHandlerTestBean();
        bean.setId(3);
        bean.setNum19(new BigInteger("1111111111111111111", 10));
        bean.setNum25(new BigInteger("0", 10));
        bean.setNum30(null);
        int count = dao.update(bean);
        assertThat(count, is(1));
        BigIntegerTypeHandlerTestBean resultBean = dao.findById(3);
        assertThat(resultBean.getNum19(), is(new BigInteger("1111111111111111111", 10)));
        assertThat(resultBean.getNum25(), is(new BigInteger("0", 10)));
        assertThat(resultBean.getNum30(), is(nullValue()));
    }
}
