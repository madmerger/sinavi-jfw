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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jse.core.message.MessageContext.Scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageContextTest {

    private MockHttpServletRequest request;

    @BeforeAll
    public static void setUpClass() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/jp/co/ctc_g/jse/core/message/MessageContextTest");
        MessageSourceLocator.set(messageSource);
    }

    @BeforeEach
    public void setUp() {
        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
    }

    @Test
    public void バリデーションメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToRequest("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName");
        List<String> result = getMessageFromRequest(MessageContext.VALIDATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
    }

    @Test
    public void スコープ指定でバリデーションメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessage("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName", Scope.REQUEST);
        List<String> result = getMessageFromRequest(MessageContext.VALIDATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
    }

    @Test
    public void バリデーションメッセージを複数件リクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToRequest("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName");
        context.saveValidationMessageToRequest("V-MESSAGE_CONTEXT_TEST#002", "property", "constraintName", "modelName");
        List<String> result = getMessageFromRequest(MessageContext.VALIDATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはバリデーションエラー複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void バリデーションメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToFlash("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName");
        List<String> result = getMessageFromFlash(MessageContext.VALIDATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
    }

    @Test
    public void スコープ指定でバリデーションメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessage("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName", Scope.FLASH);
        List<String> result = getMessageFromFlash(MessageContext.VALIDATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
    }

    @Test
    public void バリデーションメッセージを複数件フラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToFlash("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName");
        context.saveValidationMessageToFlash("V-MESSAGE_CONTEXT_TEST#002", "property", "constraintName", "modelName");
        List<String> result = getMessageFromFlash(MessageContext.VALIDATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはバリデーションエラーをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはバリデーションエラー複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void バリデーションメッセージをセッションスコープに保存するとエラーが発生する() {
        assertThrows(InternalException.class, () -> {
            new MessageContext(request).saveValidationMessage("V-MESSAGE_CONTEXT_TEST#001", "property", "constraintName", "modelName", Scope.SESSION);
        });
    }

    @Test
    public void インフォメーションメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#001");
        List<String> result = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void インフォメーションメッセージを複数件リクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはインフォメーションメッセージ複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのインフォメーションメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"));
        List<String> result = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)インフォメーションメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void インフォメーションメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#001");
        List<String> result = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void インフォメーションメッセージを複数件フラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはインフォメーションメッセージ複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void インフォメーションメッセージを指定したスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        List<String> result = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.FLASH);
        result = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
        request.getSession(true);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        result = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのインフォメーションメッセージをセッションスコープに保存する() {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"), Scope.SESSION);
        List<String> result = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)インフォメーションメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void インフォメーションメッセージを複数件セッションスコープに保存する() {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToSession("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToSession("I-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはインフォメーションメッセージ複数件をテストするダミーメッセージです。"));
    }

    public void セッションが開始していないければ開始してメッセージを設定する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        List<String> result = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これはインフォメーションメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのインフォメーションメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"));
        List<String> result = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)インフォメーションメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void エラーメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-MESSAGE_CONTEXT_TEST#001");
        List<String> result = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void エラーメッセージを複数件リクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToRequest("E-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはエラーメッセージ複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのエラーメッセージをリクエストスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"));
        List<String> result = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)エラーメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void エラーメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToFlash("E-MESSAGE_CONTEXT_TEST#001");
        List<String> result = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void エラーメッセージを複数件フラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToFlash("E-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToFlash("E-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはエラーメッセージ複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのエラーメッセージをフラッシュスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToFlash("E-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"));
        List<String> result = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)エラーメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void エラーメッセージを指定したスコープに保存する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        List<String> result = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.FLASH);
        result = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
        request.getSession(true);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        result = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void メッセージプレスホルダー置換パラメータ付きのエラーメッセージをセッションスコープに保存する() {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#003", Maps.hash("replace", "ここが置換されます。"), Scope.SESSION);
        List<String> result = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これは(メッセージプレスホルダー置換パラメータ付き)エラーメッセージをテストするダミーメッセージです。(ここが置換されます。)"));
    }

    @Test
    public void エラーメッセージを複数件セッションスコープに保存する() {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToSession("E-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToSession("E-MESSAGE_CONTEXT_TEST#002");
        List<String> result = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(result.size(), is(2));
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
        assertThat(result, hasItem("これはエラーメッセージ複数件をテストするダミーメッセージです。"));
    }

    @Test
    public void セッションが開始していないければセッションを開始してエラーメッセージを設定する() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        List<String> result = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(result, hasItem("これはエラーメッセージをテストするダミーメッセージです。"));
    }

    @Test
    public void インフォメーションメッセージをクリアする() throws Exception {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToSession("I-MESSAGE_CONTEXT_TEST#001");
        List<String> resultRequest = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest.size(), is(Integer.valueOf(1)));
        List<String> resultFlash = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash.size(), is(Integer.valueOf(1)));
        List<String> resultSession = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession.size(), is(Integer.valueOf(1)));
        context.clearInformationMessage();
        resultRequest = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest, is(nullValue()));
        resultFlash = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash, is(nullValue()));
        resultSession = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession, is(nullValue()));
    }

    @Test
    public void スコープ指定でメッセージをクリアする() throws Exception {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToFlash("I-MESSAGE_CONTEXT_TEST#001");
        context.saveInformationMessageToSession("I-MESSAGE_CONTEXT_TEST#001");
        List<String> resultRequest = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest.size(), is(Integer.valueOf(1)));
        List<String> resultFlash = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash.size(), is(Integer.valueOf(1)));
        List<String> resultSession = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession.size(), is(Integer.valueOf(1)));
        context.clearInformationMessage(Scope.REQUEST);
        resultRequest = getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest, is(nullValue()));
        context.clearInformationMessage(Scope.FLASH);
        resultFlash = getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash, is(nullValue()));
        context.clearInformationMessage(Scope.SESSION);
        resultSession = getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession, is(nullValue()));
    }

    @Test
    public void エラーメッセージをクリアする() throws Exception {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("I-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToFlash("I-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToSession("I-MESSAGE_CONTEXT_TEST#001");
        List<String> resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest.size(), is(Integer.valueOf(1)));
        List<String> resultFlash = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash.size(), is(Integer.valueOf(1)));
        List<String> resultSession = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession.size(), is(Integer.valueOf(1)));
        context.clearErrorMessage();
        resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest, is(nullValue()));
        resultFlash = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash, is(nullValue()));
        resultSession = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession, is(nullValue()));
    }

    @Test
    public void スコープ指定でエラーメッセージをクリアする() throws Exception {
        request.getSession(true);
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToFlash("E-MESSAGE_CONTEXT_TEST#001");
        context.saveErrorMessageToSession("E-MESSAGE_CONTEXT_TEST#001");
        List<String> resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest.size(), is(Integer.valueOf(1)));
        List<String> resultFlash = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash.size(), is(Integer.valueOf(1)));
        List<String> resultSession = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession.size(), is(Integer.valueOf(1)));
        context.clearErrorMessage(Scope.REQUEST);
        resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest, is(nullValue()));
        context.clearErrorMessage(Scope.FLASH);
        resultFlash = getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        assertThat(resultFlash, is(nullValue()));
        context.clearErrorMessage(Scope.SESSION);
        resultSession = getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        assertThat(resultSession, is(nullValue()));
    }

    @Test
    public void 現在有効なメッセージコンテキストが取得できる() throws Exception {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("I-MESSAGE_CONTEXT_TEST#001", Maps.hash("class", MessageContextTest.class.getName()));

        MessageContext current = MessageContext.getCurrentMessageContext(request);
        List<String> resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(current, is(notNullValue()));
        assertThat(resultRequest.size(), is(1));
    }

    @Test
    public void 現在有効なメッセージコンテキストが存在しない場合は新しいコンテキスが生成され返却される() throws Exception {
        MessageContext current = MessageContext.getCurrentMessageContext(request);
        assertThat(current, is(notNullValue()));
    }

    @Test
    public void エラーメッセージが保存されていないケースでリクエストスコープのメッセージをクリア() {
        MessageContext context = new MessageContext(request);
        context.clearErrorMessage();
        List<String> resultRequest = getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        assertThat(resultRequest, is(nullValue()));
    }
    
    @Test
    public void コード指定でスコープからインフォメーションメッセージが削除される() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.FLASH);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        context.clearInformationMessage("I-MESSAGE_CONTEXT_TEST#001");
        assertThat(getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST), nullValue());
        assertThat(getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH), nullValue());
        assertThat(getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION), nullValue());
    }
    
    @Test
    public void コード指定で他のスコープのインフォメーションメッセージが削除されない() {
        MessageContext context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearInformationMessage("I-MESSAGE_CONTEXT_TEST#001");
        assertThat(getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST), nullValue());
        assertThat(getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH), notNullValue());
        assertThat(getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION), notNullValue());
        
        context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearInformationMessage("I-MESSAGE_CONTEXT_TEST#002");
        
        assertThat(getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST), notNullValue());
        assertThat(getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH), nullValue());
        assertThat(getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION), notNullValue());
        
        context = new MessageContext(request);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveInformationMessage("I-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearInformationMessage("I-MESSAGE_CONTEXT_TEST#003");
        
        assertThat(getMessageFromRequest(MessageContext.INFORMATION_MESSAGE_KEY_TO_REQUEST), notNullValue());
        assertThat(getMessageFromFlash(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH), notNullValue());
        assertThat(getMessageFromSession(MessageContext.INFORMATION_MESSAGE_KEY_TO_SESSION), nullValue());
    }
    
    @Test
    public void コード指定でスコープからエラーメッセージが削除される() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.FLASH);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.SESSION);
        context.clearErrorMessage("E-MESSAGE_CONTEXT_TEST#001");
        assertThat(getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST), nullValue());
        assertThat(getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH), nullValue());
        assertThat(getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION), nullValue());
    }
    
    @Test
    public void コード指定で他のスコープのエラーメッセージが削除されない() {
        MessageContext context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearErrorMessage("E-MESSAGE_CONTEXT_TEST#001");
        assertThat(getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST), nullValue());
        assertThat(getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH), notNullValue());
        assertThat(getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION), notNullValue());
        
        context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearErrorMessage("E-MESSAGE_CONTEXT_TEST#002");
        
        assertThat(getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST), notNullValue());
        assertThat(getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH), nullValue());
        assertThat(getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION), notNullValue());
        
        context = new MessageContext(request);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#001", Scope.REQUEST);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#002", Scope.FLASH);
        context.saveErrorMessage("E-MESSAGE_CONTEXT_TEST#003", Scope.SESSION);
        context.clearErrorMessage("E-MESSAGE_CONTEXT_TEST#003");
        
        assertThat(getMessageFromRequest(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST), notNullValue());
        assertThat(getMessageFromFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH), notNullValue());
        assertThat(getMessageFromSession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION), nullValue());
    }

    private List<String> getMessageFromRequest(String key) {
        Messages messages = (Messages) request.getAttribute(key);
        if (messages != null && !messages.isEmpty())
            return messages.getMessageList();

        return null;
    }

    private List<String> getMessageFromFlash(String key) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        Messages messages = (Messages) flashMap.get(key);
        if (messages != null && !messages.isEmpty())
            return messages.getMessageList();

        return null;
    }

    private List<String> getMessageFromSession(String key) {
        Messages messages = (Messages) request.getSession().getAttribute(key);
        if (messages != null && !messages.isEmpty())
            return messages.getMessageList();

        return null;
    }
}
