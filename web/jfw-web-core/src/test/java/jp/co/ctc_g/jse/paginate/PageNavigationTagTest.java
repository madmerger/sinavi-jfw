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

package jp.co.ctc_g.jse.paginate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.PartialList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

public class PageNavigationTagTest {

    private MockPageContext context;

    private MockHttpServletResponse resp;

    private MockHttpServletRequest req;

    private RequestContext rcon;

    @Before
    public void setUp() {

        MockServletContext sc = new MockServletContext();
        WebApplicationContext wac = new StaticWebApplicationContext();
        sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        req = new MockHttpServletRequest(sc);
        resp = new MockHttpServletResponse();
        context = new MockPageContext(sc, req, resp);
        rcon = new RequestContext(req, sc);
        context.setAttribute(RequestContextAwareTag.REQUEST_CONTEXT_PAGE_ATTRIBUTE, rcon);
    }

    @Test
    public void 初期化される() {

        PageNavigationTag tag = new PageNavigationTag();
        tag.setPartial(new PartialList<String>());
        tag.setUrl("url");
        tag.setAction("action");
        tag.setDisplayCount(100);
        tag.setParam("param");
        tag.setVar("number");
        tag.release();
        assertThat(tag.getPartial(), nullValue());
        assertThat(tag.getUrl(), nullValue());
        assertThat(tag.getAction(), nullValue());
        assertThat(tag.getDisplayCount(), is(10));
        assertThat(tag.getParam(), is("pageNumber"));
        assertThat(tag.getVar(), is("page"));
        assertThat(tag.getHead(), nullValue());
        assertThat(tag.getPrevious(), nullValue());
        assertThat(tag.getPage(), nullValue());
        assertThat(tag.getOmission(), nullValue());
        assertThat(tag.getNext(), nullValue());
        assertThat(tag.getTail(), nullValue());
    }

    @Test
    public void 要素が空の場合は何も出力されない() throws Exception {

        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(new PartialList<String>());
        tag.doStartTag();
        tag.doEndTag();
        assertThat(resp.getContentAsString(), is(""));
    }

