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

package jp.co.ctc_g.jse.core.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public class JseRsTagTest {

    private MockPageContext context;

    private MockHttpServletResponse res;

    private MockHttpServletRequest req;

    @BeforeAll
    public static void setupClass() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(new String[] {
            "classpath:/jp/co/ctc_g/jse/core/message/JseRsTagTest",
            "classpath:/jp/co/ctc_g/jse/core/message/JseRsTagTestResources"
        });
        MessageSourceLocator.set(messageSource);
    }

    @BeforeEach
    public void setup() {

        MockServletContext sc = new MockServletContext();
        req = new MockHttpServletRequest(sc);
        res = new MockHttpServletResponse();
        res.setCharacterEncoding("UTF-8");
        context = new MockPageContext(sc, req, res);
    }

    @Test
    public void キーが設定されていない場合は例外が発生するかどうか() throws Exception {
        assertThrows(InternalException.class, () -> {
    
            JseRsTag tag = new JseRsTag();
            tag.setPageContext(context);
            tag.doEndTag();
            assertThat(res.getContentAsString(), is(""));
        });
    }

    @Test
    public void 値が出力されるかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0001");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0001出力OKのテスト"));
    }


    @Test
    public void エスケープされているかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0002");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0002&lt;br/&gt; エスケープテスト"));
    }

    @Test
    public void エスケープされないかかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0002");
        tag.setEscapeRequired(Boolean.FALSE);
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0002<br/> エスケープテスト"));
    }

    @Test
    public void 指定されたリソースから値が取得できるかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0003");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0003別のリソース参照テスト"));
    }

    @Test
    public void 複数指定されたリソースから値が取得できるかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0003");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0003別のリソース参照テスト"));
    }

    @Test
    public void 指定されたリソースから値が取得できない場合にキーが出力されるかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0004");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0004"));
    }

    @Test
    public void 初期値が設定されているかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        assertTrue("".equals(tag.getCode()));
        assertTrue(tag.isEscapeRequired());
    }

    @Test
    public void 初期化されるかどうか() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setCode("key");
        tag.setEscapeRequired(false);
        tag.release();
        assertTrue("".equals(tag.getCode()));
        assertTrue(tag.isEscapeRequired());
    }

    @Test
    public void Replaceが設定されている場合に置換されるかどうかのテスト() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0005");
        JseRsReplaceTag replace = new JseRsReplaceTag();
        replace.setParent(tag);
        replace.setName("foo");
        replace.setValue("bar");
        replace.doTag();
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0005barのテスト"));
    }

    @Test
    public void Replaceが複数設定されている場合に置換されるかどうかのテスト() throws Exception {

        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("TEST#0006");
        JseRsReplaceTag replace = new JseRsReplaceTag();
        replace.setParent(tag);
        replace.setName("hoge");
        replace.setValue("置換複数");
        replace.doTag();
        replace = new JseRsReplaceTag();
        replace.setParent(tag);
        replace.setName("foo");
        replace.setValue("bar");
        replace.doTag();
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("TEST#0006置換複数barのテスト"));
    }
    
    @Test
    public void キーにCriteriaのSuffixが指定されリソースに定義されていない場合にSuffixが取り除かれて値が取得できるかのテスト() throws Exception {
        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("cdCriteria");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("ドメイン"));
    }
    
    @Test
    public void キーにCriteriaのSuffixが指定されリソースに定義されている場合に優先して値が取得できるかのテスト() throws Exception {
        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("testCriteria");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("クライテリア"));
    }
    
    @Test
    public void キーにSelectionのSuffixが指定されリソースに定義されていない場合にSuffixが取り除かれて値が取得できるかのテスト() throws Exception {
        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("cdSelection");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("ドメイン"));
    }
    
    @Test
    public void キーにSelectionのSuffixが指定されリソースに定義されている場合に優先して値が取得できるかのテスト() throws Exception {
        JseRsTag tag = new JseRsTag();
        tag.setPageContext(context);
        tag.setCode("testSelection");
        tag.doEndTag();
        assertThat(res.getContentAsString(), is("選択"));
    }

}
