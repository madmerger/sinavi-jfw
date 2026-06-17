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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <p>
 * このクラスは、バリデーションメッセージを呼出し元に返すドメインです。
 * </p>
 * <p>
 * このクラス単体で利用されることは想定していません。
 * {@link jp.co.ctc_g.jse.core.rest.entity.ErrorMessage}に集約され、
 * クライアントへ以下の値をJSON形式で返します。
 * <ol>
 *  <li>バリデーションエラーが発生したJavaのパス(例：HogeResource.create.Hoge.id)</li>
 *  <li>バリデーションエラーのメッセージ</li>
 * </ol>
 * <pre class="brush:java">
 * {
 *  "status":400,
 *  "code":"W-REST-CLIENT#400",
 *  "message":"不正なリクエストです。",
 *  "validationMessages":[
 *   {"path":"HogeResource.create.Hoge.lastname","message":"必須入力です。"},
 *   {"path":"StaffResource.create.Hoge.firstname","message":"必須入力です。"}
 *  ]
 * }
 * </pre>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ValidationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * パス
     */
    private String path;

    /**
     * メッセージ
     */
    private String message;

    /**
     * デフォルトコンストラクタです。
     */
    public ValidationMessage() {

    }

    /**
     * コンストラクタです。
     * @param path エラーが発生したパス
     * @param message メッセージ
     */
    public ValidationMessage(String path, String message) {
        setPath(path);
        setMessage(message);
    }

    /**
     * エラーが発生したパスを取得します。
     * @return エラーが発生したパス
     */
    public String getPath() {
        return path;
    }

    /**
     * エラーが発生したパスを設定します。
     * @param path エラーが発生したパス
     */
    public void setPath(String path) {
        this.path = path;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
