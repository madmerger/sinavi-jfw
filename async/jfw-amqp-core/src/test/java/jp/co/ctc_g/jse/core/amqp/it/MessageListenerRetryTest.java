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

package jp.co.ctc_g.jse.core.amqp.it;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.co.ctc_g.jse.core.amqp.TestQueueCleaner;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationRecoverableException;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationUnrecoverableException;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpSystemException;

import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ErrorHandler;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RetryTestConsumerContextConfig.class)
@ActiveProfiles("development")
public class MessageListenerRetryTest {
    
    private CountDownLatch retry;
    private CountDownLatch recover;
    private CountDownLatch unrecover;
    private CountDownLatch unretry;

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private RabbitTemplate template;

    private SimpleMessageListenerContainer container;
    private SimpleMessageListenerContainer recoverableContainer;
    private SimpleMessageListenerContainer unrecoverableContainer;
    
    @BeforeEach
    public void setup() {
        init();
        container = container();
        recoverableContainer = container();
        unrecoverableContainer = container();
    }
    
    public void init() {
        retry = new CountDownLatch(10);
        recover = new CountDownLatch(1);
        unrecover = new CountDownLatch(1);
        unretry = new CountDownLatch(1);
    }
    
    @AfterAll
    public static void cleaner() {
        TestQueueCleaner.retryCleaner();
    }

    private SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer c = new SimpleMessageListenerContainer(ctx.getBean(ConnectionFactory.class));
        c.setConnectionFactory(ctx.getBean(ConnectionFactory.class));
        c.setErrorHandler(ctx.getBean(ErrorHandler.class));
        c.setAdviceChain(ctx.getBean(Advice[].class));
        return c;
    }

    @Test
    public void 回復可能例外の場合はデフォルトの指定回数リトライが実行されて回復可能例外キューに配信される() throws Exception {
        container.setQueues(ctx.getBean("retryTestQueue", Queue.class));
        container.setMessageListener(new MessageListenerAdapter(new ApplicationRecoverableExceptionTestHandler(), ctx.getBean(MessageConverter.class)));
        recoverableContainer.setQueues(ctx.getBean("recoverableExceptionQueue", Queue.class));
        recoverableContainer.setMessageListener(new MessageListenerAdapter(new RecoverableTestHandler(), ctx.getBean(MessageConverter.class)));
        container.start();
        recoverableContainer.start();
        template.convertAndSend("retry.test.exchange", "retry.test.binding", new RetryTestBean("test"));
        assertThat(retry.await(30, TimeUnit.SECONDS), is(true));
        assertThat(retry.getCount(), is(0L));
        assertThat(recover.await(3, TimeUnit.SECONDS), is(true));
        assertThat(recover.getCount(), is(0L));
        container.stop();
        recoverableContainer.stop();
    }

    @Test
    public void アプリケーション回復不能例外の場合はリトライが実行されない() throws Exception {
        container.setQueues(ctx.getBean("retryTestQueue", Queue.class));
        container.setMessageListener(new MessageListenerAdapter(new ApplicationUnrecoverableExceptionTestHandler(), ctx.getBean(MessageConverter.class)));
        unrecoverableContainer.setQueues(ctx.getBean("unrecoverableExceptionQueue", Queue.class));
        unrecoverableContainer.setMessageListener(new MessageListenerAdapter(new UnrecoverableTestHandler(), ctx.getBean(MessageConverter.class)));
        container.start();
        unrecoverableContainer.start();
        template.convertAndSend("retry.test.exchange", "retry.test.binding", new RetryTestBean("test"));
        assertThat(unretry.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unretry.getCount(), is(0L));
        assertThat(unrecover.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unrecover.getCount(), is(0L));
        container.stop();
        unrecoverableContainer.stop();
    }

    @Test
    public void システム例外の場合はリトライが実行されない() throws Exception {
        container.setQueues(ctx.getBean("retryTestQueue", Queue.class));
        container.setMessageListener(new MessageListenerAdapter(new SystemExceptionTestHandler(), ctx.getBean(MessageConverter.class)));
        unrecoverableContainer.setQueues(ctx.getBean("unrecoverableExceptionQueue", Queue.class));
        unrecoverableContainer.setMessageListener(new MessageListenerAdapter(new UnrecoverableTestHandler(), ctx.getBean(MessageConverter.class)));
        container.start();
        unrecoverableContainer.start();
        template.convertAndSend("retry.test.exchange", "retry.test.binding", new RetryTestBean("test"));
        assertThat(unretry.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unretry.getCount(), is(0L));
        assertThat(unrecover.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unrecover.getCount(), is(0L));
        container.stop();
        unrecoverableContainer.stop();
    }

    @Test
    public void RejectAndDontRequeue例外が発生した場合はリトライが実行されない() throws Exception {
        container.setQueues(ctx.getBean("retryTestQueue", Queue.class));
        container.setMessageListener(new MessageListenerAdapter(new AmqpRejectAndDontRequeueExceptionTestHandler(), ctx.getBean(MessageConverter.class)));
        unrecoverableContainer.setQueues(ctx.getBean("unrecoverableExceptionQueue", Queue.class));
        unrecoverableContainer.setMessageListener(new MessageListenerAdapter(new UnrecoverableTestHandler(), ctx.getBean(MessageConverter.class)));
        container.start();
        unrecoverableContainer.start();
        template.convertAndSend("retry.test.exchange", "retry.test.binding", new RetryTestBean("test"));
        assertThat(unretry.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unretry.getCount(), is(0L));
        assertThat(unrecover.await(3, TimeUnit.SECONDS), is(true));
        assertThat(unrecover.getCount(), is(0L));
        container.stop();
        unrecoverableContainer.stop();
    }

    public class ApplicationRecoverableExceptionTestHandler {
        public void handleMessage(RetryTestBean message) {
            retry.countDown();
            throw new AmqpApplicationRecoverableException("test");
        }
    }
    
    public class RecoverableTestHandler {
        public void handleMessage(RetryTestBean message) {
            recover.countDown();
        }
    }
    
    public class UnrecoverableTestHandler {
        public void handleMessage(RetryTestBean message) {
            unrecover.countDown();
        }
    }

    public class ApplicationUnrecoverableExceptionTestHandler {
        public void handleMessage(RetryTestBean message) {
            unretry.countDown();
            throw new AmqpApplicationUnrecoverableException("test");
        }
    }

    public class SystemExceptionTestHandler {
        public void handleMessage(RetryTestBean message) {
            unretry.countDown();
            throw new AmqpSystemException("test");
        }
    }

    public class AmqpRejectAndDontRequeueExceptionTestHandler {
        public void handleMessage(RetryTestBean message) {
            unretry.countDown();
            throw new AmqpRejectAndDontRequeueException("test");
        }
    }
}
