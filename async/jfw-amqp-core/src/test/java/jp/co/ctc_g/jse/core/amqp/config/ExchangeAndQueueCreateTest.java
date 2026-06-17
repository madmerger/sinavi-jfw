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

import java.util.Properties;

import jp.co.ctc_g.jse.core.amqp.TestQueueCleaner;
import jp.co.ctc_g.jse.core.amqp.config.exception.DefaultProperties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DefaultProperties.class)
@ActiveProfiles("development")
public class ExchangeAndQueueCreateTest {

    @Autowired
    private RabbitAdmin admin;
    @Autowired
    private ConnectionFactory connection;
    @Autowired
    private ExceptionQueueContextConfig config;

    @BeforeEach
    public void ping() {
        // Ping的にコネクションのみはることでExchange/Queueを作成
        Connection con = connection.createConnection();
        con.createChannel(false);
    }

    @AfterAll
    public static void cleaner() {
        TestQueueCleaner.cleaner();
    }
    
    @Test
    public void デフォルトのQueueが作成される() {
        Properties recover = admin.getQueueProperties(config.recoverableExceptionQueue);
        assertThat(recover, is(notNullValue()));
        assertThat(recover.getProperty("QUEUE_NAME"), is(notNullValue()));
        Properties unrecover = admin.getQueueProperties(config.unrecoverableExceptionQueue);
        assertThat(unrecover, is(notNullValue()));
        assertThat(unrecover.getProperty("QUEUE_NAME"), is(notNullValue()));
    }

}