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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jse.core.amqp.exception.AmqpApplicationRecoverableException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(Enclosed.class)
public class ExceptionMessageExchangerTest {

    @RunWith(MockitoJUnitRunner.class)
    public static class ExceptionMessageExchangerRecovererTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();
        
        private Message message = new Message("".getBytes(), new MessageProperties());
        private Throwable t = new Exception("Exception.");

        @Mock
        private RabbitTemplate mock;

        @InjectMocks
        private ExceptionMessageExchanger exchanger = new ExceptionMessageExchanger(); 

        @Before
        public void setup() {
            exchanger.setExchange("error.exchange");
            message.getMessageProperties().setReceivedRoutingKey("the.original.routing-key");
            message.getMessageProperties().setReceivedExchange("the.original.exchange");
        }

        @Test
        public void メッセージヘッダに例外情報が付与されてメッセージが転送される() {
            exchanger.recover(message, t);
            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
            verify(mock).send(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
            MessageProperties prop = messageCaptor.getValue().getMessageProperties();
            assertThat(prop.getHeaders().get("x-exception-stacktrace").toString(), is(ExceptionUtils.getStackTrace(t)));
            assertThat(prop.getHeaders().get("x-exception-message").toString(), is(t.getMessage()));
            assertThat(prop.getHeaders().get("x-original-exchange").toString(), is("the.original.exchange"));
            assertThat(prop.getHeaders().get("x-original-routing-key").toString(), is("the.original.routing-key"));
            assertThat(prop.getHeaders().get("x-exception-id"), is(nullValue()));
        }
        
        @Test
        public void Exchangeが指定されていない場合は例外が発生する() {
            thrown.expect(InternalException.class);
            thrown.expectMessage(containsString("Exchangeキーが設定されていません。"));
            exchanger.setExchange(null);
            exchanger.recover(message, t);
        }

        @Test
        public void AbstractAmqpExceptionを継承した例外の場合は例外IDがヘッダに設定される() {
            class AmqpException extends AmqpApplicationRecoverableException {
                private static final long serialVersionUID = 1L;
                public AmqpException(String code, String routingKey) {
                    super(code, routingKey);
                }
            }
            AmqpException e = new AmqpException("code", "dummy");
            exchanger.recover(message, e);
            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
            verify(mock).send(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
            MessageProperties prop = messageCaptor.getValue().getMessageProperties();
            assertThat(prop.getHeaders().get("x-exception-stacktrace").toString(), is(ExceptionUtils.getStackTrace(e)));
            assertThat(prop.getHeaders().get("x-exception-message").toString(), is(e.getMessage()));
            assertThat(prop.getHeaders().get("x-original-exchange").toString(), is("the.original.exchange"));
            assertThat(prop.getHeaders().get("x-original-routing-key").toString(), is("the.original.routing-key"));
            assertThat(prop.getHeaders().get("x-exception-id").toString(), is(e.getId()));
            assertThat(exchangeCaptor.getValue(), is("error.exchange"));
            assertThat(routingKeyCaptor.getValue(), is(e.getRoutingKey()));
        }

    }

    public static class ExceptionMessageExchangerExceptionTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        @SuppressWarnings("resource")
        public void AmqpTemplateが設定されていなければ例外が発生する() {
            thrown.expectCause(isA(IllegalArgumentException.class));
            thrown.expectMessage("AmqpTemplateのインスタンスが設定されていません。");
            new ClassPathXmlApplicationContext("classpath:/jp/co/ctc_g/jse/core/amqp/retry/NoInjectionAmqpTemp-Context.xml");
        }

        @Test
        @SuppressWarnings("resource")
        public void Exchangeが設定されていなければ例外が発生する() {
            thrown.expectCause(isA(IllegalArgumentException.class));
            thrown.expectMessage("Exchangeキーが設定されていません。");
            new ClassPathXmlApplicationContext("classpath:/jp/co/ctc_g/jse/core/amqp/retry/NoInjectionExchange-Context.xml");
        }

        @Test
        @SuppressWarnings("resource")
        public void DefaultRoutingKeyが設定されていなければ例外が発生する() {
            thrown.expectCause(isA(IllegalArgumentException.class));
            thrown.expectMessage("デフォルトのルーティングキーが設定されていません。");
            new ClassPathXmlApplicationContext("classpath:/jp/co/ctc_g/jse/core/amqp/retry/NoInjectionDefaultRoutingKey-Context.xml");
        }

    }

}