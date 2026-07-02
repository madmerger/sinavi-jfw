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

package jp.co.ctc_g.jse.core.rest.springmvc.server.handler;

import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.exception.ApplicationRecoverableException;
import jp.co.ctc_g.jfw.core.exception.ApplicationUnrecoverableException;
import jp.co.ctc_g.jfw.core.exception.SystemException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;
import jp.co.ctc_g.jse.core.rest.springmvc.server.util.ErrorCode;
import jp.co.ctc_g.jse.core.rest.springmvc.server.util.ErrorMessages;
import jp.co.ctc_g.jse.core.rest.springmvc.server.util.ErrorResources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * <p>
 * このクラスは、REST APIの例外ハンドリングを実現します。
 * </p>
 * <p>
 * SpringFramework内部で発生する例外やフレームワークで提供している例外などを補捉し、
 * {@link ErrorMessage}にデータを設定し、レスポンスデータを構築します。
 * また、例外の種類に応じて警告ログやエラーログを出力します。
 * 以下に捕捉する例外と発生因、ログレベル、レスポンスのステータスコードを示します。
 * <table>
 *  <thead>
 *   <tr>
 *    <th>例外の種類</th>
 *    <th>例外が発生する原因</th>
 *    <th>ログレベル</th>
 *    <th>レスポンスのステータスコード</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>{@link MissingServletRequestParameterException}</td>
 *    <td>リクエストされたパラメータが不足しているときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link ServletRequestBindingException}</td>
 *    <td>サーブレットのリクエストデータのバインドに失敗したときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link TypeMismatchException}</td>
 *    <td>プロパティの型とリクエストデータの型が一致しないときにスローされます。例えば、数値型のプロパティへ文字列をバイドしようとしたときです。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpMessageNotReadableException}</td>
 *    <td>MessageConverterが読み込めない形式のリクエストのときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link MissingServletRequestPartException}</td>
 *    <td>multipart/form-dataのリクエストの一部が見つからないときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link BindException}</td>
 *    <td>バリデーションエラーが発生したときにスローされます。</td>
 *    <td>-</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link MethodArgumentNotValidException}</td>
 *    <td><code>@Valid</code>が付与されていないときスローされます。</td>
 *    <td>-</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link ApplicationRecoverableException}</td>
 *    <td>回復可能なアプリケーションエラーのときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>400</td>
 *   </tr>
 *   <tr>
 *    <td>{@link NoHandlerFoundException}</td>
 *    <td>対象のURLで起動するメソッドが存在しないときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>404</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpRequestMethodNotSupportedException}</td>
 *    <td>サポートしていないメソッドをリクエストされたときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>405</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpMediaTypeNotAcceptableException}</td>
 *    <td>リクエストされたメディアタイプを許容できないときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>406</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpMediaTypeNotSupportedException}</td>
 *    <td>サポートしていないメディアタイプをリクエストされたときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>415</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpClientErrorException}</td>
 *    <td>REST Clientでクライアントサイドのエラーが返されたときにスローされます。</td>
 *    <td>WARN</td>
 *    <td>4xx(発生したステータスコード)</td>
 *   </tr>
 *   <tr>
 *    <td>{@link ConversionNotSupportedException}</td>
 *    <td>プロパティに対応するPropertyEditorやMessageConverterが見つからないときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpMessageNotWritableException}</td>
 *    <td>MessageConverterが書き込めない形式のレスポンスのときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *   <tr>
 *    <td>{@link HttpServerErrorException}</td>
 *    <td>REST Clientでサーバサイドのエラーが返されたときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *   <tr>
 *    <td>{@link ApplicationUnrecoverableException}</td>
 *    <td>回復不可能なアプリケーションエラーのときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *   <tr>
 *    <td>{@link SystemException}</td>
 *    <td>システムエラーのときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *   <tr>
 *    <td>{@link Throwable}</td>
 *    <td>予期しない例外が発生したときにスローされます。</td>
 *    <td>ERROR</td>
 *    <td>500</td>
 *   </tr>
 *  </tbody>
 * </table>
 * </p>
 * <p>
 * この例外ハンドラを利用する場合は、 この抽象クラスを拡張したクラスを実装し、DIコンテナへ登録してください。
 * フレームワークがデフォルトで提供している例外ハンドラは{@link RestDefaultExceptionHandler}です。
 * 以下が設定例です。
 * </p>
 * <pre>
 * &lt;bean id="compositeExceptionResolver" class="org.springframework.web.servlet.handler.HandlerExceptionResolverComposite"&gt;
 *   &lt;property name="exceptionResolvers"&gt;
 *     &lt;list&gt;
 *       &lt;bean class="jp.co.ctc_g.jse.core.rest.springmvc.server.handler.RestDefaultExceptionHandler"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p>
 * また、拡張したクラスへ{@link org.springframework.web.bind.annotation.ControllerAdvice}アノテーションを付与し、
 * コンポーネントスキャンの対象とすることで、本ハンドラを有効にすることも可能です。
 * </p>
 * <pre>
 * &#064;ControllerAdvice
 * public class RestDefaultExceptionHandler extends AbstractRestExceptionHandler {
 *   // 省略
 * }
 * </pre>
 * @see RestExceptionHandler
 * @author ITOCHU Techno-Solutions Corporation.
 */
