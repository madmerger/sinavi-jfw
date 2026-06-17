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

package jp.co.ctc_g.jse.core.util.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jse.core.util.web.TagUtils.UrlType;

public class TagUtilsTest {

    private MockPageContext page;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        response = new MockHttpServletResponse();
        page = new MockPageContext(sc, request, response);
    }
    
    @Test
    public void 絶対パスとして判定される() {
        String path = "http://www.ctc-g.co.jp";
        UrlType type = TagUtils.getUrlType(path);
        assertThat(type, is(UrlType.ABSOLUTE));
    }
    
    @Test
    public void コンテキストパスからの相対パスとして判定される() {
        String path = "/app/test";
        UrlType type = TagUtils.getUrlType(path);
        assertThat(type, is(UrlType.CONTEXT_RELATIVE));
    }
    
    @Test
    public void の相対パスとして判定される() {
        String path = "test";
        UrlType type = TagUtils.getUrlType(path);
        assertThat(type, is(UrlType.RELATIVE));
    }

    @Test
    public void パスパラメータが置換される() throws Exception {
        String path = "app/{groupId}/{userId}";
        Map<String, String[]> params = Maps.hash("groupId", Arrays.gen("test")).map("userId", Arrays.gen("20120001"));
        String replacedPath = TagUtils.replaceUriTemplateParams(path, params, page);
        assertThat(replacedPath, is("app/test/20120001"));
    }

    @Test
    public void クエリ文字列が生成される() throws Exception {
        Map<String, String[]> params = Maps.hash("groupId", Arrays.gen("test")).map("userId", Arrays.gen("20120001"));
        String queryString = TagUtils.createQueryString(params, true, page);
        assertThat(queryString, is("?groupId=test&userId=20120001"));
    }
    
    @Test
    public void URLが生成される() throws Exception {
        String path = "app/{groupId}/{userId}";
        Map<String, String[]> params = Maps.hash("groupId", Arrays.gen("test")).map("userId", Arrays.gen("20120001"));
        params.put("q1", Arrays.gen("v1"));
        params.put("q2", Arrays.gen("v20", "v21", "v22"));
        String url = TagUtils.createUrl(path, params, page, true, true);
        assertThat(url, is("app\\/test\\/20120001?groupId=test&amp;q2=v20q2=v21q2=v22&amp;q1=v1&amp;userId=20120001"));
    }
}
