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

import jp.co.ctc_g.jfw.core.util.typeconverter.TypeConverter;

import org.junit.jupiter.api.Test;

public class BeanPorterTest {

    @Test
    public void 文字列プロパティ名完全一致移送のテスト正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("string");
        b.setIntegerField(1);
        b.setBigIntegerField(BigInteger.valueOf(11L));
        b.setFloatField(.1f);
        b.setDoubleField(.11);
        b.setBigDecimalField(BigDecimal.valueOf(.111));
        BeanPorterBean created =
                new BeanPorter(b).create(BeanPorterBean.class);
        assertEquals("string", created.getStringField());
        assertEquals(Integer.valueOf(1), created.getIntegerField());
        assertEquals(BigInteger.valueOf(11L), created.getBigIntegerField());
        assertEquals(Float.valueOf(.1f), created.getFloatField());
        assertEquals(Double.valueOf(.11), created.getDoubleField());
        assertEquals(BigDecimal.valueOf(.111), created.getBigDecimalField());
    }

    @Test
    public void 文字列プロパティ名不完全一致移送のテスト正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("string");
        b.setIntegerField(1);
        b.setBigIntegerField(BigInteger.valueOf(11L));
        b.setFloatField(.1f);
        b.setDoubleField(.11);
        b.setBigDecimalField(BigDecimal.valueOf(.111));
        AnotherNamedBeanPorterBean created =
                new BeanPorter(b)
                .exchange("stringField", "stringProperty")
                .exchange("integerField", "integerProperty")
                .exchange("bigIntegerField", "bigIntegerProperty")
                .exchange("floatField", "floatProperty")
                .exchange("doubleField", "doubleProperty")
                .exchange("bigDecimalField", "bigDecimalProperty")
                .create(AnotherNamedBeanPorterBean.class);
        assertEquals("string", created.getStringProperty());
        assertEquals(Integer.valueOf(1), created.getIntegerProperty());
        assertEquals(BigInteger.valueOf(11L), created.getBigIntegerProperty());
        assertEquals(Float.valueOf(.1f), created.getFloatProperty());
        assertEquals(Double.valueOf(.11), created.getDoubleProperty());
        assertEquals(BigDecimal.valueOf(.111), created.getBigDecimalProperty());
    }

    @Test
    public void 文字列プロパティ数不完全一致移送のテスト正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("string");
        b.setIntegerField(1);
        b.setBigIntegerField(BigInteger.valueOf(11L));
        b.setFloatField(.1f);
        b.setDoubleField(.11);
        b.setBigDecimalField(BigDecimal.valueOf(.111));
        LessPropertyBeanPorterBean created =
                new BeanPorter(b).create(LessPropertyBeanPorterBean.class);
        assertEquals("string", created.getStringField());
        assertEquals(Integer.valueOf(1), created.getIntegerField());
        assertEquals(Double.valueOf(.11), created.getDoubleField());
    }

    @Test
    public void 型変換テスト正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("10000"); // 移送先はInteger
        AnotherTypedBeanPorterBean a =
                new BeanPorter(b).create(AnotherTypedBeanPorterBean.class);
        assertEquals(Integer.valueOf(10000), a.getStringField(), "stringFieldは10000のIntegerになっている");
    }

    @Test
    public void 型変換テストPorter追加正常系1() {
        BeanPorterBean b = new BeanPorterBean();
        b.setStringField("1"); // 移送先はInteger
        AnotherTypedBeanPorterBean a =
                new BeanPorter(b)
                .convert(Integer.class, new TypeConverter<Integer>() {
                    @SuppressWarnings("unused")
                    public Integer convert(String value) {
                        return Integer.valueOf(10000);
                    }
                })
                .create(AnotherTypedBeanPorterBean.class);
        assertEquals(Integer.valueOf(10000), a.getStringField(), "stringFieldは10000のIntegerになっている");
    }
    
    @Test
    public void ショートプロパティ名テスト() {
        ShortNameBeanPorterBean source = new ShortNameBeanPorterBean();
        source.setNField("value");
        ShortNameBeanPorterBean dest = new BeanPorter(source).create(ShortNameBeanPorterBean.class);
        assertEquals("value", dest.getNField());
    }

    @Test
    public void 変換定義を削除できる() {
        BeanPorterTestSourceBean s = new BeanPorterTestSourceBean();
        s.setId("convert");
        BeanPorter porter = new BeanPorter(s).exchange("id", "dest");
        BeanPorterTestDestBean d = porter.create(BeanPorterTestDestBean.class);
        assertThat(d, notNullValue());
        BeanPorterTestDestBean ex = porter.clearExchange().create(BeanPorterTestDestBean.class);
        assertThat(ex, notNullValue());
        assertThat(ex.getDest(), nullValue());
    }
    
    @Test
    public void 変換対象外の指定されたものは変換されない() {
        BeanPorterTestSourceBean s = new BeanPorterTestSourceBean();
        s.setId("convert");
        BeanPorter porter = new BeanPorter(s).ignore("id");
        BeanPorterTestDestBean d = porter.create(BeanPorterTestDestBean.class);
        assertThat(d, notNullValue());
        assertThat(d.getDest(), nullValue());
        // クリア後は変換できる
        BeanPorterTestDestBean ex = porter.clearIgnorance().exchange("id", "dest").create(BeanPorterTestDestBean.class);
        assertThat(ex, notNullValue());
        assertThat(ex.getDest(), notNullValue());
    }
    
}

class BeanPorterTestSourceBean {
    private String id;
    public void setId(String id) {this.id = id;}
    public String getId() {return id;}
}

class BeanPorterTestDestBean {
    private String dest;
    public void setDest(String dest) { this.dest = dest; }
    public String getDest() {return dest;}
}

