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

import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(Enclosed.class)
public class ProxyClientHttpRequestFactoryTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = "classpath:/jp/co/ctc_g/jse/core/rest/springmvc/client/ProxySetting-Context.xml")
    public static class コンフィグレーションテスト {

        @Autowired
        @Qualifier("default")
        protected ProxyClientHttpRequestFactory defaultFactory;

        @Autowired
        @Qualifier("override")
        protected ProxyClientHttpRequestFactory overrideFactory;

        @Autowired
        @Qualifier("proxy")
        protected ProxyClientHttpRequestFactory proxyFactory;

        @Test
        public void デフォルト値でHttpClientが生成される() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
            HttpClient client = defaultFactory.getHttpClient();
            assertThat(client, CoreMatchers.notNullValue());
            RequestConfig config = getRequestConfig(client);
            assertThat(config.getSocketTimeout(), CoreMatchers.is(60000));
            PoolingHttpClientConnectionManager manager = getConnectionManager(client);
            assertThat(manager.getMaxTotal(), CoreMatchers.is(100));
            assertThat(manager.getDefaultMaxPerRoute(), CoreMatchers.is(5));
        }

        @Test
        public void オーバライドした設定値でHttpClientが生成される() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
            HttpClient client = overrideFactory.getHttpClient();
            assertThat(client, CoreMatchers.notNullValue());
            RequestConfig config = getRequestConfig(client);
            assertThat(config.getSocketTimeout(), CoreMatchers.is(120000));
            PoolingHttpClientConnectionManager manager = getConnectionManager(client);
            assertThat(manager.getMaxTotal(), CoreMatchers.is(200));
            assertThat(manager.getDefaultMaxPerRoute(), CoreMatchers.is(10));
        }

        @Test
        public void プロキシが設定されたHttpClientが生成される() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
            HttpClient client = proxyFactory.getHttpClient();
            assertThat(client, CoreMatchers.notNullValue());
            CredentialsProvider provider = getCredentialsProvider(client);
            assertThat(provider, CoreMatchers.notNullValue());
            Credentials credentials = provider.getCredentials(new AuthScope("ctcpro.ctc-g.co.jp", 8080));
            assertThat(credentials, CoreMatchers.notNullValue());
            assertThat(credentials.getUserPrincipal().getName(), CoreMatchers.is("z1111111"));
            assertThat(credentials.getPassword(), CoreMatchers.is("P@ssword!"));
        }
        
        private RequestConfig getRequestConfig(HttpClient client) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
            Field f = client.getClass().getDeclaredField("defaultConfig");
            f.setAccessible(true);
            RequestConfig config = (RequestConfig) f.get(client);
            return config;
        }
        
        private PoolingHttpClientConnectionManager getConnectionManager(HttpClient client)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
            Field f = client.getClass().getDeclaredField("connManager");
            f.setAccessible(true);
            PoolingHttpClientConnectionManager manager = (PoolingHttpClientConnectionManager) f.get(client);
            return manager;
        }
        
        private CredentialsProvider getCredentialsProvider(HttpClient client) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
            Field f = client.getClass().getDeclaredField("credentialsProvider");
            f.setAccessible(true);
            CredentialsProvider provider = (CredentialsProvider) f.get(client);
            return provider;
        }
    }

    
    public static class コンフィグレーションエラーテスト {

        @Rule
        public ExpectedException thrown = ExpectedException.none();
        
        protected ClassPathXmlApplicationContext context;
        
        @After
        public void teardown() {
            if (context != null) context.close();
        }

        @Test
        public void プロキシホストが指定されていない場合はエラーが発生する() {
            thrown.expectCause(CoreMatchers.isA(IllegalArgumentException.class));
            thrown.expectMessage("プロキシホスト(proxyHost)は必須です。");
            context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotProxyHostSetting-Context.xml");
        }
        
        @Test
        public void プロキシポート番号が指定されていない場合はエラーが発生する() {
            thrown.expectCause(CoreMatchers.isA(IllegalArgumentException.class));
            thrown.expectMessage("プロキシポート番号(proxyPort)は必須です。");
            context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotProxyPortSetting-Context.xml");
        }

        @Test
        public void ユーザ認証が指定されていてかつユーザ名が指定されていない場合はエラーが発生する() {
            thrown.expectCause(CoreMatchers.isA(IllegalArgumentException.class));
            thrown.expectMessage("ユーザ認証がtrueに設定された場合、ユーザ名(username)は必須です。");
            context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotUsernameSetting-Context.xml");
        }

        @Test
        public void ユーザ認証が指定されていてかつパスワードが指定されていない場合はエラーが発生する() {
            thrown.expectCause(CoreMatchers.isA(IllegalArgumentException.class));
            thrown.expectMessage("ユーザ認証がtrueに設定された場合、パスワード(password)は必須です。");
            context = new ClassPathXmlApplicationContext("/jp/co/ctc_g/jse/core/rest/springmvc/client/NotPasswordSetting-Context.xml");
        }

    }

}
