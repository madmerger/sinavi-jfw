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

package jp.co.ctc_g.jse.core.rest.springmvc.client;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.CoreMatchers;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import jp.co.ctc_g.jfw.core.internal.InternalException;

// hamcrest removed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// ExpectedException removed - use assertThrows;

public class TargetTest {

    public static final String URI_TEMPLATE = "http://127.0.0.1/test";
    protected EntityTestBean target;

    @BeforeEach
    public void setup() {
        target = new EntityTestBean("test");
    }

    @Test
    public void URIにNULL値が指定されたときは例外が発生する() {
        InternalException ex = assertThrows(InternalException.class, () -> Target.target(null));
        assertThat(ex.getMessage(), is("URLは必須です。"));
    }

    @Test
    public void URIがNULLかつマップ変数が指定されていたときは例外が発生する() {
        InternalException ex = assertThrows(InternalException.class, () -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", 1);
            Target.target(null, map);
        });
        assertThat(ex.getMessage(), is("URLは必須です。"));
    }

    @Test
    public void 設定したURIが取得できる() {
        Target t = Target.target(URI_TEMPLATE);
        assertThat(t.getUrl().toString(), CoreMatchers.is(URI_TEMPLATE));
    }

    @Test
    public void パスが置換されたURIが取得できる() {
        Target t = Target.target(URI_TEMPLATE + "/{id}", 1);
        assertThat(t.getUrl().toString(), CoreMatchers.is(URI_TEMPLATE + "/1"));
    }

    @Test
    public void マップ変数を指定してパスが置換されたURIが取得できる() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        Target t = Target.target(URI_TEMPLATE + "/{id}", map);
        assertThat(t.getUrl().toString(), CoreMatchers.is(URI_TEMPLATE + "/1"));
    }

    public class EntityTestBean {

        private String id;

        public EntityTestBean(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
