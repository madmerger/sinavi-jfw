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

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * <p>
 * このクラスは、{@link org.springframework.web.client.RestOperations}で外部システムとHTTP通信を行う場合にプロキシサーバを経由する設定を行います。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ProxyClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory implements InitializingBean {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    private int maxTotal = DEFAULT_MAX_TOTAL_CONNECTIONS;

    private int defaultMaxPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

    private int readTimeout = DEFAULT_READ_TIMEOUT_MILLISECONDS;

    private String proxyHost;

    private String proxyPort;

    private boolean authentication = false;

    private String username;

    private String password;

    /**
     * コンストラクタです。
     */
    public ProxyClientHttpRequestFactory() {}

    /**
     * プロパティの設定が終了したあとにHttpClientのインスタンスを生成し、プロキシの設定を行います。
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(proxyHost, "プロキシホスト(proxyHost)は必須です。");
        Assert.notNull(proxyPort, "プロキシポート番号(proxyPort)は必須です。");

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        HttpClientBuilder builder = HttpClients.custom();
        builder.setConnectionManager(connectionManager);
        if (authentication) {
            Assert.notNull(username, "ユーザ認証がtrueに設定された場合、ユーザ名(username)は必須です。");
            Assert.notNull(password, "ユーザ認証がtrueに設定された場合、パスワード(password)は必須です。");
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            builder.setRoutePlanner(routePlanner);
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxyHost, Integer.parseInt(proxyPort)),
                    new UsernamePasswordCredentials(username, password.toCharArray()));
            builder.setDefaultCredentialsProvider(credsProvider);
        }
        builder.setDefaultRequestConfig(RequestConfig.custom()
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeout))
                .build());
        CloseableHttpClient client = builder.build();
        setHttpClient(client);
    }

    /**
     * プロキシホストを設定します。
     * @param proxyHost プロキシホスト
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * プロキシのポート番号を設定します。
     * @param proxyPort ポート番号
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * ユーザ認証が必要かどうかを設定します。
     * @param authentication ユーザ認証が必要な場合はtrue/必要ない場合はfalse
     */
    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    /**
     * ユーザ名を設定します。
     * @param username ユーザ名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * パスワードを設定します。
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 最大コネクション数を設定します。
     * @param maxTotal 最大コネクション数
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    /**
     * ルートごとのデフォルトの最大値を設定します。
     * @param defaultMaxPerRoute ルートごとのデフォルト最大値
     */
    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    /**
     * 読込のタイムアウトを設定します。
     * @param readTimeout コネクションのタイムアウト値
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
}
