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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Maps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

public class ViewIdTest {

    private MockHttpServletRequest request;

    @BeforeEach
    public void setup() throws Exception {
        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
    }

    @Test
    public void 画面IDを定義する() {
        final String ID = "VID#001";
        ViewId vid = new ViewId(ID);
        assertEquals(ID, vid.getId());
    }

    @Test
    public void 画面IDの指定が空文字の場合例外が発生する() {
        assertThrows(InternalException.class, () -> {
            final String ID = "";
            new ViewId(ID);
        });
    }

    @Test
    public void 単一パラメータ付きの画面IDを定義する() {
        ViewId vid = new ViewId("VID#001");
        vid.setParams(new HashMap<String, String[]>(Maps.hash("key", new String[] {"value"})));
        String actual = vid.getQuery();
        assertEquals("?key=value", actual);
    }

    @Test
    public void 複数パラメータgetQueryテスト() {
        ViewId vid = new ViewId("test");
        // 順序保証が必要なのでLinkedHashMap
        HashMap<String, String[]> params = new LinkedHashMap<String, String[]>();
        params.put("key1", new String[] {"value1"});
        params.put("key2", new String[] {"value2"});
        vid.setParams(params);
        String actual = vid.getQuery();
        assertEquals("?key1=value1&key2=value2", actual);
    }
    
    @Test
    public void 同名複数パラメータgetQueryテスト() {
        ViewId vid = new ViewId("test");
        // 順序保証が必要なのでLinkedHashMap
        Map<String, String[]> params = new LinkedHashMap<String, String[]>();
        params.put("key", new String[] {"value1", "value2"});
        vid.setParams(new HashMap<String, String[]>(params));
        String actual = vid.getQuery();
        assertEquals("?key=value1&key=value2", actual);
    }
    
    @Test
    public void 画面IDを削除する() {
        ViewId vid1 = new ViewId("VID#001");
        ViewId.is(vid1, request);
        ViewId vid2 = new ViewId("VID#002");
        ViewId.is(vid2, request);
        ViewId vid3 = new ViewId("VID#003");
        ViewId.is(vid3, request);
        ViewId.clear(request, "VID#002");
        assertThat(ViewId.size(request), is(2));
    }
}
