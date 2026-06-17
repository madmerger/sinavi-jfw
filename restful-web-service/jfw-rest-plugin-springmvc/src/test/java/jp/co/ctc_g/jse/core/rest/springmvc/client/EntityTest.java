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

import java.nio.charset.Charset;

// hamcrest removed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// ExpectedException removed - use assertThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

public class EntityTest {
    protected EntityTestBean target;

    @BeforeEach
    public void setup() {
        target = new EntityTestBean("z1111111");
    }

    @Test
    public void メディアタイプが設定されずにacceptを実行した場合は例外が発生する() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            Entity.entity(target, null)
                    .accept();
        });
        assertThat(ex.getMessage(), is("メディアタイプは必須です。"));
    }

    @Test
    public void JSONのContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void XMLのContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.xml(target).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_XML));
    }

    @Test
    public void HTMLのContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.html(target).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.TEXT_HTML));
    }

    @Test
    public void TEXTのContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.text(target).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.TEXT_PLAIN));
    }

    @Test
    public void OCTETのContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.octet(target).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    public void 任意のContentTypeがHttpHeaderに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.entity(target, MediaType.APPLICATION_ATOM_XML).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_ATOM_XML));
    }

    @Test
    public void デフォルトのメディアタイプでHttpHeaderのacceptに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).accept().get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAccept().size(), CoreMatchers.is(1));
        assertThat(entity.getHeaders().getAccept().get(0), CoreMatchers.is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void 指定したメディアタイプでHttpHeaderのacceptに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).accept(MediaType.APPLICATION_XML).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAccept().size(), CoreMatchers.is(1));
        assertThat(entity.getHeaders().getAccept().get(0), CoreMatchers.is(MediaType.APPLICATION_XML));
    }

    @Test
    public void 複数指定したメディアタイプでHttpHeaderのacceptに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAccept().size(), CoreMatchers.is(2));
        assertThat(entity.getHeaders().getAccept().get(0), CoreMatchers.is(MediaType.APPLICATION_XML));
        assertThat(entity.getHeaders().getAccept().get(1), CoreMatchers.is(MediaType.APPLICATION_JSON));
    }

    @Test
    public void デフォルトのCharsetでHttpHeaderのacceptCharsetに設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).acceptCharset().get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAcceptCharset().size(), CoreMatchers.is(1));
        assertThat(entity.getHeaders().getAcceptCharset().get(0), CoreMatchers.is(Charset.defaultCharset()));
    }

    @Test
    public void 指定したCharsetでHttpHeaderのacceptCharsetに設定されている() {
        Charset utf8 = Charset.forName("UTF-8");
        HttpEntity<EntityTestBean> entity = Entity.json(target).acceptCharset(utf8).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAcceptCharset().size(), CoreMatchers.is(1));
        assertThat(entity.getHeaders().getAcceptCharset().get(0), CoreMatchers.is(utf8));
    }

    @Test
    public void 複数指定したCharsetでHttpHeaderのacceptCharsetに設定されている() {
        Charset utf8 = Charset.forName("UTF-8");
        Charset sjis = Charset.forName("Shift_JIS");
        HttpEntity<EntityTestBean> entity = Entity.json(target).acceptCharset(utf8, sjis).get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(MediaType.APPLICATION_JSON));
        assertThat(entity.getHeaders().getAcceptCharset().size(), CoreMatchers.is(2));
        assertThat(entity.getHeaders().getAcceptCharset().get(0), CoreMatchers.is(utf8));
        assertThat(entity.getHeaders().getAcceptCharset().get(1), CoreMatchers.is(sjis));
    }

    @Test
    public void 任意のヘッダが設定されている() {
        HttpEntity<EntityTestBean> entity = Entity.json(target).add("x-auth", "aaaaa").get();
        assertThat(entity, CoreMatchers.notNullValue());
        assertThat(entity.getHeaders().get("x-auth").size(), CoreMatchers.is(1));
        assertThat(entity.getHeaders().get("x-auth").get(0), CoreMatchers.is("aaaaa"));
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
