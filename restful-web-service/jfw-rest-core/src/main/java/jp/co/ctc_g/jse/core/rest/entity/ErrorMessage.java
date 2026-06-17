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

package jp.co.ctc_g.jse.core.rest.entity;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * <p>
 * このクラスは、例外が発生した際に例外情報を呼び出し元に返すドメインです。
 * </p>
 * <p>
 * 例外情報をJSON形式で呼出し元に返します。
 * 入力エラーや回復可能な例外、回復不可能な例外によって返す値が異なります。
 * </p>
 * <p>
 * 入力エラーは
 * 以下の値を返します。
 * <ol>
 * <li>HTTPステータスコード:400</li>
 * <li>エラーコード</li>
 * <li>エラーメッセージ</li>
 * <li>バリデーションエラーメッセージの配列：詳細は{@link ValidationMessage}を参照してください。</li>
 * </ol>
 * </p>
 * <p>
 * 回復可能な例外(HTTPのステータスコードが4xxやアプリケーション例外)は
 * 以下の値を返します。
 * <ol>
 * <li>HTTPステータスコード</li>
 * <li>エラーコード</li>
 * <li>エラーメッセージ</li>
 * </ol>
 * </p>
 * <p>
 * 回復不可能な例外(HTTPのスタータスコードが5xxやシステム例外)は
 * 以下の値を返します。
 * <ol>
 * <li>ユニークID</li>
 * <li>HTTPステータスコード</li>
 * <li>エラーコード</li>
 * <li>エラーメッセージ</li>
 * </ol>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SEPARATOR = " ";

    /**
     * UUID
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String id;

    /**
     * HTTPステータスコード
     */
    private int status = -1;

    /**
     * エラーコード
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String code;

    /**
     * エラーメッセージ
     */
    private String message;

    /**
     * バリデーションメッセージ
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private List<ValidationMessage> validationMessages;

    /**
     * コンストラクタです。
     */
    public ErrorMessage() {}

    /**
     * HTTPステータスコードを取得します。
     * @return HTTPステータスコード
     */
    public int getStatus() {
        return status;
    }

    /**
     * HTTPステータスコードを設定します。
     * @param status HTTPステータスコード
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * メッセージを取得します。
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * メッセージを設定します。
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * コードを取得します。
     * @return コード
     */
    public String getCode() {
        return code;
    }

    /**
     * コードを設定します。
     * @param code コード
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * UUIDを取得します。
     * @return UUID
     */
    public String getId() {
        return id;
    }

    /**
     * UUIDを設定します。
     * @param id UUID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * バリデーションメッセージを取得します。
     * @return バリデーションメッセージ
     */
    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    /**
     * バリデーションメッセージを設定します。
     * @param validationMessages バリデーションメッセージ
     */
    public void setValidationMessages(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * ログメッセージを生成します。
     * デフォルトでは
     * <p>ステータスコード△エラーコード△id△メッセージ△[バリデーションエラー]</p>
     * です。
     * △は半角スペース
     * それぞれの変数の値がNULLの場合はログメッセージには含めません。
     * @return ログメッセージ
     */
    public String log() {
        StringBuilder msg = new StringBuilder();
        if (status != -1) msg.append(status).append(SEPARATOR);
        if (code != null) msg.append(code).append(SEPARATOR);
        if (id != null) msg.append(id).append(SEPARATOR);
        if (message != null) msg.append(message).append(SEPARATOR);
        if (validationMessages != null && !validationMessages.isEmpty()) {
            for (ValidationMessage vm : validationMessages) {
                msg.append(vm.toString()).append(SEPARATOR);
            }
        }
        return msg.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