public abstract class AbstractRestExceptionHandler implements RestExceptionHandler<ErrorMessage> {

    private static final Logger L = LoggerFactory.getLogger(AbstractRestExceptionHandler.class);
    private static final ResourceBundle R = InternalMessages.getBundle(AbstractRestExceptionHandler.class);

    /**
     * デフォルトコンストラクタです。
     */
    public AbstractRestExceptionHandler() {}

    /**
     * {@link NoHandlerFoundException}をハンドリングします。
     * @param e {@link NoHandlerFoundException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 404 でレスポンスを返却します。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @Override
    public ErrorMessage handle(NoHandlerFoundException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0001"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.NOT_FOUND);
        warn(error, e);
        return error;
    }

    /**
     * {@link HttpRequestMethodNotSupportedException}をハンドリングします。
     * @param e {@link HttpRequestMethodNotSupportedException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 405 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @Override
    public ErrorMessage handle(HttpRequestMethodNotSupportedException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0002"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.METHOD_NOT_ALLOWED);
        warn(error, e);
        return error;
    }

    /**
     * {@link HttpMediaTypeNotSupportedException}をハンドリングします。
     * @param e {@link HttpMediaTypeNotSupportedException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 415 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @Override
    public ErrorMessage handle(HttpMediaTypeNotSupportedException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0003"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        warn(error, e);
        return error;
    }

    /**
     * {@link HttpMediaTypeNotAcceptableException}をハンドリングします。
     * @param e {@link HttpMediaTypeNotAcceptableException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 406 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    @Override
    public ErrorMessage handle(HttpMediaTypeNotAcceptableException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0004"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.NOT_ACCEPTABLE);
        warn(error, e);
        return error;
    }

    /**
     * {@link MissingServletRequestParameterException}をハンドリングします。
     * @param e {@link MissingServletRequestParameterException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(MissingServletRequestParameterException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0005"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.BAD_REQUEST);
        warn(error, e);
        return error;
    }

    /**
     * {@link ServletRequestBindingException}をハンドリングします。
     * @param e {@link ServletRequestBindingException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(ServletRequestBindingException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0006"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.BAD_REQUEST);
        warn(error, e);
        return error;
    }

    /**
     * {@link ConversionNotSupportedException}をハンドリングします。
     * @param e {@link ConversionNotSupportedException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(ConversionNotSupportedException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0007"), e);
        }
        ErrorMessage error = createServerErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR);
        error(error, e);
        return error;
    }

    /**
     * {@link TypeMismatchException}をハンドリングします。
     * @param e {@link TypeMismatchException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(TypeMismatchException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0008"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.BAD_REQUEST);
        warn(error, e);
        return error;
    }

    /**
     * {@link HttpMessageNotReadableException}をハンドリングします。
     * @param e {@link HttpMessageNotReadableException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(HttpMessageNotReadableException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0009"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.BAD_REQUEST);
        warn(error, e);
        return error;
    }

    /**
     * {@link HttpMessageNotWritableException}をハンドリングします。
     * @param e {@link HttpMessageNotWritableException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(HttpMessageNotWritableException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0010"), e);
        }
        ErrorMessage error = createServerErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR);
        error(error, e);
        return error;
    }

    /**
     * {@link MissingServletRequestPartException}をハンドリングします。
     * @param e {@link MissingServletRequestPartException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(MissingServletRequestPartException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0011"), e);
        }
        ErrorMessage error = createClientErrorMessage(HttpStatus.BAD_REQUEST);
        warn(error, e);
        return error;
    }
    
    /**
     * {@link MethodArgumentNotValidException}をハンドリングします。
     * @param e {@link MethodArgumentNotValidException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(MethodArgumentNotValidException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0012"), e);
        }
        return createValidationErrorMessage(HttpStatus.BAD_REQUEST, e.getBindingResult());
    }

    /**
     * {@link BindException}をハンドリングします。
     * @param e {@link BindException}
     * @return {@link ErrorMessage}
     *         HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(BindException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0013"), e);
        }
        return createValidationErrorMessage(HttpStatus.BAD_REQUEST, e.getBindingResult());
    }

    /**
     * {@link HttpClientErrorException}をハンドリングします。
     * @param e {@link HttpClientErrorException}
     * @param headers HTTPヘッダー
     * @return {@link ErrorMessage}
     *         REST Clientで返されたHTTPステータスを返却します。
     */
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    @Override
    public ResponseEntity<ErrorMessage> handle(HttpClientErrorException e, HttpHeaders headers) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0014"), e);
        }
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ErrorMessage error = createServerErrorMessage(status);
        warn(error, e);
        return new ResponseEntity<ErrorMessage>(error, headers, status);
    }

    /**
     * {@link HttpServerErrorException}をハンドリングします。
     * @param e {@link HttpServerErrorException}
     * @return {@link ErrorMessage}
     *      HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(HttpServerErrorException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0015"), e);
        }
        ErrorMessage error = createServerErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR);
        error(error, e);
        return error;
    }

    /**
     * {@link Throwable}をハンドリングします。
     * @param e {@link Throwable}
     * @return {@link ErrorMessage}
     *      HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(Throwable e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0016"), e);
        }
        ErrorMessage error = createServerErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR);
        error(error, e);
        return error;
    }

    /**
     * {@link ApplicationRecoverableException}をハンドリングします。
     * @param e {@link ApplicationRecoverableException}
     * @return {@link ErrorMessage}
     *      HTTPステータス 400 でレスポンスを返却します。
     */
    @ExceptionHandler(ApplicationRecoverableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Override
    public ErrorMessage handle(ApplicationRecoverableException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0017"), e);
        }
        ErrorMessage error = ErrorMessages.create(HttpStatus.BAD_REQUEST)
            .message(e.getMessage())
            .code(e.getCode())
            .get();
        warn(error, e);
        return error;
    }

    /**
     * {@link ApplicationUnrecoverableException}をハンドリングします。
     * @param e {@link ApplicationUnrecoverableException}
     * @return {@link ErrorMessage}
     *      HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(ApplicationUnrecoverableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(ApplicationUnrecoverableException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0018"), e);
        }
        ErrorMessage error = ErrorMessages.create(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(e.getMessage())
            .id(e.getId())
            .code(e.getCode())
            .get();
        error(error, e);
        return error;
    }

    /**
     * {@link SystemException}をハンドリングします。
     * @param e {@link SystemException}
     * @return {@link ErrorMessage}
     *      HTTPステータス 500 でレスポンスを返却します。
     */
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @Override
    public ErrorMessage handle(SystemException e) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-SPRINGMVC-REST-HANDLER#0019"), e);
        }
        ErrorMessage error = ErrorMessages.create(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(e.getMessage())
            .id(e.getId())
            .code(e.getCode())
            .get();
        error(error, e);
        return error;
    }

    /**
     * クライアントサイドのエラーコードを取得します。
     * @param status HTTPステータス
     * @return エラーコード
     */
    abstract String getClientErrorCode(HttpStatus status);

    /**
     * サーバサイドのエラーコードを取得します。
     * @param status HTTPステータス
     * @return エラーコード
     */
    abstract String getServerErrorCode(HttpStatus status);
    
    /**
     * クライアントエラーの{@link ErrorMessage}のインスタンスを生成します。
     * @param status HTTPステータス
     * @return {@link ErrorMessage}
     */
    protected ErrorMessage createClientErrorMessage(HttpStatus status){
        return ErrorMessages.create(status)
            .message(ErrorResources.find(ErrorCode.get(status).code()))
            .code(getClientErrorCode(status))
            .get();
    }
    
    /**
     * バリデーションエラーのメッセージオブジェクトのインスタンスを生成します。
     * @param status HTTPステータス
     * @param binding バリデーションエラー
     * @return {@link ErrorMessage}
     */
    protected ErrorMessage createValidationErrorMessage(HttpStatus status, BindingResult binding) {
        return ErrorMessages.create(status)
            .message(ErrorResources.find(ErrorCode.get(status).code()))
            .bind(binding)
            .get();
    }
    
    /**
     * サーバエラーの{@link ErrorMessage}のインスタンスを生成します。
     * @param status HTTPステータス
     * @return {@link ErrorMessage}
     */
    protected ErrorMessage createServerErrorMessage(HttpStatus status){
        return ErrorMessages.create(status)
            .message(ErrorResources.find(ErrorCode.get(status).code()))
            .id()
            .code(getServerErrorCode(status))
            .get();
    }
}
