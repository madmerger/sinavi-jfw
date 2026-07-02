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

package jp.co.ctc_g.jse.core.amqp.config;

import java.util.Collections;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.SleepingBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * <p>
 * このクラスは、RabbitMQのコネクションファクトリの設定やRabbitTemplateのインスタンス生成などのAMQPに共通して必要な設定を提供します。
 * </p>
 * <p>
 * この設定を有効にする場合は、以下の3通りの方法があります。
 * <ol>
 *   <li>このクラスをコンポーネントスキャンの対象に含める方法</li>
 *   <li>JavaConfigにインポートする方法</li>
 *   <ul>
 *     <div>
 *       <pre class="brush:java">
 *       &#064;Configuration
 *       &#064;Import(AmqpContextConfig.class)
 *       public class AppContextConfig {
 *         // Appのコンフィグ定義
 *       }
 *       </pre>
 *     </div>
 *   </ul>
 *   <li>XMLにインポートする方法</li>
 *   <ul>
 *     <div>
 *       <pre class="brush:java">
 *       &lt;beans&gt;
 *         &lt;bean class="jp.co.ctc_g.jse.core.amqp.config.AmqpContextConfig" /&gt;
 *       &lt;/beans&gt;
 *       </pre>
 *     </div>
 *   </ul>
 * </ol>
 * </p>
 * <p>
 * また、設定のオーバライドは
 * 大幅に挙動を変更する場合はJavaの継承を利用し、
 * 値のみを変更する場合はプロパティ値による指定で
 * オーバライドしてください。
 * なお、デフォルト値を有効にする場合でも以下の設定と
 * 空のプロパティファイルの設置が必要です。
 * プロパティファイルを指定する場合はSpringの{@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer}を利用します。
 * 以下にXMLとJavaConfigのプロパティの指定例を示します。
 * <pre class="brush:java">
 * &lt;context:property-placeholder location="classpath:RabbitMQ.properties" 
 *   ignore-unresolvable="true"
 *   ignore-resource-not-found="true" /&gt;
 * </pre>
 * もしくは
 * <pre class="brush:java">
 * ※この設定はアプリケーション1つに対して1つとなります。
 * 例えば、Jdbc.propertiesも読み込みたい場合はPropertySourceの値にカンマ区切りで複数のプロパティを指定してください。
 * &#064;PropertySource("classpath:/RabbitMQ.properties")
 * public class PropertyPlacehodler {
 *     public PropertyPlacehodler() {}
 *     &#064;Bean
 *     public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
 *         PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
 *         configurer.setIgnoreResourceNotFound(true);
 *         configurer.setIgnoreUnresolvablePlaceholders(true);
 *         return configurer;
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * プロパティ値による指定でオーバライド可能な値は以下の通りです。
 * </p>
 * <table>
 *  <thead>
 *   <tr>
 *    <th>キー</th>
 *    <th>概要</th>
 *    <th>デフォルト値</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>rabbitmq.host</td>
 *    <td>RabbitMQの接続ホストを指定します。カンマで複数設定することも可能です。</td>
 *    <td>127.0.0.1</td>
 *   </tr>
 *   <tr>
 *    <td>rabbitmq.username</td>
 *    <td>接続するためのユーザ名を指定します。</td>
 *    <td>guest</td>
 *   </tr>
 *   <tr>
 *    <td>rabbitmq.password</td>
 *    <td>接続するためのパスワードを指定します。</td>
 *    <td>guest</td>
 *   </tr>
 *   <tr>
 *    <td>rabbitmq.channel-cache-size</td>
 *    <td>接続チャネルのキャッシュサイズを指定します。</td>
 *    <td>10</td>
 *   </tr>
 *   <tr>
 *    <td>rabbitmq.retry.count</td>
 *    <td>リトライの最大回数を指定します。</td>
 *    <td>10</td>
 *   </tr>
 *   <tr>
 *    <td>rabbitmq.retry.back.off.period</td>
 *    <td>リトライを実行する間隔を指定します。</td>
 *    <td>1000</td>
 *   </tr>
 *  </tbody>
 * </table>
 * @author ITOCHU Techno-Solutions Corporation.
 */
@Configuration
public class AmqpContextConfig {

    /**
     * 接続するRabbitMQのホスト名
     * 
     * デフォルト：127.0.0.1
     */
    @Value("${rabbitmq.host:127.0.0.1}")
    protected String host;

    /**
     * RabbitMQに接続するためのユーザ名
     * 
     * デフォルト：guest
     */
    @Value("${rabbitmq.username:guest}")
    protected String username;

    /**
     * RabbitMQに接続するためのパスワード
     * 
     * デフォルト：guest
     */
    @Value("${rabbitmq.password:guest}")
    protected String password;

    /**
     * 接続チャネルのキャッシュサイズ
     * 
     * デフォルト:10
     */
    @Value("${rabbitmq.channel-cache-size:10}")
    protected int channelCacheSize;

    /**
     * リトライの最大実行回数
     * 
     * デフォルト:10
     */
    @Value("${rabbitmq.retry.count:10}")
    protected int retryCount;
    
    /**
     * リトライ間隔
     * 
     * デフォルト:1000L(ミリ秒)
     */
    @Value("${rabbitmq.retry.back.off.period:1000}")
    protected long backOffPeriod;

    /**
     * デフォルトコンストラクタです。
     */
    public AmqpContextConfig() {}

    /**
     * {@link ConnectionFactory}のインスタンスをDIコンテナに登録します。
     * @return {@link CachingConnectionFactory}のインスタンス
     */
    @Bean
    public ConnectionFactory factory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setChannelCacheSize(channelCacheSize);
        configure(connectionFactory);
        return connectionFactory;
    }

    /**
     * {@link RabbitTemplate}のインスタンスをDIコンテナに登録します。
     * @return {@link RabbitTemplate}のインスタンス
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(factory());
        template.setMessageConverter(converter());
        template.setRetryTemplate(retryTemplate());
        configure(template);
        return template;
    }

    /**
     * {@link MessageConverter}のインスタンスをDIコンテナに登録します。
     * @return {@link Jackson2JsonMessageConverter}のインスタンス
     */
    @Bean
    public MessageConverter converter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }

    /**
     * {@link RetryTemplate}のインスタンスをDIコンテナに登録します。
     * @return {@link RetryTemplate}のインスタンス
     */
    protected RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(backOffPolicy());
        template.setRetryPolicy(simpleRetryPolicy());
        return template;
    }

    /**
     * リトライ間隔を設定し、{@link SleepingBackOffPolicy}のインスタンスを生成します。
     * @return {@link FixedBackOffPolicy}のインスタンス
     */
    protected SleepingBackOffPolicy<FixedBackOffPolicy> backOffPolicy() {
        FixedBackOffPolicy policy = new FixedBackOffPolicy();
        policy.setBackOffPeriod(backOffPeriod);
        return policy;
    }

    /**
     * リトライの対象となる例外とリトライ回数を指定し、
     * {@link RetryPolicy}のインスタンスを生成します。
     * @return {@link SimpleRetryPolicy}のインスタンス
     */
    protected RetryPolicy simpleRetryPolicy() {
        return new SimpleRetryPolicy(retryCount, Collections.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true));
    }

    /**
     * {@link CachingConnectionFactory}の設定を変更する拡張ポイントです。
     * このメソッドが実行される前に接続情報が設定されます。
     * @param factory {@link CachingConnectionFactory}のインスタンス
     */
    protected void configure(CachingConnectionFactory factory) {}

    /**
     * {@link RabbitTemplate}の設定を変更する拡張ポイントです。
     * このメソッドが実行される前には{@link ConnectionFactory}の設定と
     * {@link MessageConverter}が設定されます。
     * @param template {@link RabbitTemplate}のインスタンス
     */
    protected void configure(RabbitTemplate template) {}

}
