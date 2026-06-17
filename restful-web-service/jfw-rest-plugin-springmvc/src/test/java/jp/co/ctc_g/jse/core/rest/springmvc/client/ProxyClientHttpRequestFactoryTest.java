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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ProxyClientHttpRequestFactoryTest {

    @Nested
    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(locations = "classpath:/jp/co/ctc_g/jse/core/rest/springmvc/client/ProxySetting-Context.xml")
    class コンフィグレーションテスト {

        @Autowired
        @Qualifier("proxy")
        private ProxyClientHttpRequestFactory factory;

        @Test
        public void プロキシホストが設定されている() {
            assertThat(factory, is(notNullValue()));
        }

    }

    @Nested
    class コンフィグレーションエラーテスト {
        protected ClassPathXmlApplicationContext context;
        
        @AfterEach
        public void teardown() {
            if (context != null) context.close();
        }

        @Test
        public void プロキシホストが指定されていない場合はエラーが発生する() {
            Exception ex = assertThrows(Exception.class, () ->
                context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotProxyHostSetting-Context.xml"));
            assertInstanceOf(IllegalArgumentException.class, ex.getCause());
            assertThat(ex.getMessage(), containsString("プロキシホスト(proxyHost)は必須です。"));
        }
        
        @Test
        public void プロキシポート番号が指定されていない場合はエラーが発生する() {
            Exception ex = assertThrows(Exception.class, () ->
                context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotProxyPortSetting-Context.xml"));
            assertInstanceOf(IllegalArgumentException.class, ex.getCause());
            assertThat(ex.getMessage(), containsString("プロキシポート番号(proxyPort)は必須です。"));
        }

        @Test
        public void ユーザ認証が指定されていてかつユーザ名が指定されていない場合はエラーが発生する() {
            Exception ex = assertThrows(Exception.class, () ->
                context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotUsernameSetting-Context.xml"));
            assertInstanceOf(IllegalArgumentException.class, ex.getCause());
            assertThat(ex.getMessage(), containsString("ユーザ認証がtrueに設定された場合、ユーザ名(username)は必須です。"));
        }

        @Test
        public void ユーザ認証が指定されていてかつパスワードが指定されていない場合はエラーが発生する() {
            Exception ex = assertThrows(Exception.class, () ->
                context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotPasswordSetting-Context.xml"));
            assertInstanceOf(IllegalArgumentException.class, ex.getCause());
            assertThat(ex.getMessage(), containsString("ユーザ認証がtrueに設定された場合、パスワード(password)は必須です。"));
        }

    }

}
