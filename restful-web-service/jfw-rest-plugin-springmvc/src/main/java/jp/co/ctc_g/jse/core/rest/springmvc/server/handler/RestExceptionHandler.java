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

import jp.co.ctc_g.jfw.core.exception.ApplicationRecoverableException;
import jp.co.ctc_g.jfw.core.exception.ApplicationUnrecoverableException;
import jp.co.ctc_g.jfw.core.exception.SystemException;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * <p>
 * このインタフェースは、REST APIの例外ハンドリングのインタフェースです。
 * </p>
 * <p>
 * ここで指定された例外が発生した際はこの例外ハンドラにて例外を捕捉し、
 * 任意のオブジェクトのインスタンスを構築します。
 * </p>
 * @see AbstractRestExceptionHandler
 * @author ITOCHU Techno-Solutions Corporation.
 * @param <T> 任意の型
 */
public interface RestExceptionHandler<T> {

    /**
     * {@link HttpRequestMethodNotSupportedException}をハンドリングします。
     * @param e {@link HttpRequestMethodNotSupportedException}
     * @return 任意の型
     */
    T handle(HttpRequestMethodNotSupportedException e);

    /**
     * {@link NoHandlerFoundException}をハンドリングします。
     * @param e {@link NoHandlerFoundException}
     * @return 任意の型
     */
    T handle(NoHandlerFoundException e);

    /**
     * {@link HttpMediaTypeNotSupportedException}をハンドリングします。
     * @param e {@link HttpMediaTypeNotSupportedException}
     * @return 任意の型
     */
    T handle(HttpMediaTypeNotSupportedException e);

    /**
     * {@link HttpMediaTypeNotAcceptableException}をハンドリングします。
     * @param e {@link HttpMediaTypeNotAcceptableException}
     * @return 任意の型
     */
    T handle(HttpMediaTypeNotAcceptableException e);

    /**
     * {@link MissingServletRequestParameterException}をハンドリングします。
     * @param e {@link MissingServletRequestParameterException}
     * @return 任意の型
     */
    T handle(MissingServletRequestParameterException e);

    /**
     * {@link ServletRequestBindingException}をハンドリングします。
     * @param e {@link ServletRequestBindingException}
     * @return 任意の型
     */
    T handle(ServletRequestBindingException e);

    /**
     * {@link ConversionNotSupportedException}をハンドリングします。
     * @param e {@link ConversionNotSupportedException}
     * @return 任意の型
     */
    T handle(ConversionNotSupportedException e);

    /**
     * {@link TypeMismatchException}をハンドリングします。
     * @param e {@link TypeMismatchException}
     * @return 任意の型
     */
    T handle(TypeMismatchException e);

    /**
     * {@link HttpMessageNotReadableException}をハンドリングします。
     * @param e {@link HttpMessageNotReadableException}
     * @return 任意の型
     */
    T handle(HttpMessageNotReadableException e);

    /**
     * {@link HttpMessageNotWritableException}をハンドリングします。
     * @param e {@link HttpMessageNotWritableException}
     * @return 任意の型
     */
    T handle(HttpMessageNotWritableException e);

    /**
     * {@link MissingServletRequestPartException}をハンドリングします。
     * @param e {@link MissingServletRequestPartException}
     * @return 任意の型
     */
    T handle(MissingServletRequestPartException e);

    /**
     * {@link MethodArgumentNotValidException}をハンドリングします。
     * @param e {@link MethodArgumentNotValidException}
     * @return 任意の型
     */
    T handle(MethodArgumentNotValidException e);

    /**
     * {@link BindException}をハンドリングします。
     * @param e {@link BindException}
     * @return 任意の型
     */
    T handle(BindException e);

    /**
     * {@link HttpServerErrorException}をハンドリングします。
     * @param e {@link HttpServerErrorException}
     * @return 任意の型
     */
    T handle(HttpServerErrorException e);

    /**
     * {@link HttpClientErrorException}をハンドリングします。
     * @param e {@link HttpClientErrorException}
     * @param headers HTTPヘッダ
     * @return 任意の型
     */
    ResponseEntity<T> handle(HttpClientErrorException e, HttpHeaders headers);

    /**
     * {@link ApplicationRecoverableException}をハンドリングします。
     * @param e {@link ApplicationRecoverableException}
     * @return 任意の型
     */
    T handle(ApplicationRecoverableException e);
    
    /**
     * {@link ApplicationUnrecoverableException}をハンドリングします。
     * @param e {@link ApplicationUnrecoverableException}
     * @return 任意の型
     */
    T handle(ApplicationUnrecoverableException e);
    
    /**
     * {@link SystemException}をハンドリングします。
     * @param e {@link SystemException}
     * @return 任意の型
     */
    T handle(SystemException e);
    

    /**
     * {@link java.lang.Throwable} をハンドリングします。
     * @param e {@link java.lang.Throwable}
     * @return 任意の型
     */
    T handle(Throwable e);

    /**
     * 警告ログ出力の拡張ポイントです。
     * @param entity 任意の方
     * @param e 例外
     */
    void warn(T entity, Throwable e);

    /**
     * エラーログ出力の拡張ポイントです。
     * @param entity 任意の方
     * @param e 例外
     */
    void error(T entity, Throwable e);
}
