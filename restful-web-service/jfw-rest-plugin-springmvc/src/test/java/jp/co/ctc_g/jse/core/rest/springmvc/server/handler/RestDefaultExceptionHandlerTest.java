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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.Locale;

import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

public class RestDefaultExceptionHandlerTest {

    private RestDefaultExceptionHandler exceptionHandlerSupport;

    @BeforeAll
    public static void setupDefaultLocale() {
        Locale.setDefault(new Locale("ja", "JP"));
    }

    @BeforeEach
    public void setup() {
        this.exceptionHandlerSupport = new RestDefaultExceptionHandler();
    }

    @Test
    public void NoHandlerFoundExceptionをハンドリングできる() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/test", null);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(404));
        assertThat(message.getMessage(), is("リソースが見つかりません。"));
    }

    @Test
    public void testHttpRequestMethodNotSupportedExceptionをハンドリングできる() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(null);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(405));
        assertThat(message.getMessage(), is("リクエストが許可されていません。"));
    }

    @Test
    public void HttpMediaTypeNotSupportedExceptionをハンドリングできる() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(null);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(415));
        assertThat(message.getMessage(), is("サポートされていないメディアタイプです。"));
    }

    @Test
    public void HttpMediaTypeNotAcceptableExceptionをハンドリングできる() {
        HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException("message");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(406));
        assertThat(message.getMessage(), is("Accept関連のヘッダに受理できない内容が含まれています。"));
    }

    @Test
    public void MissingServletRequestParameterExceptionをハンドリングできる() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "type");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void ServletRequestBindingExceptionをハンドリングできる() {
        ServletRequestBindingException ex = new ServletRequestBindingException("message");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));

    }

    @Test
    public void ConversionNotSupportedExceptionをハンドリングできる() {
        ConversionNotSupportedException ex = new ConversionNotSupportedException(new Object(), Class.class, new Throwable());
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(500));
        assertThat(message.getMessage(), is("予期しない例外が発生しました。"));
    }

    @Test
    public void TypeMismatchExceptionをハンドリングできる() {
        TypeMismatchException ex = new TypeMismatchException(new Object(), Class.class);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void HttpMessageNotReadableExceptionをハンドリングできる() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("message");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void HttpMessageNotWritableExceptionをハンドリングできる() {
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException("message");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(500));
        assertThat(message.getMessage(), is("予期しない例外が発生しました。"));
    }

    @Test
    public void MissingServletRequestPartExceptionをハンドリングできる() {
        MissingServletRequestPartException ex = new MissingServletRequestPartException("partName");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void BindExceptionをハンドリングできる() {
        BindException ex = new BindException(new Object(), "name");
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void MethodArgumentNotValidExceptionをハンドリングできる() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(TestController.class.getMethod("foo", String.class), 1);
        BindingResult mock = Mockito.mock(BindingResult.class);
        Mockito.when(mock.hasFieldErrors()).thenReturn(true);
        Mockito.when(mock.getFieldErrors()).thenReturn(Arrays.asList(new FieldError("objectName", "name", "必須入力")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, mock);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(400));
        assertThat(message.getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void HttpClientErrorExceptionをハンドリングできる() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ErrorMessage> message = this.exceptionHandlerSupport.handle(ex, headers);
        assertThat(message, notNullValue());
        assertThat(message.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(message.getBody().getMessage(), is("不正なリクエストです。"));
    }

    @Test
    public void HttpServerErrorException() {
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorMessage message = this.exceptionHandlerSupport.handle(ex);
        assertThat(message, notNullValue());
        assertThat(message.getStatus(), is(500));
        assertThat(message.getMessage(), is("予期しない例外が発生しました。"));
    }

    public static class TestController {

        public void foo(String s) {}
    }
}