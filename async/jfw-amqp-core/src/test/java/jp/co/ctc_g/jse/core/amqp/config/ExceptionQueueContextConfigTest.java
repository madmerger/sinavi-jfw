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
import static org.junit.jupiter.api.Assertions.assertThrows;
import jp.co.ctc_g.jse.core.amqp.config.exception.DefaultProperties;
import jp.co.ctc_g.jse.core.amqp.config.exception.OverrideProperties;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationRecoverableException;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationUnrecoverableException;
import jp.co.ctc_g.jse.core.amqp.retry.ExceptionMessageExchanger;
import jp.co.ctc_g.jse.core.amqp.retry.LoggingErrorHandler;

import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// ExpectedException removed - use assertThrows;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// @Nested classes used instead of Enclosed
public class ExceptionQueueContextConfigTest {

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = DefaultProperties.class)
    public static class ExceptionQueueContextConfigLoadTest {

        @Autowired
        private ApplicationContext context;

        @Autowired
        private ExceptionQueueContextConfig config;

        @Test
        public void デフォルト値が設定されて指定のインスタンスがDIコンテナに登録される() {
            assertThat(config.errorExchangeKey, is("error.exchange"));
            assertThat(config.exceptionBindingKey, is("#.exception.#"));
            assertThat(config.exceptionExchangeKey, is("exception.exchange"));
            assertThat(config.recoverableExceptionQueue, is("recoverable.exception.messages.queue"));
            assertThat(config.unrecoverableExceptionQueue, is("unrecoverable.exception.messages.queue"));
            assertThat(config.unrecoverableExceptionBindingKey, is("#.unrecoverable.exception.#"));
            assertThat(config.recoverableExceptionBindingKey, is("#.recoverable.exception.#"));
            assertThat(config.unknownExceptionRoutingKey, is("unknown.unrecoverable.exception.key"));
            TopicExchange error = context.getBean("errorExchange", TopicExchange.class);
            assertThat(error, is(notNullValue()));
            assertThat(error.getType(), is("topic"));
            assertThat(error.getName(), is("error.exchange"));
            TopicExchange exception = context.getBean("exceptionExchange", TopicExchange.class);
            assertThat(exception, is(notNullValue()));
            assertThat(exception.getType(), is("topic"));
            assertThat(exception.getName(), is("exception.exchange"));
            Queue recover = context.getBean("recoverableExceptionQueue", Queue.class);
            assertThat(recover, is(notNullValue()));
            assertThat(recover.getName(), is("recoverable.exception.messages.queue"));
            Queue unrecover = context.getBean("unrecoverableExceptionQueue", Queue.class);
            assertThat(unrecover, is(notNullValue()));
            assertThat(unrecover.getName(), is("unrecoverable.exception.messages.queue"));
            Binding exceptionBinding = context.getBean("exceptionBinding", Binding.class);
            assertThat(exceptionBinding, is(notNullValue()));
            assertThat(exceptionBinding.getExchange(), is("error.exchange"));
            assertThat(exceptionBinding.getDestination(), is("exception.exchange"));
            assertThat(exceptionBinding.getRoutingKey(), is("#.exception.#"));
            Binding recoverBinding = context.getBean("recoverableExceptionQueueBinding", Binding.class);
            assertThat(recoverBinding, is(notNullValue()));
            assertThat(recoverBinding.getExchange(), is("exception.exchange"));
            assertThat(recoverBinding.getDestination(), is("recoverable.exception.messages.queue"));
            assertThat(recoverBinding.getRoutingKey(), is("#.recoverable.exception.#"));
            Binding unrecoverBinding = context.getBean("unrecoverableExceptionQueueBinding", Binding.class);
            assertThat(unrecoverBinding, is(notNullValue()));
            assertThat(unrecoverBinding.getExchange(), is("exception.exchange"));
            assertThat(unrecoverBinding.getDestination(), is("unrecoverable.exception.messages.queue"));
            assertThat(unrecoverBinding.getRoutingKey(), is("#.unrecoverable.exception.#"));
            
            assertThat(config.retryCount, is(10));
            assertThat(config.backOffPeriod, is(1000L));
            assertThat(context.getBean(RetryOperations.class), is(notNullValue()));
            assertThat(context.getBean(ExceptionMessageExchanger.class), is(notNullValue()));
            assertThat(context.getBean(LoggingErrorHandler.class), is(notNullValue()));
            LoggingErrorHandler handler = context.getBean(LoggingErrorHandler.class);
            BinaryExceptionClassifier mapping = handler.getRetryableClassifier();
            assertThat(mapping, is(notNullValue()));
            assertThat(mapping.classify(new AmqpApplicationRecoverableException("")), is(true));
            assertThat(mapping.classify(new AmqpApplicationUnrecoverableException("")), is(false));
            assertThat(context.getBean(LoggingErrorHandler.class), is(notNullValue()));
            assertThat(context.getBean(Advice[].class), is(notNullValue()));
            assertThat(context.getBean(StatefulRetryOperationsInterceptor.class), is(notNullValue()));
        }

        @Test
        public void プロファイルが指定されていないときはRabbitAdminのインスタンスが生成されない() {
            assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(AmqpAdmin.class));
        }

    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = DefaultProperties.class)
    @ActiveProfiles("development")
    public static class BeanCreateAmqpAdminTest {

        @Autowired
        private ApplicationContext context;

        @Test
        public void プロファイルが指定されていないときはRabbitAdminのインスタンスが生成される() {
            assertThat(context.getBean(AmqpAdmin.class), is(notNullValue()));
        }
    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = OverrideProperties.class)
    public static class ExceptionQueueContextConfigOverridePropertiesLoadTest {

        @Autowired
        private ApplicationContext context;

        @Autowired
        private ExceptionQueueContextConfig config;

        @Test
        public void デフォルト値が設定されて指定のインスタンスがDIコンテナに登録される() {
            assertThat(config.errorExchangeKey, is("override.error"));
            assertThat(config.exceptionBindingKey, is("override.exception.binding"));
            assertThat(config.exceptionExchangeKey, is("override.exception"));
            assertThat(config.recoverableExceptionQueue, is("override.recoverable.exception.message.queue"));
            assertThat(config.unrecoverableExceptionQueue, is("override.unrecoverable.exception.message.queue"));
            assertThat(config.unrecoverableExceptionBindingKey, is("override.unrecoverable.exception.binding"));
            assertThat(config.recoverableExceptionBindingKey, is("override.recoverable.exception.binding"));
            assertThat(config.unknownExceptionRoutingKey, is("override.exception.key"));
            TopicExchange error = context.getBean("errorExchange", TopicExchange.class);
            assertThat(error, is(notNullValue()));
            assertThat(error.getType(), is("topic"));
            assertThat(error.getName(), is("override.error"));
            TopicExchange exception = context.getBean("exceptionExchange", TopicExchange.class);
            assertThat(exception, is(notNullValue()));
            assertThat(exception.getType(), is("topic"));
            assertThat(exception.getName(), is("override.exception"));
            Queue recover = context.getBean("recoverableExceptionQueue", Queue.class);
            assertThat(recover, is(notNullValue()));
            assertThat(recover.getName(), is("override.recoverable.exception.message.queue"));
            Queue unrecover = context.getBean("unrecoverableExceptionQueue", Queue.class);
            assertThat(unrecover, is(notNullValue()));
            assertThat(unrecover.getName(), is("override.unrecoverable.exception.message.queue"));
            Binding exceptionBinding = context.getBean("exceptionBinding", Binding.class);
            assertThat(exceptionBinding, is(notNullValue()));
            assertThat(exceptionBinding.getExchange(), is("override.error"));
            assertThat(exceptionBinding.getDestination(), is("override.exception"));
            assertThat(exceptionBinding.getRoutingKey(), is("override.exception.binding"));
            Binding recoverBinding = context.getBean("recoverableExceptionQueueBinding", Binding.class);
            assertThat(recoverBinding, is(notNullValue()));
            assertThat(recoverBinding.getExchange(), is("override.exception"));
            assertThat(recoverBinding.getDestination(), is("override.recoverable.exception.message.queue"));
            assertThat(recoverBinding.getRoutingKey(), is("override.recoverable.exception.binding"));
            Binding unrecoverBinding = context.getBean("unrecoverableExceptionQueueBinding", Binding.class);
            assertThat(unrecoverBinding, is(notNullValue()));
            assertThat(unrecoverBinding.getExchange(), is("override.exception"));
            assertThat(unrecoverBinding.getDestination(), is("override.unrecoverable.exception.message.queue"));
            assertThat(unrecoverBinding.getRoutingKey(), is("override.unrecoverable.exception.binding"));

            assertThat(config.retryCount, is(999));
            assertThat(config.backOffPeriod, is(99999L));
            assertThat(context.getBean(RetryOperations.class), is(notNullValue()));
            assertThat(context.getBean(ExceptionMessageExchanger.class), is(notNullValue()));
            assertThat(context.getBean(LoggingErrorHandler.class), is(notNullValue()));
            LoggingErrorHandler handler = context.getBean(LoggingErrorHandler.class);
            BinaryExceptionClassifier mapping = handler.getRetryableClassifier();
            assertThat(mapping, is(notNullValue()));
            assertThat(mapping.classify(new AmqpApplicationRecoverableException("")), is(true));
            assertThat(mapping.classify(new AmqpApplicationUnrecoverableException("")), is(false));
            assertThat(context.getBean(LoggingErrorHandler.class), is(notNullValue()));
            assertThat(context.getBean(Advice[].class), is(notNullValue()));
            assertThat(context.getBean(StatefulRetryOperationsInterceptor.class), is(notNullValue()));
        }

    }

}
