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

import java.util.Map;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.util.ErrorHandler;

/**
 * <p>
 * このクラスは、メッセージ受信側で発生した例外をハンドリングするための基底クラスです。
 * </p>
 * <p>
 * このクラスは、メッセージ受信側で発生した例外をハンドリングするための基底クラスで、
 * 例外の型に応じて、リトライ対象とするかどうかを設定するマッピング情報を保持しています。
 * 例外を引き起こす原因となった例外とマッピング情報から
 * 警告ログを出力するかエラーログを出力するかどうかを判断します。
 * </p>
 * <p>
 * また、このクラスは{@link org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer#invokeErrorHandler}より実行されます。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public abstract class AbstractAmqpErrorHandler implements ErrorHandler {

    private static final Logger L = LoggerFactory.getLogger(AbstractAmqpErrorHandler.class);
    private static final ResourceBundle R = InternalMessages.getBundle(AbstractAmqpErrorHandler.class);

    private BinaryExceptionClassifier retryableClassifier = new BinaryExceptionClassifier(false);

    /**
     * デフォルトコンストラクタです。
     */
    public AbstractAmqpErrorHandler() {}

    /**
     * メッセージ受信側で発生した例外をハンドリングし、
     * リトライ可能な対象の例外のときは警告ログ出力を、その他の例外のときはエラーログ出力を実行します。
     * @param throwable 発生した例外(Amqpを利用すると{@link org.springframework.amqp.rabbit.support.ListenerExecutionFailedException}にラップされます。)
     */
    @Override
    public void handleError(Throwable throwable) {
        if (classify(throwable)) {
            if (L.isDebugEnabled()) L.debug(R.getString("D-AMQP-RETRY#0001"));
            warn(throwable);
        } else {
            if (L.isDebugEnabled()) L.debug(R.getString("D-AMQP-RETRY#0002"));
            error(throwable);
        }
    }

    /**
     * リトライ対象のマッピング情報を設定します。
     * @param exceptions マッピング情報
     */
    public void setRetryableExceptions(Map<Class<? extends Throwable>, Boolean> exceptions) {
        this.retryableClassifier = new BinaryExceptionClassifier(exceptions);
        this.retryableClassifier.setTraverseCauses(true);
    }

    /**
     * リトライ対象のマッピング情報を取得します。
     * @return マッピング情報
     */
    public BinaryExceptionClassifier getRetryableClassifier() {
        return this.retryableClassifier;
    }

    /**
     * 警告ログを出力します。
     * ログの出力形式などを拡張できるように考慮し、抽象化しています。
     * @param t 例外情報
     */
    protected abstract void warn(Throwable t);

    /**
     * エラーログを出力します。
     * ログの出力形式などを拡張できるように考慮し、抽象化しています。
     * @param t 例外情報
     */
    protected abstract void error(Throwable t);

    private boolean classify(Throwable ex) {
        return retryableClassifier.classify(ex);
    }

}
