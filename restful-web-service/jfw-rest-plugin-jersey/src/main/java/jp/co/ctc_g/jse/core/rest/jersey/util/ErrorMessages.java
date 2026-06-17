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

package jp.co.ctc_g.jse.core.rest.jersey.util;

import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;
import jp.co.ctc_g.jse.core.rest.entity.ValidationMessage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * <p>
 * このクラスは、例外が発生した際に例外情報を呼び出し元に返すドメインを構築するユーティリティです。
 * </p>
 * <p>
 * 以下のように利用します。
 * <pre class="brush:java">
 * ErrorMessages.create().id().status(400).message("リソースが見つかりません。).get()
 * </pre>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public final class ErrorMessages {

    private static final ResourceBundle R = InternalMessages.getBundle(ErrorMessages.class);
    private final ErrorMessage error;
    private final List<ValidationMessage> validation = new LinkedList<ValidationMessage>();

    /**
     * このユーティリティのインスタンスを生成します。
     * @return ユーティリティ
     */
    public static ErrorMessages create() {
        return new ErrorMessages();
    }

    /**
     * このユーティリティのインスタンスを生成します。
     * 引数で指定されたステータスコードで内部で保持している例外情報のドメインにステータスコードを設定します。
     * @param status HTTPステータス
     * @return ユーティリティ
     */
    public static ErrorMessages create(Status status) {
        return new ErrorMessages(status);
    }

    /**
     * このユーティリティのインスタンスを生成します。
     * 引数で指定された例外情報が保持しているステータスコードで内部で保持している例外情報のドメインにステータスコードを設定します。
     * @param exception 例外情報
     * @return ユーティリティ
     */
    public static ErrorMessages create(WebApplicationException exception) {
        return new ErrorMessages(exception);
    }

    /**
     * ランダムUUIDを設定します。
     * @return 本クラス
     */
    public ErrorMessages id() {
        error.setId(UUID.randomUUID().toString());
        return this;
    }

    /**
     * 指定されたIDを設定します。
     * @param id ID
     * @return ユーティリティ
     */
    public ErrorMessages id(String id) {
        error.setId(id);
        return this;
    }

    /**
     * エラーコードを設定します。
     * @param code エラーコード
     * @return ユーティリティ
     */
    public ErrorMessages code(String code) {
        error.setCode(code);
        return this;
    }

    /**
     * HTTPステータスコードを設定します。
     * @param status HTTPステータスコード
     * @return ユーティリティ
     */
    public ErrorMessages status(int status) {
        error.setStatus(status);
        return this;
    }

    /**
     * HTTPステータスコードを設定します。
     * @param status HTTPステータス
     * @return ユーティリティ
     */
    public ErrorMessages status(Status status) {
        error.setStatus(status.getStatusCode());
        error.setMessage(status.getReasonPhrase());
        return this;
    }

    /**
     * エラーメッセージを設定します。
     * @param message メッセージ
     * @return ユーティリティ
     */
    public ErrorMessages message(String message) {
        error.setMessage(message);
        return this;
    }

    /**
     * エラーコードをプロパティファイルより解決し、エラーメッセージを設定します。
     * @return ユーティリティ
     */
    public ErrorMessages resolve() {
        Args.checkNotNull(error.getCode(), R.getString("E-REST-JERSEY-UTIL#0001"));
        error.setMessage(ErrorResources.find(error.getCode()));
        return this;
    }

    /**
     * 指定されたキーでプロパティファイルより解決し、エラーメッセージを設定します。
     * @param key キー
     * @param locale ロケール
     * @return ユーティリティ
     */
    public ErrorMessages resolveMessage(String key, Locale locale) {
        error.setMessage(ErrorResources.find(key, locale));
        return this;
    }

    /**
     * 指定されたプロパティ名、メッセージをバリデーションメッセージに追加します。
     * @param path パス
     * @param message メッセージ
     * @return ユーティリティ
     */
    public ErrorMessages bind(String path, String message) {
        validation.add(new ValidationMessage(path, message));
        return this;
    }

    /**
     * {@link ErrorMessage}を取得します。
     * @return {@link ErrorMessage}
     */
    public ErrorMessage get() {
        if (!validation.isEmpty()) error.setValidationMessages(validation);
        return error;
    }

    private ErrorMessages() {
        this.error = new ErrorMessage();
    }
    
    private ErrorMessages(Status status) {
        this.error = new ErrorMessage();
        this.error.setStatus(status.getStatusCode());
        this.error.setMessage(status.getReasonPhrase());
    }

    private ErrorMessages(WebApplicationException exception) {
        this.error = new ErrorMessage();
        this.error.setStatus(exception.getResponse().getStatusInfo().getStatusCode());
        this.error.setMessage(exception.getResponse().getStatusInfo().getReasonPhrase());
    }

}
