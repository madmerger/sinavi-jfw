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

package jp.co.ctc_g.jse.core.message;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jse.core.message.Messages.MessageType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class MessagesTest {

    @BeforeAll
    public static void setUpClass() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/jp/co/ctc_g/jse/core/message/MessageContextTest");
        MessageSourceLocator.set(messageSource);
    }
    @Test
    public void Messageインスタンスを追加できる() {
        Messages msgs = new Messages(MessageType.ERROR);
        msgs.add(new Message("CREATE_MESSAGE_TEST#001"));
        List<String> messegeList = msgs.getMessageList();
        assertThat(messegeList, hasItem("これはメッセージ生成のテストです。"));
    }
    
    @Test
    public void Messagesインスタンスを追加できる() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        msgs1.add(new Message("CREATE_MESSAGE_TEST#001"));
        
        Messages msgs2 = new Messages(MessageType.ERROR);
        msgs2.add(new Message("CREATE_MESSAGE_TEST#002"));
        
        msgs1.add(msgs2);
        
        List<String> messegeList = msgs1.getMessageList();
        assertThat(messegeList.size(), is(2));
        assertThat(messegeList, hasItem("これはメッセージ生成のテストです。"));
        assertThat(messegeList, hasItem("これはメッセージ追加のテストです。"));
    }
    
    @Test
    public void Messageインスタンスのリストを追加できる() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        msgs1.add(new Message("CREATE_MESSAGE_TEST#001"));
        msgs1.add(Lists.gen(new Message("CREATE_MESSAGE_TEST#002")));
        
        List<String> messegeList = msgs1.getMessageList();
        assertThat(messegeList.size(), is(2));
        assertThat(messegeList, hasItem("これはメッセージ生成のテストです。"));
        assertThat(messegeList, hasItem("これはメッセージ追加のテストです。"));
    }
    
    @Test
    public void メッセージが登録されていない場合空のリストが返却される() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        List<String> messegeList = msgs1.getMessageList();
        assertThat(messegeList.isEmpty(), is(true));
    }
    
    @Test
    public void インスタンス生成時に指定したメッセージタイプが返却される() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        assertThat(msgs1.getMessageType(), is(MessageType.ERROR));
        Messages msgs2 = new Messages(MessageType.VALIDATION);
        assertThat(msgs2.getMessageType(), is(MessageType.VALIDATION));
        Messages msgs3 = new Messages(MessageType.INFORMATION);
        assertThat(msgs3.getMessageType(), is(MessageType.INFORMATION));
    }
    
    @Test
    public void メッセージをクリアできる() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        msgs1.add(new Message("CREATE_MESSAGE_TEST#001"));
        
        Messages msgs2 = new Messages(MessageType.ERROR);
        msgs2.add(new Message("CREATE_MESSAGE_TEST#002"));
        
        msgs1.add(msgs2);
        
        List<String> messegeList = msgs1.getMessageList();
        assertThat(messegeList.size(), is(2));
        
        msgs1.clear();
        assertThat(msgs1.isEmpty(), is(true));
    }
    
    @Test
    public void プロパティ指定でメッセージが登録されていない場合空のリストが返却される() {
        Messages msgs1 = new Messages(MessageType.ERROR);
        List<String> messegeList = msgs1.getMessageList("property");
        assertThat(messegeList.isEmpty(), is(true));
    }
    
    @Test
    public void プロパティ指定でメッセージが返却される() {
        Messages msgs = new Messages(MessageType.VALIDATION);
        msgs.add(new Message("CREATE_MESSAGE_TEST#001", null, "property", "constraintName", "modelName"));
        msgs.add(new Message("CREATE_MESSAGE_TEST#002", null, "property", "constraintName", "modelName"));
        List<String> messegeList = msgs.getMessageList("property");
        assertThat(messegeList.size(), is(2));
        assertThat(messegeList, hasItem("これはメッセージ生成のテストです。"));
        assertThat(messegeList, hasItem("これはメッセージ追加のテストです。"));
    }
    
    @Test
    public void プロパティ指定でワイルドカードを指定し全てのメッセージが返却される() {
        Messages msgs = new Messages(MessageType.VALIDATION);
        msgs.add(new Message("CREATE_MESSAGE_TEST#001", null, "property1", "constraintName", "modelName"));
        msgs.add(new Message("CREATE_MESSAGE_TEST#002", null, "property2", "constraintName", "modelName"));
        List<String> messegeList = msgs.getMessageList("*");
        assertThat(messegeList.size(), is(2));
        assertThat(messegeList, hasItem("これはメッセージ生成のテストです。"));
        assertThat(messegeList, hasItem("これはメッセージ追加のテストです。"));
    }
    
    @Test
    public void プロパティ指定で指定したプロパティに該当するメッセージがない場合は空のリストが返却される() {
        Messages msgs = new Messages(MessageType.VALIDATION);
        msgs.add(new Message("CREATE_MESSAGE_TEST#001", null, "property1", "constraintName", "modelName"));
        msgs.add(new Message("CREATE_MESSAGE_TEST#002", null, "property2", "constraintName", "modelName"));
        List<String> messegeList = msgs.getMessageList("property3");
        assertThat(messegeList.size(), is(0));
    }
    
    @Test
    public void 同じキーのインスタンスで削除できる() {
        Messages msgs = new Messages(MessageType.VALIDATION);
        Message msg = new Message("message.key.1");
        msgs.add(msg);
        msgs.add(new Message("message.key.2"));
        List<String> messegeList = msgs.getMessageList();
        assertThat(messegeList.size(), is(2));
        msgs.remove(msg);
        messegeList = msgs.getMessageList();
        assertThat(messegeList.size(), is(1));
    }
    
    @Test
    public void メッセージキーで削除できる() {
        Messages msgs = new Messages(MessageType.VALIDATION);
        Message msg = new Message("message.key.1");
        msgs.add(msg);
        msgs.add(new Message("message.key.2"));
        List<String> messegeList = msgs.getMessageList();
        assertThat(messegeList.size(), is(2));
        msgs.remove("message.key.1");
        messegeList = msgs.getMessageList();
        assertThat(messegeList.size(), is(1));
    }
}
