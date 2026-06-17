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
import static org.hamcrest.MatcherAssert.assertThat;

import java.beans.PropertyEditor;
import java.util.Date;

import jp.co.ctc_g.jse.core.util.web.beans.DateEditor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

public class JseDefaultDatePropertyEditorRegistrarTest {

    private JseDefaultDatePropertyEditorRegistrar registrar;
    private PropertyEditorRegistry registry;

    @Test
    public void Date型のプロパティエディタが検索できる() {
        registry = new ExtendedServletRequestDataBinder(new TestBean());
        registrar = new JseDefaultDatePropertyEditorRegistrar();
        registrar.registerCustomEditors(registry);
        PropertyEditor editor = registry.findCustomEditor(Date.class, "birth");
        assertThat(editor, is(notNullValue()));
        assertThat(editor.getClass().getName(), is(DateEditor.class.getName()));
    }

    @Test
    public void allowEmptyプロパティの設定が反映される() {
        registry = new ExtendedServletRequestDataBinder(new TestBean());
        registrar = new JseDefaultDatePropertyEditorRegistrar();
        registrar.setAllowEmpty(true);
        registrar.registerCustomEditors(registry);
        PropertyEditor editor = registry.findCustomEditor(Date.class, "birth");
        DateEditor dateEditor = (DateEditor)editor;
        dateEditor.setAsText(null);
        assertThat(dateEditor.getAsText(), is(""));
    }

    @Test
    public void patternプロパティの設定が反映される() {
        registry = new ExtendedServletRequestDataBinder(new TestBean());
        registrar = new JseDefaultDatePropertyEditorRegistrar();
        registrar.setPattern("yyyy-MM-dd");
        registrar.registerCustomEditors(registry);
        PropertyEditor editor = registry.findCustomEditor(Date.class, "birth");
        DateEditor dateEditor = (DateEditor)editor;
        dateEditor.setAsText("2013-01-01");
        assertThat(dateEditor.getAsText(), is("2013-01-01"));
    }

    class TestBean {
        private Date birth;
        public void setBirth(Date birth) {
            this.birth = birth;
        }
        public Date getBirth() {
            return this.birth;
        }
    }
}
