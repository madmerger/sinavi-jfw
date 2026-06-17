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
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspWriter;

import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.framework.Controllers;
import jp.co.ctc_g.jse.vid.adaptor.HttpServletRequestAdaptor;
import jp.co.ctc_g.jse.vid.adaptor.HttpSessionAdaptor;
import jp.co.ctc_g.jse.vid.adaptor.JspWriterAdaptor;
import jp.co.ctc_g.jse.vid.adaptor.PageContextAdaptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;


public class PankuzuTagTest {

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
    public void デフォルト状態ではリクエストパラメータが引き継がれない() throws Exception {
        req.setParameter("key1", "value1");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key2", "value2");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        assertThat(res.getContentAsString(), is("<span class=\"jfw_vid_pankuzu_item\"><a href=\"/foo/bar/baz1.do\">ViewA</a></span>"));
    }

    @Test
    public void 指定したリクエストパラメータが引き継がれる() throws Exception {
        req.setParameter("key1", "value1");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setInclude("key1");
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key2", "value2");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        assertThat(res.getContentAsString(), is("<span class=\"jfw_vid_pankuzu_item\"><a href=\"/foo/bar/baz1.do?key1=value1\">ViewA</a></span>"));
    }

    @Test
    public void 指定したリクエストパラメータのみが引き継がれる() throws Exception {
        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setInclude("key1,key3");
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key3", "value3");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        String actual = res.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_vid_pankuzu_item\">", "");
        actual = actual.replaceAll("</span>", "");
        actual = actual.replaceAll("<a href=\"/foo/bar/baz1.do\\?", "");
        actual = actual.replaceAll("\">ViewA</a>", "");
        String[] linkTags = actual.split("&");
        assertThat(Arrays.asList(linkTags).size(), is(2));
        assertThat(Arrays.asList(linkTags), hasItem("key1=value1"));
        assertThat(Arrays.asList(linkTags), hasItem("key3=value3"));
    }

    @Test
    public void アスタリスクを指定し全てのリクエストパラメータが引き継がれる() throws Exception {
        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setInclude("*");
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key5", "value5");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        String actual = res.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_vid_pankuzu_item\">", "");
        actual = actual.replaceAll("</span>", "");
        actual = actual.replaceAll("<a href=\"/foo/bar/baz1.do\\?", "");
        actual = actual.replaceAll("\">ViewA</a>", "");
        String[] linkTags = actual.split("&");
        assertThat(Arrays.asList(linkTags).size(), is(4));
        assertThat(Arrays.asList(linkTags), hasItem("key1=value1"));
        assertThat(Arrays.asList(linkTags), hasItem("key2=value2"));
        assertThat(Arrays.asList(linkTags), hasItem("key3=value3"));
        assertThat(Arrays.asList(linkTags), hasItem("key4=value4"));
    }

    @Test
    public void 指定したリクエストパラメータが除外される() throws Exception {
        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setExclude("key2,key4");
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key3", "value3");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        String actual = res.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_vid_pankuzu_item\">", "");
        actual = actual.replaceAll("</span>", "");
        actual = actual.replaceAll("<a href=\"/foo/bar/baz1.do\\?", "");
        actual = actual.replaceAll("\">ViewA</a>", "");
        String[] linkTags = actual.split("&");
        assertThat(Arrays.asList(linkTags).size(), is(2));
        assertThat(Arrays.asList(linkTags), hasItem("key1=value1"));
        assertThat(Arrays.asList(linkTags), hasItem("key3=value3"));
    }

    @Test
    public void 除外設定が優先される() throws Exception {
        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setExclude("key2,key4");
        tag.setInclude("key1,key2");
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key3", "value3");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        String actual = res.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_vid_pankuzu_item\">", "");
        actual = actual.replaceAll("</span>", "");
        actual = actual.replaceAll("<a href=\"/foo/bar/baz1.do\\?", "");
        actual = actual.replaceAll("\">ViewA</a>", "");
        String[] linkTags = actual.split("&");
        assertThat(Arrays.asList(linkTags).size(), is(2));
        assertThat(Arrays.asList(linkTags), hasItem("key1=value1"));
        assertThat(Arrays.asList(linkTags), hasItem("key3=value3"));
    }

    @Test
    public void ViewIdParameterTagで指定したパラメータが設定される() throws Exception {
        req.setParameter("key1", "value1");
        req.setParameter("key2", "value2");
        req.setParameter("key3", "value3");
        req.setParameter("key4", "value4");
        ViewIdDefinitionTag tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0001");
        tag.setLabel("ViewA");
        tag.setUrl("/foo/bar/baz1.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.setExclude("key2,key4");
        tag.setInclude("key1,key2");
        ViewIdParameterTag nTag = new ViewIdParameterTag();
        nTag.setParent(tag);
        nTag.setJspContext(context);
        nTag.setName("key5");
        nTag.setValue("value5");
        nTag.doTag();
        tag.doStartTag();
        tag.doEndTag();

        req.setParameter("key6", "value6");
        tag = new ViewIdDefinitionTag();
        tag.setPageContext(context);
        tag.setId("VID#0002");
        tag.setLabel("ViewB");
        tag.setUrl("/foo/bar/baz2.do");
        tag.setPankuzu(true);
        tag.setScope(Controllers.SCOPE_SESSION);
        tag.doStartTag();
        tag.doEndTag();

        PankuzuTag pTag = new PankuzuTag();
        pTag.setJspContext(context);
        pTag.setScope(Controllers.SCOPE_SESSION);
        pTag.doTag();

        String actual = res.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_vid_pankuzu_item\">", "");
        actual = actual.replaceAll("</span>", "");
        actual = actual.replaceAll("<a href=\"/foo/bar/baz1.do\\?", "");
        actual = actual.replaceAll("\">ViewA</a>", "");
        String[] linkTags = actual.split("&");
        assertThat(Arrays.asList(linkTags).size(), is(1));
        assertThat(Arrays.asList(linkTags), hasItem("key5=value5"));
    }

    @Test
    public void URLにパラメータがついていた場合はクエリのハテナがアンドになる() {
        String url = "http://foo/bar?buz=cux";
        PankuzuTag tag = new PankuzuTag();
        String trimed = tag.trimQueryMarkerIfGetRequestParameterExists(url, "?addtional=query");
        assertThat(ViewId.QUERIES_JOIN_WORD + "addtional=query", is(trimed));
    }
    
    @Test
    public void URLにパラメータがついない場合はクエリのハテナがアンドにならない() {
        String url = "http://foo/bar";
        PankuzuTag tag = new PankuzuTag();
        String trimed = tag.trimQueryMarkerIfGetRequestParameterExists(url, "?addtional=query");
        assertThat("?addtional=query", is(trimed));
    }
    
    @Test
    public void リクエストパラメータがエンコードされているかどうかのテスト() throws Exception {
        final String url = "http://www.ctc-g.co.jp/";
        final String label = "<div>テスト</div>";
        final String data = "[]^_`:;<=>?@{|}~ !\"#$%&'()*+,-./";
        final String escaped = "%5B%5D%5E_%60%3A%3B%3C%3D%3E%3F%40%7B%7C%7D%7E+%21%22%23%24%25%26%27%28%29*%2B%2C-.%2F";
        final String template = "<span class=\"jfw_vid_pankuzu_item\"><a href=\"${url}${query}\">${label}</a></span>";
        final String expected = Strings.substitute(
                template,
                Maps.hash("url", url)
                    .map("query", "?" + Strings.joinBy("=", escaped, escaped))
                    .map("label", Strings.escapeHTML(label)));
        PankuzuTag tag = new PankuzuTag();
        tag.setJspContext(new PageContextAdaptor() {

            Map<String, Object> attributes = new HashMap<String, Object>();

            @Override
            public HttpServletRequest getRequest() {
                return new HttpServletRequestAdaptor() {
                    @Override
                    public HttpSession getSession() {
                        return new HttpSessionAdaptor() {
                            @Override
                            public Object getAttribute(String key) {
                                LinkedList<ViewId> container = null;
                                if (ViewId.VIEW_ID_CONTAINER_KEY.equals(key)) {
                                    container = new LinkedList<ViewId>();
                                    // 1
                                    ViewId id = new ViewId(data + "1");
                                    id.setUrl(url);
                                    id.setLabel(label);
                                    id.setParams(new HashMap<String, String[]>(Maps
                                        .hash(data, jp.co.ctc_g.jfw.core.util.Arrays.gen(data))));
                                    id.setPankuzu(true);
                                    id.freeze();
                                    container.add(id);
                                    // 2
                                    id = new ViewId(data + "2");
                                    id.setUrl(url);
                                    id.setLabel(label);
                                    id.setParams(new HashMap<String, String[]>(Maps
                                        .hash(data, jp.co.ctc_g.jfw.core.util.Arrays.gen(data))));
                                    id.setPankuzu(true);
                                    id.freeze();
                                    container.add(id);
                                }
                                return container;
                            }

                            @Override
                            public String getId() {
                                return UUID.randomUUID().toString();
                            }
                        };
                    }
                };
            }
            
            @Override
            public JspWriter getOut() {
                return new JspWriterAdaptor() {
                    @Override
                    public void print(String actual) throws IOException {
                        assertEquals(expected, actual);
                    }
                };
            }

            @Override
            public void setAttribute(String key, Object value) {
                attributes.put(key, value);
            }

            @Override
            public Object getAttribute(String key) {
                return attributes.get(key);
            }

            @Override
            public ServletResponse getResponse() {
                return new MockHttpServletResponse();
            }
        });
        tag.doTag();
    }
}
