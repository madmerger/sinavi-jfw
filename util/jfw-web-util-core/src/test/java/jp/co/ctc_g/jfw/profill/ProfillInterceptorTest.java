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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.annotation.Resource;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfillInterceptorTest {

    private ProfillInterceptor interceptor;
    
    @BeforeEach
    public void instantiate() {
        interceptor = new ProfillInterceptor();
    }
    
    @Test
    public void BeanFactoryが設定されていない場合はafterPrpertiesSetを実行できない() throws Exception {
        assertThrows(InternalException.class, () -> {
            interceptor.afterPropertiesSet();
        });
    }
    
    @Test
    public void プロフィルが設定されていない場合はBeanFactoryから型一致で検索する() throws Exception {
        interceptor.setBeanFactory(new BeanFactoryStub() {
            @Override public <T> T getBean(Class<T> requiredType) throws BeansException {
                return requiredType.cast(new Profill());
            }
        });
        interceptor.afterPropertiesSet();
        assertThat(interceptor.getProfill(), is(notNullValue()));
    }
    
    @Test
    public void マーカーアノテーションが必要な場合はデフォルトではProfillableが設定される() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.setParameterAnnotationRequired(true);
        interceptor.afterPropertiesSet();
        assertThat(interceptor.markers, is(notNullValue()));
        assertThat(interceptor.markers.length, is(1));
        assertThat(interceptor.markers[0] == Profillable.class, is(true));
    }
    
    @Test
    public void マーカーアノテーションが必要な場合は任意のアノテーションを設定できる() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.setParameterAnnotationRequired(true);
        interceptor.setMarkerAnnotationTypes(Arrays.gen(
                Autowired.class.getName(), Resource.class.getName()));
        interceptor.afterPropertiesSet();
        assertThat(interceptor.markers, is(notNullValue()));
        assertThat(interceptor.markers.length, is(2));
        assertThat(interceptor.markers[0] == Autowired.class, is(true));
        assertThat(interceptor.markers[1] == Resource.class, is(true));
    }
    
    @Test
    public void マーカーアノテーションが必要ない場合は常にtrueが返却される() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.afterPropertiesSet();
        assertThat(interceptor.isProfillable(null), is(true));
        assertThat(interceptor.isProfillable(Arrays.gen(new AnnotationStub())), is(true));
    }
    
    @Test
    public void マーカーアノテーションが必要な場合にアノテーションがパラメータに指定されていないならfalseが返却される() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.setParameterAnnotationRequired(true);
        interceptor.afterPropertiesSet();
        assertThat(interceptor.isProfillable(null), is(false));
        assertThat(interceptor.isProfillable(new AnnotationStub[0]), is(false));
    }
    
    @Test
    public void マーカーアノテーションが必要な場合にアノテーションタイプが一致するとtrueが返却される() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.setParameterAnnotationRequired(true);
        interceptor.afterPropertiesSet();
        assertThat(interceptor.isProfillable(Arrays.gen(new AnnotationStub(Profillable.class))), is(true));
    }
    
    @Test
    public void マーカーアノテーションが必要な場合にアノテーションタイプが一致しないとfalseが返却される() throws Exception {
        interceptor.setProfill(new Profill());
        interceptor.setParameterAnnotationRequired(true);
        interceptor.setMarkerAnnotationTypes(Arrays.gen(Autowired.class.getName()));
        interceptor.afterPropertiesSet();
        assertThat(interceptor.isProfillable(Arrays.gen(new AnnotationStub(Profillable.class))), is(false));
    }
}
