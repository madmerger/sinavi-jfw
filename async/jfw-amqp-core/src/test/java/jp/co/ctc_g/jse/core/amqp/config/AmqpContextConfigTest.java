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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import jp.co.ctc_g.jse.core.amqp.config.amqp.DefaultProperties;
import jp.co.ctc_g.jse.core.amqp.config.amqp.OverrideProperties;

import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// @Nested classes used instead of Enclosed
public class AmqpContextConfigTest {

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = DefaultProperties.class)
    public static class AmqpContextConfigLoadTest {

        @Autowired
        private ApplicationContext context;

        @Autowired
        private AmqpContextConfig config;

        @Test
        public void デフォルト値が設定されて指定のインスタンスがDIコンテナに登録される() {
            assertThat(config.host, is("127.0.0.1"));
            assertThat(config.username, is("guest"));
            assertThat(config.password, is("guest"));
            assertThat(config.channelCacheSize, is(10));
            assertThat(context.getBean(ConnectionFactory.class), is(notNullValue()));
            assertThat(context.getBean(RabbitTemplate.class), is(notNullValue()));
            assertThat(context.getBean(MessageConverter.class), is(notNullValue()));
        }

    }
    
    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = OverrideProperties.class)
    public static class AmqpContextConfigOverridePropertiesLoadTest {
        @Autowired
        private ApplicationContext context;

        @Autowired
        private AmqpContextConfig config;

        @Test
        public void 値がオーバライドされて指定のインスタンスがDIコンテナに登録される() {
            assertThat(config.host, is("192.168.10.10"));
            assertThat(config.username, is("jfw"));
            assertThat(config.password, is("jfw"));
            assertThat(config.channelCacheSize, is(100));
            assertThat(context.getBean(ConnectionFactory.class), is(notNullValue()));
            assertThat(context.getBean(RabbitTemplate.class), is(notNullValue()));
            assertThat(context.getBean(MessageConverter.class), is(notNullValue()));
        }
    }

}
