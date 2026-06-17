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

package jp.co.ctc_g.jse.vid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public class ViewIdDefinitionTagTest {

    private MockPageContext context;
    private MockHttpServletResponse res;
    private MockHttpServletRequest req;

    @BeforeEach
    public void setup() {

        MockServletContext sc = new MockServletContext();
        req = new MockHttpServletRequest(sc);
        res = new MockHttpServletResponse();
        context = new MockPageContext(sc, req, res);
    }

    @Test
    public void 初期値が設定されているかどうか() throws Exception {

        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        assertThat(tag.getInclude(), is(nullValue()));
        assertThat(tag.getExclude(), is(nullValue()));
    }

    @Test
    public void デフォルトではパラメータが保存されない() throws Exception {

        req.setParameter("key", "value");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.doStartTag();
        tag.doEndTag();

        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key"), is(nullValue()));
    }

    @Test
    public void includeに指定したパラメータがViewIdにパラメータとして設定される() throws Exception {

        req.setParameter("key", "value");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setInclude("key");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key")[0], is("value"));
    }

    @Test
    public void includeに指定したパラメータのみがViewIdにパラメータとして設定される() throws Exception {

        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setInclude("key1");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key1")[0], is("value1"));
        assertThat(id.getParams().get("key2"), is(nullValue()));
    }

    @Test
    public void includeにアスタリスクを指定した場合に全てのパラメータがViewIdにパラメータとして設定される() throws Exception {

        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setInclude("*");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key1")[0], is("value1"));
        assertThat(id.getParams().get("key2")[0], is("value2"));
        assertThat(id.getParams().get("key3")[0], is("value3"));
    }

    @Test
    public void excludeに指定したパラメータがViewIdにパラメータとして設定されない() throws Exception {

        req.setParameter("key", "value");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setExclude("key");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key"), is(nullValue()));
    }

    @Test
    public void excludeに指定したパラメータ以外がViewIdにパラメータとして設定される() throws Exception {

        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setExclude("key2,key3");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().size(), is(2));
        assertThat(id.getParams().get("key1")[0], is("value1"));
        assertThat(id.getParams().get("key4")[0], is("value4"));
    }

    @Test
    public void excludeの指定がincludeの指定より優先される() throws Exception {

        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setExclude("key2,key3");
        tag.setInclude("key1,key2");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().size(), is(2));
        assertThat(id.getParams().get("key1")[0], is("value1"));
        assertThat(id.getParams().get("key4")[0], is("value4"));
    }

    @Test
    public void excludeの指定がincludeのアスタリスクの指定より優先される() throws Exception {

        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        tag.setExclude("key2,key3");
        tag.setInclude("*");
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().size(), is(2));
        assertThat(id.getParams().get("key1")[0], is("value1"));
        assertThat(id.getParams().get("key4")[0], is("value4"));
    }

    @Test
    public void Requestスコープを指定したときに例外が発生するかどうか() throws Exception {
        assertThrows(InternalException.class, () -> {
    
            ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
            tag.setScope("request");
        });
    }

    @Test
    public void ViewIdがセッションに設定されるかどうか() throws Exception {

        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("画面A");
        tag.setUrl("/foo/bar/baz.do");
        tag.setPankuzu(true);
        tag.setScope("session");
        ViewIdParameterTag parameter = new ViewIdParameterTag();
        parameter.setParent(tag);
        parameter.setJspContext(context);
        parameter.setName("key");
        parameter.setValue("value");
        parameter.doTag();
        tag.doStartTag();
        tag.doEndTag();
        assertThat(tag.getId(), is(nullValue()));
        assertThat(tag.getLabel(), is(nullValue()));
        assertThat(tag.getUrl(), is(nullValue()));
        assertThat(tag.getScope(), is("session"));
        ViewId id = ViewId.current(req);
        assertThat(id.getId(), is("VID#0001"));
        assertThat(id.getLabel(), is("画面A"));
        assertThat(id.getUrl(), is("/foo/bar/baz.do"));
        assertThat(id.getParams().get("key")[0], is("value"));
    }

}
