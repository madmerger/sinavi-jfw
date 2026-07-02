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
import org.springframework.amqp.support.converter.JsonMessageConverter;
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
 * RabbitMQのコネクションファクトリとRabbitTemplateを設定します。
 * 有効化方法はコンポーネントスキャン、JavaConfig の {@code @Import}、XML の bean 定義です。
 * 値のみを変更する場合はプロパティ値で上書きします。
 * 主なプロパティは {@code rabbitmq.host}、{@code rabbitmq.username}、{@code rabbitmq.password}、
 * {@code rabbitmq.channel-cache-size}、{@code rabbitmq.retry.count}、{@code rabbitmq.retry.back.off.period} です。
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
     * @return {@link JsonMessageConverter}のインスタンス
     */
    @Bean
    public MessageConverter converter() {
        JsonMessageConverter converter = new JsonMessageConverter();
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
