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

package jp.co.ctc_g.jse.core.amqp.initializer;

import java.util.Map;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Bean登録されているExchangeとQueueをRabbitMQに作成・削除します。
 * 起動時に登録済みのExchange/Queue/Bindingを宣言し、`deleted=true` の場合は停止時に削除します。
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class RabbitMQInitializer implements InitializingBean, DisposableBean {

    private static final Logger L = LoggerFactory.getLogger(RabbitMQInitializer.class);
    private static final ResourceBundle R = InternalMessages.getBundle(RabbitMQInitializer.class);

    @Autowired
    private AmqpAdmin admin;

    @Autowired
    private ApplicationContext context;

    private boolean deleted = false;

    /**
     * デフォルトコンストラクタです。
     */
    public RabbitMQInitializer() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Args.checkNotNull(admin, R.getObject("E-AMQP-INITIALIZER#0001"));
        L.debug(R.getString("D-AMQP-INITIALIZER#0001"));
        Map<String, Exchange> exchanges = context.getBeansOfType(Exchange.class);
        for (Map.Entry<String, Exchange> exchange : exchanges.entrySet()) {
            Exchange e = exchange.getValue();
            L.debug(Strings.substitute(R.getString("D-AMQP-INITIALIZER#0003"), Maps.hash("name", e.getName())));
            admin.declareExchange(e);
        }
        Map<String, Queue> queues = context.getBeansOfType(Queue.class);
        for (Map.Entry<String, Queue> queue : queues.entrySet()) {
            Queue q = queue.getValue();
            L.debug(Strings.substitute(R.getString("D-AMQP-INITIALIZER#0004"), Maps.hash("name", q.getName())));
            admin.declareQueue(q);
        }
        Map<String, Binding> bindings = context.getBeansOfType(Binding.class);
        for (Map.Entry<String, Binding> binding : bindings.entrySet()) {
            Binding b = binding.getValue();
            L.debug(Strings.substitute(R.getString("D-AMQP-INITIALIZER#0005"), 
                Maps.hash("ename", b.getExchange())
                    .map("qname", b.getDestination())
                    .map("key", b.getRoutingKey())));
            admin.declareBinding(b);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        if (deleted) {
            L.debug(R.getString("D-AMQP-INITIALIZER#0002"));
            Map<String, Exchange> exchanges = context.getBeansOfType(Exchange.class);
            for (Map.Entry<String, Exchange> exchange : exchanges.entrySet()) {
                Exchange e = exchange.getValue();
                L.debug(Strings.substitute(R.getString("D-AMQP-INITIALIZER#0006"), Maps.hash("name", e.getName())));
                admin.deleteExchange(e.getName());
            }
            Map<String, Queue> queues = context.getBeansOfType(Queue.class);
            for (Map.Entry<String, Queue> queue : queues.entrySet()) {
                Queue q = queue.getValue();
                L.debug(Strings.substitute(R.getString("D-AMQP-INITIALIZER#0007"), Maps.hash("name", q.getName())));
                admin.deleteQueue(q.getName());
            }
        } else {
            L.debug(R.getString("D-AMQP-INITIALIZER#0008"));
        }
    }

    /**
     * {@link AmqpAdmin} を設定します。
     * @param admin {@link AmqpAdmin}
     */
    public void setAdmin(AmqpAdmin admin) {
        this.admin = admin;
    }

    /**
     * {@link ApplicationContext} を設定します。
     * @param context {@link ApplicationContext}
     */
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 削除有効・無効 を設定します。
     * @param deleted true:削除有効、false:削除無効
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
