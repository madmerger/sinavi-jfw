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

package jp.co.ctc_g.jse.core.framework;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;

import jp.co.ctc_g.jse.core.util.web.beans.HalfwidthDecimalEditor;
import jp.co.ctc_g.jse.core.util.web.beans.HalfwidthNumberEditor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

public class JseDefaultNumberPropertyEditorRegistrarTest {

    private JseDefaultNumberPropertyEditorRegistrar registrar;
    private PropertyEditorRegistry registry;

    @Test
    public void 各タイプに合わせたプロパティエディタ一括登録されていることを確認() {
        registry = new ExtendedServletRequestDataBinder(new TestBean());
        registrar = new JseDefaultNumberPropertyEditorRegistrar();
        registrar.registerCustomEditors(registry);
        
        PropertyEditor editor = registry.findCustomEditor(Byte.class, "byteP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthNumberEditor.class.getName()));
        
        editor = registry.findCustomEditor(Short.class, "shortP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthNumberEditor.class.getName()));
        
        editor = registry.findCustomEditor(Integer.class, "integerP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthNumberEditor.class.getName()));
        
        editor = registry.findCustomEditor(Long.class, "longP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthNumberEditor.class.getName()));
        
        editor = registry.findCustomEditor(BigInteger.class, "bigIntegerP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthNumberEditor.class.getName()));
        
        editor = registry.findCustomEditor(Float.class, "floatP");
        assertThat(editor, is(nullValue()));
        
        editor = registry.findCustomEditor(Double.class, "doubleP");
        assertThat(editor, is(nullValue()));
        
        editor = registry.findCustomEditor(BigDecimal.class, "bigDecimalP");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(HalfwidthDecimalEditor.class.getName()));
    }

    @Test
    public void allowEmptyプロパティの設定が反映される() {
        registry = new ExtendedServletRequestDataBinder(new TestBean());
        registrar = new JseDefaultNumberPropertyEditorRegistrar();
        registrar.setAllowEmpty(true);
        registrar.registerCustomEditors(registry);
        PropertyEditor editor = registry.findCustomEditor(Byte.class, "byteP");
        editor.setAsText(null);
        assertThat(editor.getAsText(), is(""));
    }

    class TestBean {
        private Byte byteP;
        private Short shortP;
        private Integer integerP;
        private Long longP;
        private BigInteger bigIntegerP;
        private Float floatP;
        private Double doubleP;
        private BigDecimal bigDecimalP;
        public void setByteP(Byte byteP) {
            this.byteP = byteP;
        }
        public Byte getByteP() {
            return this.byteP;
        }
        public void setShortP(Short shortP) {
            this.shortP = shortP;
        }
        public Short getShortP() {
            return this.shortP;
        }
        public void setIntegerP(Integer integerP) {
            this.integerP = integerP;
        }
        public Integer getIntegerP() {
            return this.integerP;
        }
        public void setLongP(Long longP) {
            this.longP = longP;
        }
        public Long getLongP() {
            return this.longP;
        }
        public void setBigIntegerP(BigInteger bigIntegerP) {
            this.bigIntegerP = bigIntegerP;
        }
        public BigInteger getBigIntegerP() {
            return this.bigIntegerP;
        }
        public void setFloatP(Float floatP) {
            this.floatP = floatP;
        }
        public Float getFloatP() {
            return this.floatP;
        }
        public void setDoubleP(Double doubleP) {
            this.doubleP = doubleP;
        }
        public Double getDoubleP() {
            return this.doubleP;
        }
        public void setBigDecimalP(BigDecimal bigDecimalP) {
            this.bigDecimalP = bigDecimalP;
        }
        public BigDecimal getBigDecimalP() {
            return this.bigDecimalP;
        }
    }
}