    @Test
    public void 表示ページ数よりも要素のページ数が小さい場合はページ番号がすべて表示される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(1);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.setUrl("");
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(14));
        assertThat(Arrays.asList(split), hasItem("&lt;&lt;"));
        assertThat(Arrays.asList(split), hasItem("&lt;"));
        assertThat(Arrays.asList(split), hasItem("1"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=10\">10</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=2\">&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=10\">&gt;&gt;</a>"));
    }

    @Test
    public void URLが設定される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(1);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setUrl("/list");
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(14));
        assertThat(Arrays.asList(split), hasItem("&lt;&lt;"));
        assertThat(Arrays.asList(split), hasItem("&lt;"));
        assertThat(Arrays.asList(split), hasItem("1"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=10\">10</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=2\">&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=10\">&gt;&gt;</a>"));
    }

    @Test
    public void 最後のページの場合は次へと最後へのリンクが生成されない() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(10);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(14));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">&lt;&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=9\">&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">1</a>"));
        assertThat(Arrays.asList(split), hasItem("10"));
        assertThat(Arrays.asList(split), hasItem("&gt;"));
        assertThat(Arrays.asList(split), hasItem("&gt;&gt;"));
    }

    @Test
    public void 表示ページ数よりも要素のページ数が大きい場合は表示ページ数と省略記号が表示される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 110; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(110);
        display.setPartCount(11);
        display.setElementCountPerPart(10);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_omission\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(15));
        assertThat(Arrays.asList(split), hasItem("&lt;&lt;"));
        assertThat(Arrays.asList(split), hasItem("&lt;"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">1</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=10\">10</a>"));
        assertThat(Arrays.asList(split), hasItem("..."));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">&gt;&gt;</a>"));
    }

    @Test
    public void 表示ページ数よりも要素のページ数が大きい場合は2_11表示ページ数と省略記号が表示される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 110; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(110);
        display.setPartCount(11);
        display.setElementCountPerPart(10);
        display.setPartIndex(7);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_omission\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(15));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">&lt;&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=6\">&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=2\">2</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">11</a>"));
        assertThat(Arrays.asList(split), hasItem("7"));
        assertThat(Arrays.asList(split), hasItem("..."));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=8\">&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">&gt;&gt;</a>"));
    }

    @Test
    public void jspFragmentが設定される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 110; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(110);
        display.setPartCount(11);
        display.setElementCountPerPart(10);
        display.setPartIndex(7);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        JspFragment fragment = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("----");
            }
        };
        tag.setOmission(fragment);
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_omission\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(15));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">&lt;&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=6\">&lt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=2\">2</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">11</a>"));
        assertThat(Arrays.asList(split), hasItem("7"));
        assertThat(Arrays.asList(split), hasItem("----"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=8\">&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">&gt;&gt;</a>"));
    }

    @Test
    public void ページ送りの文字列が設定される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 110; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(110);
        display.setPartCount(11);
        display.setElementCountPerPart(10);
        display.setPartIndex(7);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        JspFragment head = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("&lt;&lt;-");
            }
        };
        tag.setHead(head);
        JspFragment previous = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("&lt;-");
            }
        };
        tag.setPrevious(previous);
        JspFragment page = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("|");
            }
        };
        tag.setPage(page);
        JspFragment next = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("-&gt;");
            }
        };
        tag.setNext(next);
        tag.setPrevious(previous);
        JspFragment tail = new JspFragment() {

            @Override
            public JspContext getJspContext() {
                return context;
            }

            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write("-&gt;&gt;");
            }
        };
        tag.setTail(tail);
        tag.doStartTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_head\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_previous\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_page_selected\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_next\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_tail\">", "");
        actual = actual.replaceAll("<span class=\"jfw_paginate_navi_omission\">", "");
        actual = actual.replaceAll("</span>", "\n");
        String[] split = actual.split("\n");
        assertThat(split.length, is(15));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=1\">&lt;&lt;-</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=6\">&lt;-</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=2\">|</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">|</a>"));
        assertThat(Arrays.asList(split), hasItem("|"));
        assertThat(Arrays.asList(split), hasItem("..."));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=8\">-&gt;</a>"));
        assertThat(Arrays.asList(split), hasItem("<a href=\"/list?pageNumber=11\">-&gt;&gt;</a>"));
    }

    @Test(expected = InternalException.class)
    public void ActionもURLも設定されていない場合は例外が発生する() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(1);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.doStartTag();
        tag.doEndTag();
    }

    @Test
    public void パラメータが設定される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(1);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.doStartTag();
        PageNavigationParameterTag parameter = new PageNavigationParameterTag();
        parameter.setParent(tag);
        parameter.setJspContext(context);
        parameter.setName("hoge");
        parameter.setValue("bar");
        parameter.doTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        assertThat(actual, is(containsString("hoge=bar")));
    }

    @Test
    public void パラメータが複数設定される() throws Exception {

        PartialList<String> display = new PartialList<String>();
        for (int i = 0; i < 100; i++) {
            display.add(Integer.toString(i));
        }
        display.setElementCount(100);
        display.setPartCount(10);
        display.setElementCountPerPart(10);
        display.setPartIndex(1);
        PageNavigationTag tag = new PageNavigationTag();
        tag.setPageContext(context);
        tag.setPartial(display);
        tag.setAction("/list");
        tag.doStartTag();
        PageNavigationParameterTag parameter1 = new PageNavigationParameterTag();
        parameter1.setParent(tag);
        parameter1.setJspContext(context);
        parameter1.setName("hoge");
        parameter1.setValue("bar1");
        parameter1.doTag();
        PageNavigationParameterTag parameter2 = new PageNavigationParameterTag();
        parameter2.setParent(tag);
        parameter2.setJspContext(context);
        parameter2.setName("hoge");
        parameter2.setValue("bar2");
        parameter2.doTag();
        tag.doEndTag();
        String actual = resp.getContentAsString();
        assertThat(actual, is(containsString("hoge=bar1")));
        assertThat(actual, is(containsString("hoge=bar2")));
    }
}
