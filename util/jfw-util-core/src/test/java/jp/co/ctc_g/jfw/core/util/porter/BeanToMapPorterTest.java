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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class BeanToMapPorterTest {

    @Test
    public void 文字列プロパティ名完全一致移送のテスト正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("string");
        b.setIntegerField(1);
        b.setBigIntegerField(BigInteger.valueOf(11L));
        b.setFloatField(.1f);
        b.setDoubleField(.11);
        b.setBigDecimalField(BigDecimal.valueOf(.111));
        Map<String, Object> created =
                new BeanToMapPorter(b).copyTo(new HashMap<String, Object>());
        assertEquals("string", created.get("stringField"));
        assertEquals(Integer.valueOf(1), created.get("integerField"));
        assertEquals(BigInteger.valueOf(11L), created.get("bigIntegerField"));
        assertEquals(Float.valueOf(.1f), created.get("floatField"));
        assertEquals(Double.valueOf(.11), created.get("doubleField"));
        assertEquals(BigDecimal.valueOf(.111), created.get("bigDecimalField"));
    }
    
    @Test
    public void Mapを生成できる() {
        
        BeanToMapPorterTestSourceBean b = new BeanToMapPorterTestSourceBean();
        b.setId(1);
        b.setName("nameValue");
        Map<String, Object> map = new BeanToMapPorter(b).create();
        assertThat(map, notNullValue());
        assertThat((Integer)map.get("id"), is(Integer.valueOf(1)));
        assertThat((String)map.get("name"), is("nameValue"));
    }
    
    @Test
    public void 変換対象外の指定されたものは変換されない() {
        
        BeanToMapPorterTestSourceBean b = new BeanToMapPorterTestSourceBean();
        b.setId(1);
        b.setName("nameValue");
        BeanToMapPorter porter = new BeanToMapPorter(b);
        Map<String, Object> map = porter.ignore("id").create();
        assertThat(map, notNullValue());
        assertThat((Integer)map.get("id"), nullValue());
        assertThat((String)map.get("name"), is("nameValue"));
        Map<String, Object> afterClearMap = porter.clearIgnorance().create();
        assertThat(afterClearMap, notNullValue());
        assertThat((Integer)afterClearMap.get("id"), is(Integer.valueOf(1)));
        assertThat((String)afterClearMap.get("name"), is("nameValue"));
    }
    
    @Test
    public void exchangeで指定したキーに変換される() {
        
        BeanToMapPorterTestSourceBean b = new BeanToMapPorterTestSourceBean();
        b.setId(1);
        b.setName("nameValue");
        BeanToMapPorter porter = new BeanToMapPorter(b);
        Map<String, Object> map = porter.exchange("id", "exId").create();
        assertThat(map, notNullValue());
        assertThat((Integer)map.get("exId"), is(Integer.valueOf(1)));
        assertThat((String)map.get("name"), is("nameValue"));
        Map<String, Object> afterClearMap = porter.clearExchange().create();
        assertThat(afterClearMap, notNullValue());
        assertThat((Integer)afterClearMap.get("id"), is(Integer.valueOf(1)));
        assertThat((String)afterClearMap.get("name"), is("nameValue"));
    }

}

class BeanToMapPorterTestSourceBean {
    private Integer id;
    private String name;
    public void setId(Integer id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public Integer getId() {return id;}
    public String getName() {return name;}
}