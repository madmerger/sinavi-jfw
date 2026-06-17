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

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import jp.co.ctc_g.jfw.core.util.GenCall;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jfw.profill.ProfillIntegrationTestBean.ChildBean;
import jp.co.ctc_g.jfw.profill.ProfillIntegrationTestBean.ParentBean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfillIntegrationTest {

    private Profill profill;
    
    @BeforeEach
    public void instantiate() {
        profill = new Profill();
    }
    
    @Test
    public void アノテーションで指定されたプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.fill(bean);
        assertThat(bean.getAnnotationInjection(), is(notNullValue()));
        assertThat(bean.getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(bean.getNameInjection(), is(nullValue()));
        assertThat(bean.getTypeInjection(), is(nullValue()));
    }
    
    @Test
    public void プロパティ名で指定されたプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        profill.addFillingProvider(new NameMatchedFillingProvider());
        profill.fill(bean);
        assertThat(bean.getNameInjection(), is(notNullValue()));
        assertThat(bean.getNameInjection(), is(NameMatchedFillingProvider.MATCHED));
        assertThat(bean.getAnnotationInjection(), is(nullValue()));
        assertThat(bean.getTypeInjection(), is(nullValue()));
    }
    
    @Test
    public void プロパティ名で複数マッチした場合もプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        profill.addFillingProvider(new MultiNameMatchedFillingProvider());
        profill.fill(bean);
        assertThat(bean.getAnnotationInjection(), is(notNullValue()));
        assertThat(bean.getAnnotationInjection(), is(MultiNameMatchedFillingProvider.MULTI_MATCHED));
        assertThat(bean.getNameInjection(), is(notNullValue()));
        assertThat(bean.getNameInjection(), is(MultiNameMatchedFillingProvider.MULTI_MATCHED));
        assertThat(bean.getTypeInjection(), is(nullValue()));
    }
    
    @Test
    public void プロパティ型で指定されたプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        profill.addFillingProvider(new TypeMatchedFillingProvider());
        profill.fill(bean);
        assertThat(bean.getTypeInjection(), is(notNullValue()));
        assertThat(bean.getTypeInjection(), is(TypeMatchedFillingProvider.MATCHED));
        assertThat(bean.getAnnotationInjection(), is(nullValue()));
        assertThat(bean.getNameInjection(), is(nullValue()));
    }
    
    @Test
    public void アノテーションとプロパティ名マッチの場合にプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        // この場合はアノテーションで1回、名前マッチでもう1回代入されることになる仕様
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.addFillingProvider(new MultiNameMatchedFillingProvider());
        profill.fill(bean);
        // よってannotationInjectionプロパティの値はINJECTEDではなくMULTI_MATCHEDになる
        assertThat(bean.getAnnotationInjection(), is(notNullValue()));
        assertThat(bean.getAnnotationInjection(), is(MultiNameMatchedFillingProvider.MULTI_MATCHED));
        assertThat(bean.getNameInjection(), is(notNullValue()));
        assertThat(bean.getNameInjection(), is(MultiNameMatchedFillingProvider.MULTI_MATCHED));
        assertThat(bean.getTypeInjection(), is(nullValue()));
    }
    
    @Test
    public void プロパティ名とアノテーションマッチの場合にプロパティに値が自動的に設定される() {
        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
        // この場合は名前マッチで1回、アノテーションマッチでもう1回代入されることになる仕様
        profill.addFillingProvider(new MultiNameMatchedFillingProvider());
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.fill(bean);
        // よってannotationInjectionプロパティの値はMULTI_MATCHEDではなくINJECTEDになる
        assertThat(bean.getAnnotationInjection(), is(notNullValue()));
        assertThat(bean.getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(bean.getNameInjection(), is(notNullValue()));
        assertThat(bean.getNameInjection(), is(MultiNameMatchedFillingProvider.MULTI_MATCHED));
        assertThat(bean.getTypeInjection(), is(nullValue()));
    }
    
    // ネストテスト ----------------------------------------------------------------------
    
    @Test
    public void ネストされたビーンのプロパティに値が自動的に設定される() {
        ParentBean parent = new ParentBean();
        parent.setChild(new ChildBean());
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.fill(parent);
        assertThat(parent.getAnnotationInjection(), is(notNullValue()));
        assertThat(parent.getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(parent.getChild().getAnnotationInjection(), is(notNullValue()));
        assertThat(parent.getChild().getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
    }
    
    @Test
    public void ネストされたビーンがnullの場合も設定によりプロパティに値が自動的に設定される() {
        ParentBean parent = new ParentBean();
        parent.setChild(null);
        profill.setTryToInstantiateIfNestedPropertyIsNull(true);
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.fill(parent);
        assertThat(parent.getAnnotationInjection(), is(notNullValue()));
        assertThat(parent.getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
        assertThat(parent.getChild().getAnnotationInjection(), is(notNullValue()));
        assertThat(parent.getChild().getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
    }
    
    // マルチスレッドテスト --------------------------------------------------------------
    
    @Test
    public void マルチスレッドアクセスでも正常にプロパティ値が設定される() throws InterruptedException {
        final int THREAD_COUNT = 0xFF;
        final Boolean[] called = new Boolean[THREAD_COUNT];
        final Profill profill = new Profill();
        profill.addFillingProvider(new AnnotatedStringLiteralFillingProvider());
        profill.makeSafeAgainstMultiThreadedAccess();
        List<Thread> ts = Lists.gen(THREAD_COUNT, new GenCall<Thread>() {
            @Override public Thread gen(final int index, int total) {
                called[index] = false;
                return new Thread(new Runnable() {
                    @Override public void run() {
                        ProfillIntegrationTestBean bean = new ProfillIntegrationTestBean();
                        profill.fill(bean);
                        assertThat(bean.getAnnotationInjection(), is(notNullValue()));
                        assertThat(bean.getAnnotationInjection(), is(AnnotatedStringLiteralFillingProvider.INJECTED));
                        assertThat(bean.getNameInjection(), is(nullValue()));
                        assertThat(bean.getTypeInjection(), is(nullValue()));
                        called[index] = true;
                    }
                });
            }
        });
        for (Thread t : ts) {
            t.start();
        }
        for (Thread t : ts) {
            t.join();
        }
        assertThat(Lists.gen(called), everyItem(is(true)));
    }    
}
