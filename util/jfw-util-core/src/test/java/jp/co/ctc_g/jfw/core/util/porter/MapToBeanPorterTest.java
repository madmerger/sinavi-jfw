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

package jp.co.ctc_g.jfw.core.util.porter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import jp.co.ctc_g.jfw.core.util.Maps;

import org.junit.jupiter.api.Test;

public class MapToBeanPorterTest {

    @Test
    public void 文字列プロパティ名完全一致移送のテスト正常系1() {
        Map<String,Object> source = Maps
                .hash("stringField", (Object) "string")
                .map("integerField", 1)
                .map("bigIntegerField", BigInteger.valueOf(11L))
                .map("floatField", .1f)
                .map("doubleField", .11)
                .map("bigDecimalField", BigDecimal.valueOf(.111));
        BeanPorterBean created = new MapToBeanPorter(source).create(BeanPorterBean.class);
        assertEquals("string", created.getStringField());
        assertEquals(Integer.valueOf(1), created.getIntegerField());
        assertEquals(BigInteger.valueOf(11L), created.getBigIntegerField());
        assertEquals(Float.valueOf(.1f), created.getFloatField());
        assertEquals(Double.valueOf(.11), created.getDoubleField());
        assertEquals(BigDecimal.valueOf(.111), created.getBigDecimalField());
    }
    
    @Test
    public void 変換対象外の指定されたものは変換されない() {
        Map<String, Object> source = Maps
            .hash("name", (Object)"nameValue")
            .map("id", 1);
        MapToBeanPorter porter = new MapToBeanPorter(source);
        MapToBeanPorterTestSourceBean dest = porter.ignore("id").create(MapToBeanPorterTestSourceBean.class);
        assertThat(dest, notNullValue());
        assertThat(dest.getId(), nullValue());
        assertThat(dest.getName(), is("nameValue"));
        MapToBeanPorterTestSourceBean afterClearBean = porter.clearIgnorance().create(MapToBeanPorterTestSourceBean.class);
        assertThat(afterClearBean, notNullValue());
        assertThat(afterClearBean.getId(), is(1));
        assertThat(afterClearBean.getName(), is("nameValue"));
    }
    
    @Test
    public void exchangeで指定したキーに変換される() {
        Map<String, Object> source = Maps
            .hash("name", (Object)"nameValue")
            .map("exId", 1);
        MapToBeanPorter porter = new MapToBeanPorter(source);
        MapToBeanPorterTestSourceBean dest = porter.exchange("exId", "id").create(MapToBeanPorterTestSourceBean.class);
        assertThat(dest, notNullValue());
        assertThat(dest.getId(), is(1));
        assertThat(dest.getName(), is("nameValue"));
        MapToBeanPorterTestSourceBean afterClearBean = porter.clearExchange().create(MapToBeanPorterTestSourceBean.class);
        assertThat(afterClearBean, notNullValue());
        assertThat(afterClearBean.getId(), nullValue());
        assertThat(afterClearBean.getName(), is("nameValue"));
    }
}

class MapToBeanPorterTestSourceBean {
    private Integer id;
    private String name;
    public void setId(Integer id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public Integer getId() {return id;}
    public String getName() {return name;}
}
