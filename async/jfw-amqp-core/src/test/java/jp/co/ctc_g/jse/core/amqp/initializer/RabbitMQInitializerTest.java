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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jfw.core.internal.InternalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class RabbitMQInitializerTest {
    
    public static class RabbitMQInitializerTestContext {

        @Bean
        public Exchange exchange() {
            return new DirectExchange("etest");
        }

        @Bean
        public Queue queue() {
            return new Queue("qtest");
        }
        @Bean
        public Binding binding() {
            return BindingBuilder.bind(queue())
                .to(exchange())
                .with("test.routing.key")
                .noargs();
        }
        
        @Bean
        public ConnectionFactory factory() {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory("127.0.0.1");
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
            return connectionFactory;
        }
        
        @Bean
        public AmqpAdmin amqpAdmin() {
            RabbitAdmin rabbitAdmin = new RabbitAdmin(factory());
            rabbitAdmin.setAutoStartup(true);
            return rabbitAdmin;
        }

    }

    private RabbitMQInitializer initializer;
    private AnnotationConfigApplicationContext context;
    private AmqpAdmin admin;
    
    @BeforeEach
    public void setup() {
        context = new AnnotationConfigApplicationContext(RabbitMQInitializerTestContext.class);
        admin = context.getBean(AmqpAdmin.class);
        initializer = new RabbitMQInitializer();
        initializer.setAdmin(admin);
        initializer.setContext(context);
        initializer.setDeleted(true);
    }

    @Test
    public void ExchangeとQueueが作成される() throws Exception {
        initializer.destroy();
        initializer.afterPropertiesSet();
        assertThat(admin.getQueueProperties("qtest"), is(notNullValue()));
    }
    
    @Test
    public void ExchagenとQueueが削除される() throws Exception {
        initializer.afterPropertiesSet();
        initializer.destroy();
        assertThat(admin.getQueueProperties("qtest"), is(nullValue()));
    }
    
    @Test
    public void AmqpAdminのインスタンスが設定されていない場合は例外が発生する() throws Exception {
        initializer.setAdmin(null);
        InternalException ex = assertThrows(InternalException.class, () -> initializer.afterPropertiesSet());
        assertThat(ex.getMessage(), containsString("AmqpAdminのインスタンスが設定されていません。"));
    }

}
