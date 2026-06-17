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

public class ViewIdOutTagTest {

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

        ViewIdOutTag tag = new ViewIdOutTag();
        assertThat(tag.getProperty(), is(""));
        assertThat(tag.getHistory(), is(0));
        assertThat(tag.isEscapeRequired(), is(true));
        assertThat(tag.getScope(), is(nullValue()));
    }

    @Test
    public void Requestスコープを指定したときに例外が発生するかどうか() throws Exception {
        assertThrows(InternalException.class, () -> {
    
            ViewIdOutTag tag = new ViewIdOutTag();
            tag.setScope("request");
        });
    }

    @Test
    public void SessionスコープのIDが出力されるかどうか() throws Exception {

        ViewId vid = new ViewId("VID#0001");
        ViewId.is(vid, req);
        ViewIdOutTag tag = new ViewIdOutTag();
        tag.setJspContext(context);
        tag.setProperty("id");
        tag.setEscapeRequired(true);
        tag.setScope("session");
        tag.doTag();
        assertThat(res.getContentAsString(), is("VID#0001"));
    }

    @Test
    public void プロパティが指定されない場合は何も出力されないかどうか() throws Exception {

        ViewId vid = new ViewId("VID#0001");
        ViewId.is(vid, req);
        ViewIdOutTag tag = new ViewIdOutTag();
        tag.setJspContext(context);
        tag.setProperty("");
        tag.setEscapeRequired(true);
        tag.setScope("session");
        tag.doTag();
        assertThat(res.getContentAsString(), is(""));
    }

}
