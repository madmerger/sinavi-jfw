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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/jp/co/ctc_g/jfw/profill/ProfillInterceptorAnnotationAwareIntegrationTestContext.xml")
public class ProfillInterceptorIntegrationAnnotationAwareTest {

    @Autowired
    public ProfillTestBoundary boundary;
    
    @Test
    public void ビーンのプロパティに値が設定されている() {
        ProfillInterceptorIntegrationTestBean bean = new ProfillInterceptorIntegrationTestBean();
        boundary.invokeWithDefaultAnnotatedParameter(bean);
        assertThat(bean.getString(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(bean.getString(), is(notNullValue())); // トランザクション時間を特定できない
    }
    
    @Test
    public void 複数のビーンのプロパティに値が設定されている() {
        ProfillInterceptorIntegrationTestBean first = new ProfillInterceptorIntegrationTestBean();
        ProfillInterceptorIntegrationTestBean second = new ProfillInterceptorIntegrationTestBean();
        boundary.invokeWithDefaultAnnotatedParameter(first, second);
        assertThat(first.getString(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(first.getString(), is(notNullValue())); // トランザクション時間を特定できない
        assertThat(second.getString(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(second.getString(), is(notNullValue())); // トランザクション時間を特定できない
    }
    
    @Test
    public void マーキングされていないビーンはプロパティに値が設定されていない() {
        ProfillInterceptorIntegrationTestBean first = new ProfillInterceptorIntegrationTestBean();
        ProfillInterceptorIntegrationTestBean not = new ProfillInterceptorIntegrationTestBean();
        ProfillInterceptorIntegrationTestBean second = new ProfillInterceptorIntegrationTestBean();
        boundary.invokeWithDefaultAnnotatedParameter(first, not, second);
        assertThat(first.getString(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(first.getString(), is(notNullValue())); // トランザクション時間を特定できない
        assertThat(not.getString(), is(nullValue()));
        assertThat(not.getUpdateStamp(), is(nullValue()));
        assertThat(second.getString(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(second.getString(), is(notNullValue())); // トランザクション時間を特定できない
    }
}
