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

import java.util.Map;
import java.util.ResourceBundle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.message.Messages.MessageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * <p>
 * このクラスは、メッセージを保存するためのインタフェースを提供するコンテキストです。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class MessageContext {

    /**
     * <p>
     * メッセージを格納するためのスコープを表現する列挙型です。
     * </p>
     * @author ITOCHU Techno-Solutions Corporation.
     */
    public enum Scope {
        /**
         * リクエスト
         */
        REQUEST,
        /**
         * フラッシュ
         */
        FLASH,
        /**
         * セッション
         */
        SESSION
    }

    /** {@code MessageContext} インスタンスを{@link HttpServletRequest}に保存する際のキー */
    public static final String MESSAGE_CONTEXT_ATTRIBUTE_KEY = MessageContext.class.getName() + ".MESSAGE_CONTEXT";

    /** 入力チェックエラー・メッセージを格納した{@link Messages}インスタンスを{@link HttpServletRequest}に保存する際のキー */
    public static final String VALIDATION_MESSAGE_KEY_TO_REQUEST = MessageContext.class.getName() + ".VALIDATION_MESSAGE_KEY_TO_REQUEST";

    /** 入力チェックエラー・メッセージを格納した{@link Messages}インスタンスをフラッシュ・スコープに保存する際のキー */
    public static final String VALIDATION_MESSAGE_KEY_TO_FLASH = MessageContext.class.getName() + ".VALIDATION_MESSAGE_KEY_TO_FLASH";

    /** エラー・メッセージを格納した{@link Messages}インスタンスを{@link HttpServletRequest}に保存する際のキー */
    public static final String ERROR_MESSAGE_KEY_TO_REQUEST = MessageContext.class.getName() + ".ERROR_MESSAGE_KEY_TO_REQUEST";

    /** エラー・メッセージを格納した{@link Messages}インスタンスをフラッシュ・スコープに保存する際のキー */
    public static final String ERROR_MESSAGE_KEY_TO_FLASH = MessageContext.class.getName() + ".ERROR_MESSAGE_KEY_TO_FLASH";

    /** エラー・メッセージを格納した{@link Messages}インスタンスをセッション・スコープに保存する際のキー */
    public static final String ERROR_MESSAGE_KEY_TO_SESSION = MessageContext.class.getName() + ".ERROR_MESSAGE_KEY_TO_SESSION";

    /** インフォメーション・メッセージを格納した{@link Messages}インスタンスを{@link HttpServletRequest}に保存する際のキー */
    public static final String INFORMATION_MESSAGE_KEY_TO_REQUEST = MessageContext.class.getName() + ".INFORMATION_MESSAGE_KEY_TO_REQUEST";

    /** インフォメーション・メッセージを格納した{@link Messages}インスタンスをフラッシュ・スコープに保存する際のキー */
    public static final String INFORMATION_MESSAGE_KEY_TO_FLASH = MessageContext.class.getName() + ".INFORMATION_MESSAGE_KEY_TO_FLASH";

    /** インフォメーション・メッセージを格納した{@link Messages}インスタンスをセッション・スコープに保存する際のキー */
    public static final String INFORMATION_MESSAGE_KEY_TO_SESSION = MessageContext.class.getName() + "INFORMATION_MESSAGE_KEY_TO_SESSION";

    /** 画面上に表示しないメッセージを格納するキー */
    public static final String MESSGE_IGNORE_KEY = MessageContext.class.getName() + ".MESSAGE_IGNORE_KEY";

    /** メッセージをスコープにバインドする際のキー文字列の配列 */
    protected static final String[] MESSAGES_KEY;
    static {
        MESSAGES_KEY = Arrays.gen(
            VALIDATION_MESSAGE_KEY_TO_REQUEST,
            VALIDATION_MESSAGE_KEY_TO_FLASH,
            INFORMATION_MESSAGE_KEY_TO_REQUEST,
            INFORMATION_MESSAGE_KEY_TO_FLASH,
            INFORMATION_MESSAGE_KEY_TO_SESSION,
            ERROR_MESSAGE_KEY_TO_REQUEST,
            ERROR_MESSAGE_KEY_TO_FLASH,
            ERROR_MESSAGE_KEY_TO_SESSION
        );
    }

    private HttpServletRequest request;

    private static final ResourceBundle R = InternalMessages.getBundle(MessageContext.class);
    private static final Logger L = LoggerFactory.getLogger(MessageContext.class);

    /**
     * デフォルトコンストラクタです。
     */
    public MessageContext() {}

    /**
     * コンストラクタです。
     * @param request {@link HttpServletRequest} インスタンス
     */
    public MessageContext(HttpServletRequest request) {
        Args.checkNotNull(request);
        this.request = request;
    }

    /**
     * <p>
     * バリデーションメッセージをリクエストスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param property プロパティ名
     * @param constraintName バリデータ名
     * @param modelName モデル名
     */
    public void saveValidationMessageToRequest(String code, String property, String constraintName, String modelName) {
        saveValidationMessageToRequest(code, null, property, constraintName, modelName);
    }

    /**
     * <p>
     * バリデーションメッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param property プロパティ名
     * @param constraintName バリデータ名
     * @param modelName モデル名
     * @param scope 保存するスコープ
     */
    public void saveValidationMessage(String code, String property, String constraintName, String modelName, Scope scope) {
        saveValidationMessage(code, null, property, constraintName, modelName, scope);
    }

    /**
     * <p>
     * バリデーションメッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param replace プレースホルダーを置換するキーと値の{@link Map}
     * @param property プロパティ名
     * @param constraintName バリデータ名
     * @param modelName モデル名
     * @param scope 保存するスコープ
     */
    public void saveValidationMessage(String code, Map<String, ? extends Object> replace, String property, String constraintName, String modelName, Scope scope) {
        Args.checkNotNull(scope);
        switch (scope) {
        case REQUEST:
            saveValidationMessageToRequest(code, replace, property, constraintName, modelName);
            break;
        case FLASH:
            saveValidationMessageToFlash(code, replace, property, constraintName, modelName);
            break;
        case SESSION:
        default:
            L.debug(R.getString("D-MESSAGE_CONTEXT#0001"));
            throw new InternalException(MessageContext.class, "D-MESSAGE_CONTEXT#0001");
        }
    }

    /**
     * <p>
     * バリデーションメッセージをリクエストスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param replace プレースホルダーを置換するキーと値の{@link Map}
     * @param property プロパティ名
     * @param constraintName バリデータ名
     * @param modelName モデル名
     */
    public void saveValidationMessageToRequest(
            String code,
            Map<String, ? extends Object> replace,
            String property,
            String constraintName,
            String modelName) {
        Args.checkNotBlank(code);
        Messages messages = (Messages) request.getAttribute(VALIDATION_MESSAGE_KEY_TO_REQUEST);

        if (messages == null) messages = new Messages(MessageType.VALIDATION);
        messages.add(new Message(code, replace, property, constraintName, modelName));
        request.setAttribute(VALIDATION_MESSAGE_KEY_TO_REQUEST, messages);
    }

    /**
     * 入力チェックエラー・メッセージをフラッシュスコープにストアします。
     * @param code メッセージ・コード
     * @param property プロパティ名
     * @param constraintName 入力チェック名
     * @param modelName モデル名
     */
    public void saveValidationMessageToFlash(String code, String property, String constraintName, String modelName) {
        saveValidationMessageToFlash(code, null, property, constraintName, modelName);
    }

    /**
     * 入力チェックエラー・メッセージをフラッシュスコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     * @param property プロパティ名
     * @param constraintName 入力チェック名
     * @param modelName モデル名
     */
    public void saveValidationMessageToFlash(
            String code,
            Map<String, ? extends Object> replace,
            String property,
            String constraintName,
            String modelName) {

        Args.checkNotNull(code);
        Messages messages = (Messages)RequestContextUtils.getOutputFlashMap(request).get(VALIDATION_MESSAGE_KEY_TO_FLASH);
        if (messages == null) {
            messages = new Messages(MessageType.VALIDATION);
        }
        messages.add(new Message(code, replace, property, constraintName, modelName));
        RequestContextUtils.getOutputFlashMap(request).put(VALIDATION_MESSAGE_KEY_TO_FLASH, messages);
    }

    /**
     * インフォメーション・メッセージをリクエスト・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveInformationMessageToRequest(String code) {
        saveInformationMessageToRequest(code, null);
    }

    /**
     * インフォメーション・メッセージをリクエスト・スコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveInformationMessageToRequest(
            String code,
            Map<String, ? extends Object> replace) {
        Args.checkNotBlank(code);
        Messages messages = (Messages) request.getAttribute(INFORMATION_MESSAGE_KEY_TO_REQUEST);
        if (messages == null) messages = new Messages(MessageType.INFORMATION);
        messages.add(new Message(code, replace));
        request.setAttribute(INFORMATION_MESSAGE_KEY_TO_REQUEST, messages);
    }

    /**
     * インフォメーション・メッセージをフラッシュ・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveInformationMessageToFlash(String code) {
        saveInformationMessageToFlash(code, null);
    }

    /**
     * インフォメーション・メッセージをフラッシュ・スコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveInformationMessageToFlash(
            String code,
            Map<String, ? extends Object> replace) {
        Args.checkNotNull(code);
        Messages messages = (Messages)RequestContextUtils.getOutputFlashMap(request).get(INFORMATION_MESSAGE_KEY_TO_FLASH);
        if (messages == null) {
            messages = new Messages(MessageType.INFORMATION);
            RequestContextUtils.getOutputFlashMap(request).put(INFORMATION_MESSAGE_KEY_TO_FLASH, messages);
        }
        messages.add(new Message(code, replace));
    }

    /**
     * インフォメーション・メッセージをセッション・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveInformationMessageToSession(String code) {
        saveInformationMessageToSession(code, null);
    }

    /**
     * インフォメーション・メッセージをセッション・スコープにストアします。
     * セッションが存在しない場合はセッションを開始します。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveInformationMessageToSession(String code, Map<String, ? extends Object> replace) {
        Args.checkNotBlank(code);
        HttpSession session = request.getSession(true);
        Messages messages = (Messages) session.getAttribute(INFORMATION_MESSAGE_KEY_TO_SESSION);
        if (messages == null) messages = new Messages(MessageType.INFORMATION);
        messages.add(new Message(code, replace));
        session.setAttribute(INFORMATION_MESSAGE_KEY_TO_SESSION, messages);
    }

    /**
     * <p>
     * インフォメーション・メッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param scope 保存するスコープ
     */
    public void saveInformationMessage(String code, Scope scope) {
        saveInformationMessage(code, null, scope);
    }

    /**
     * <p>
     * インフォメーション・メッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     * @param scope 保存するスコープ
     */
    public void saveInformationMessage(String code, Map<String, ? extends Object> replace, Scope scope) {
        Args.checkNotNull(scope);
        switch (scope) {
        case FLASH:
            saveInformationMessageToFlash(code, replace);
            break;
        case SESSION:
            saveInformationMessageToSession(code, replace);
            break;
        case REQUEST:
        default:
            saveInformationMessageToRequest(code, replace);
            break;
        }
    }

    /**
     * <p>
     * エラー・メッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param scope 保存するスコープ
     */
    public void saveErrorMessage(String code, Scope scope) {
        saveErrorMessage(code, null, scope);
    }

    /**
     * <p>
     * エラー・メッセージを指定されたスコープに保存します。<br/>
     * </p>
     * @param code メッセージを指定するキー
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     * @param scope 保存するスコープ
     */
    public void saveErrorMessage(String code, Map<String, ? extends Object> replace, Scope scope) {
        Args.checkNotNull(scope);
        switch (scope) {
        case FLASH:
            saveErrorMessageToFlash(code, replace);
            break;
        case SESSION:
            saveErrorMessageToSession(code, replace);
            break;
        case REQUEST:
        default:
            saveErrorMessageToRequest(code, replace);
            break;
        }
    }

    /**
     * ストアされているインフォメーション・メッセージをクリアします。
     */
    public void clearInformationMessage() {
        MessageType type = MessageType.INFORMATION;
        clearForRequest(INFORMATION_MESSAGE_KEY_TO_REQUEST, null, type);
        clearForFlash(INFORMATION_MESSAGE_KEY_TO_FLASH, null, type);
        clearForSession(INFORMATION_MESSAGE_KEY_TO_SESSION, null, type);
    }
    
    /**
     * ストアされているインフォメーション・メッセージをクリアします。
     * このメソッドは指定されたキーが格納されているスコープは関係なく、削除します。
     * @param code メッセージを指定するキー
     */
    public void clearInformationMessage(String code) {
        MessageType type = MessageType.INFORMATION;
        clearForRequest(INFORMATION_MESSAGE_KEY_TO_REQUEST, code, type); 
        clearForFlash(INFORMATION_MESSAGE_KEY_TO_FLASH, code, type);
        clearForSession(INFORMATION_MESSAGE_KEY_TO_SESSION, code, type);
    }

    /**
     * エラー・メッセージをリクエスト・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveErrorMessageToRequest(String code) {
        saveErrorMessageToRequest(code, null);
    }

    /**
     * エラー・メッセージをリクエスト・スコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveErrorMessageToRequest(
            String code,
            Map<String, ? extends Object> replace) {
        Args.checkNotBlank(code);
        Messages messages = (Messages)request.getAttribute(ERROR_MESSAGE_KEY_TO_REQUEST);
        if (messages == null) messages = new Messages(MessageType.ERROR);
        messages.add(new Message(code, replace));
        request.setAttribute(ERROR_MESSAGE_KEY_TO_REQUEST, messages);
    }

    /**
     * エラー・メッセージをフラッシュ・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveErrorMessageToFlash(String code) {
        saveErrorMessageToFlash(code, null);
    }

    /**
     * エラー・メッセージをフラッシュ・スコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveErrorMessageToFlash(
            String code,
            Map<String, ? extends Object> replace) {

        Args.checkNotBlank(code);
        Messages messages = (Messages)RequestContextUtils.getOutputFlashMap(request).get(ERROR_MESSAGE_KEY_TO_FLASH);
        if (messages == null) {
            messages = new Messages(MessageType.ERROR);
            RequestContextUtils.getOutputFlashMap(request).put(ERROR_MESSAGE_KEY_TO_FLASH, messages);
        }
        messages.add(new Message(code, replace));
    }

    /**
     * エラー・メッセージをセッション・スコープにストアします。
     * @param code メッセージ・コード
     */
    public void saveErrorMessageToSession(String code) {
        saveErrorMessageToSession(code, null);
    }

    /**
     * エラー・メッセージをセッション・スコープにストアします。
     * @param code メッセージ・コード
     * @param replace メッセージのプレースホルダを置換するキーとバリューの{@link Map}
     */
    public void saveErrorMessageToSession(String code, Map<String, ? extends Object> replace) {
        Args.checkNotBlank(code);
        HttpSession session = request.getSession(true);
        Messages messages = (Messages)session.getAttribute(ERROR_MESSAGE_KEY_TO_SESSION);
        if (messages == null) messages = new Messages(MessageType.ERROR);
        messages.add(new Message(code, replace));
        session.setAttribute(ERROR_MESSAGE_KEY_TO_SESSION, messages);
    }

    /**
     * ストアされているエラー・メッセージをクリアします。
     */
    public void clearErrorMessage() {
        MessageType type = MessageType.ERROR;
        clearForRequest(ERROR_MESSAGE_KEY_TO_REQUEST, null, type);
        clearForFlash(ERROR_MESSAGE_KEY_TO_FLASH, null, type);
        clearForSession(ERROR_MESSAGE_KEY_TO_SESSION, null, type);
    }
    
    /**
     * ストアされているエラー・メッセージをクリアします。
     * このメソッドは指定されたキーが格納されているスコープは関係なく、削除します。
     * @param code メッセージを指定するキー
     */
    public void clearErrorMessage(String code) {
        MessageType type = MessageType.ERROR;
        clearForRequest(ERROR_MESSAGE_KEY_TO_REQUEST, code, type);
        clearForFlash(ERROR_MESSAGE_KEY_TO_FLASH, code, type);
        clearForSession(ERROR_MESSAGE_KEY_TO_SESSION, code, type);
    }

    /**
     * 指定されたスコープにストアされているインフォメーション・・メッセージをクリアします。
     * @param scope スコープ
     */
    public void clearInformationMessage(Scope scope) {
        Args.checkNotNull(scope);
        MessageType type = MessageType.INFORMATION;
        switch (scope) {
        case FLASH:
            clearForFlash(INFORMATION_MESSAGE_KEY_TO_FLASH, null, type);
            break;
        case SESSION:
            clearForSession(INFORMATION_MESSAGE_KEY_TO_SESSION, null, type);
            break;
        case REQUEST:
        default:
            clearForRequest(INFORMATION_MESSAGE_KEY_TO_REQUEST, null, type);
            break;
        }
    }

    /**
     * 指定されたスコープにストアされているエラー・メッセージをクリアします。
     * @param scope スコープ
     */
    public void clearErrorMessage(Scope scope) {
        Args.checkNotNull(scope);
        MessageType type = MessageType.ERROR;
        switch (scope) {
        case FLASH:
            clearForFlash(ERROR_MESSAGE_KEY_TO_FLASH, null, type);
            break;
        case SESSION:
            clearForSession(ERROR_MESSAGE_KEY_TO_SESSION, null, type);
            break;
        case REQUEST:
        default:
            clearForRequest(ERROR_MESSAGE_KEY_TO_REQUEST, null, type);
            break;
        }
    }
    
    private void clearForRequest(String key, String code, MessageType type) {
        Messages messages = (Messages) request.getAttribute(key);
        if (messages == null) return;
        if (Strings.isEmpty(code)) {
            request.removeAttribute(key);
        } else {
            messages.remove(code);
            request.setAttribute(key, messages);
        }
    }

    private void clearForFlash(String key, String code, MessageType type) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        Messages messages = (Messages) flashMap.get(key);
        if (messages == null) return;
        
        if (Strings.isEmpty(code)) {
            flashMap.remove(key);
        } else {
            messages.remove(code);
            flashMap.put(key, messages);
        }
    }

    private void clearForSession(String key, String code, MessageType type) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        Messages messages = (Messages) session.getAttribute(key);
        if (messages == null) return;
        if (Strings.isEmpty(code)) {
            session.removeAttribute(key);
        } else {
            messages.remove(code);
            session.setAttribute(key, messages);
        }
    }

    /**
     * 現在のリクエストで有効な{@code MessageContext} インスタンスを返却します。
     * @param request {@link HttpServletRequest} インスタンス
     * @return 現在のリクエストで有効な{@code MessageContext} インスタンス
     */
    public static MessageContext getCurrentMessageContext(HttpServletRequest request) {
        MessageContext context = (MessageContext)request.getAttribute(MessageContext.MESSAGE_CONTEXT_ATTRIBUTE_KEY);
        if (context == null) context = new MessageContext(request);
        return context;
    }
}
