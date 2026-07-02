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

package jp.co.ctc_g.jse.core.amqp.retry;

import java.util.Map;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.amqp.exception.AbstractAmqpException;
import jp.co.ctc_g.jse.core.amqp.internal.AmqpInternals;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * このクラスは、{@link MessageRecoverer}を実装したクラスです。
 * </p>
 * <p>
 * このクラスは、メッセージ受信側で発生した例外情報をメッセージのヘッダに付与し、
 * RabbitMQの例外用のキューへ転送します。
 * </p>
 * <p>
 * メッセージの転送前にメッセージヘッダへ以下の情報を付与し、
 * 指定されたExchange/RoutingKeyで転送します。
 * このメッセージヘッダのキーは変更することが可能です。
 * 変更可能なキーとそのキーでどのような情報を設定するかは以下を参照してください。
 * <table>
 *  <thead>
 *   <tr>
 *    <th>プロパティキー</th>
 *    <th>概要</th>
 *    <th>メッセージヘッダキー</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *     <td>jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger.exception_id</td>
 *     <td>例外発生時に設定されるUIDを設定します。これは、{@link AbstractAmqpException}を継承した例外のみ有効になります。</td>
 *     <td>x-exception-id</td>
 *   </tr>
 *   <tr>
 *     <td>jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger.exception_message</td>
 *     <td>例外のメッセージを設定します。</td>
 *     <td>x-exception-message</td>
 *   </tr>
 *   <tr>
 *     <td>jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger.exception_stacktrace</td>
 *     <td>例外のスタックトレースを設定します。</td>
 *     <td>x-exception-stacktrace</td>
 *   </tr>
 *   <tr>
 *     <td>jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger.original_exchange</td>
 *     <td>メッセージ送信側が指定したExchangeを設定します。</td>
 *     <td>x-original-exchange</td>
 *   </tr>
 *   <tr>
 *     <td>jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger.original_routing_key</td>
 *     <td>メッセージ送信側が指定したRoutingKeyを設定します。</td>
 *     <td>x-original-exchange</td>
 *   </tr>
 * </table>
 * </p>
 * <p>
 * 転送するRoutingKeyはAMQPの基底クラス{@link AbstractAmqpException}を継承した例外であれば、
 * 例外個別に指定することができます。
 * 例外に応じて配送するQueueを変えたい場合に利用します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ExceptionMessageExchanger implements MessageRecoverer, InitializingBean {

    private static final Logger L = LoggerFactory.getLogger(ExceptionMessageExchanger.class);
    private static final ResourceBundle R = InternalMessages.getBundle(ExceptionMessageExchanger.class);

    private static final String HEADER_KEY_EXCEPTION_STACKTRACE;
    private static final String HEADER_KEY_EXCEPTION_MESSAGE;
    private static final String HEADER_KEY_EXCEPTION_ID;
    private static final String HEADER_KEY_ORIGINAL_EXCHANGE;
    private static final String HEADER_KEY_ORIGINAL_ROUTING_KEY;
    static {
        Config c = AmqpInternals.getConfig(ExceptionMessageExchanger.class);
        HEADER_KEY_EXCEPTION_STACKTRACE = c.find("exception_stacktrace");
        HEADER_KEY_EXCEPTION_MESSAGE = c.find("exception_message");
        HEADER_KEY_EXCEPTION_ID = c.find("exception_id");
        HEADER_KEY_ORIGINAL_EXCHANGE = c.find("original_exchange");
        HEADER_KEY_ORIGINAL_ROUTING_KEY = c.find("original_routing_key");
    }

    private AmqpTemplate amqpTemplate;

    private String exchange;

    private String defaultRoutingKey;

    /**
     * デフォルトコンストラクタです。
     */
    public ExceptionMessageExchanger() {}

    /**
     * メッセージヘッダに必要な情報を付与し、例外用のキューへメッセージを転送します。
     * @param message 例外が発生したメッセージ
     * @param throwable 発生した例外
     */
    @Override
    public void recover(Message message, Throwable throwable) {
        Args.checkNotNull(exchange, R.getObject("E-AMQP-RETRY#0004"));
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put(HEADER_KEY_EXCEPTION_STACKTRACE, ExceptionUtils.getStackTrace(throwable));
        headers.put(HEADER_KEY_EXCEPTION_MESSAGE, throwable.getMessage());
        headers.put(HEADER_KEY_ORIGINAL_EXCHANGE, message.getMessageProperties().getReceivedExchange());
        headers.put(HEADER_KEY_ORIGINAL_ROUTING_KEY, message.getMessageProperties().getReceivedRoutingKey());
        String rk = defaultRoutingKey;
        Throwable cause = throwable;
        if (throwable instanceof ListenerExecutionFailedException) {
            cause = throwable.getCause();
        }
        if (cause instanceof AbstractAmqpException) {
            headers.put(HEADER_KEY_EXCEPTION_ID, ((AbstractAmqpException) cause).getId());
            rk = ((AbstractAmqpException) cause).getRoutingKey();
        }
        amqpTemplate.send(exchange, rk, message);
        logging(exchange, rk, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (amqpTemplate == null) {
            throw new IllegalArgumentException(R.getString("E-AMQP-RETRY#0002"));
        }
        if (Strings.isEmpty(exchange)) {
            throw new IllegalArgumentException(R.getString("E-AMQP-RETRY#0004"));
        }
        if (Strings.isEmpty(defaultRoutingKey)) {
            throw new IllegalArgumentException(R.getString("E-AMQP-RETRY#0005"));
        }
    }

    /**
     * AMQPテンプレートを設定します。
     * @param amqpTemplate AMQPテンプレート
     */
    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * 例外用のExchangeを設定します。
     * @param exchange 例外用のExchange
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * 例外転送時のRoutingKeyを設定します。
     * @param defaultRoutingKey 例外転送時のRoutingKey
     */
    public void setDefaultRoutingKey(String defaultRoutingKey) {
        this.defaultRoutingKey = defaultRoutingKey;
    }

    /**
     * 例外が発生した情報をログに出力します。
     * @param ex 転送先のExchangeキー
     * @param rk 転送先のRoutingキー
     * @param message 例外が発生した対象のメッセージ
     */
    protected void logging(String ex, String rk, Message message) {
        L.info(Strings.substitute(R.getString("I-AMQP-RETRY#0001"), 
            Maps.hash("exchange", message.getMessageProperties().getReceivedExchange())
                .map("routingKey", message.getMessageProperties().getReceivedRoutingKey())
                .map("messageId", message.getMessageProperties().getMessageId())
                .map("errorExchange", ex)
                .map("errorRoutingKey", rk)));
    }

}
