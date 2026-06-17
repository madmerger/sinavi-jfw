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

package jp.co.ctc_g.jfw.profill;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;

public class ProfillFactoryBeanTest {

    private ProfillFactoryBean factory;
    
    @BeforeEach
    public void instantiate() {
        factory = new ProfillFactoryBean();
    }
    
    @Test
    public void BeanFactoryが設定されていない場合はafterPrpertiesSetを実行できない() throws Exception {
        assertThrows(InternalException.class, () -> {
            factory.afterPropertiesSet();
        });
    }
    
    @Test
    public void providerRefsが設定されている場合はそれがプロバイダに追加されている() throws Exception {
        factory.setBeanFactory(new BeanFactoryStub() {
            @Override public <T> T getBean(String name, Class<T> requiredType)
                    throws BeansException {
                return requiredType.cast(new AnnotatedStringLiteralFillingProvider());
            }
        });
        factory.setProviderRefs(Arrays.gen("meaningless"));
        factory.afterPropertiesSet();
        Profill profill = factory.getObject();
        assertThat(profill.matchers.size(), is(1));
    }
    
    @Test
    public void providerTypesが設定されている場合はそれがプロバイダに追加されている() throws Exception {
        factory.setBeanFactory(new BeanFactoryStub());
        factory.setProviderTypes(Arrays.gen(AnnotatedStringLiteralFillingProvider.class.getName()));
        factory.afterPropertiesSet();
        Profill profill = factory.getObject();
        assertThat(profill.matchers.size(), is(1));
    }
    
    @Test
    public void providersが設定されている場合はそれがプロバイダに追加されている() throws Exception {
        factory.setBeanFactory(new BeanFactoryStub());
        factory.setProviders(Arrays.gen(new AnnotatedStringLiteralFillingProvider()));
        factory.afterPropertiesSet();
        Profill profill = factory.getObject();
        assertThat(profill.matchers.size(), is(1));
    }
}
