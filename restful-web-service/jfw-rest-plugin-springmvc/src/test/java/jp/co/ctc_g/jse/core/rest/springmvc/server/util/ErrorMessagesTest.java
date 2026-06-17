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

package jp.co.ctc_g.jse.core.rest.springmvc.server.util;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.CoreMatchers;
import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ErrorMessagesTest {

    @Test
    public void ランダムなIDが設定される() {
        ErrorMessage error = ErrorMessages.create().id().get();
        assertThat(error.getId(), CoreMatchers.notNullValue());
    }

    @Test
    public void 指定したIDが設定される() {
        ErrorMessage error = ErrorMessages.create().id("12345").get();
        assertThat(error.getId(), CoreMatchers.notNullValue());
        assertThat(error.getId(), CoreMatchers.is("12345"));
    }

    @Test
    public void 指定したHTTPステータスでインスタンスが生成される() {
        ErrorMessage error = ErrorMessages.create(HttpStatus.NOT_FOUND).get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getStatus(), CoreMatchers.is(404));
        assertThat(error.getMessage(), CoreMatchers.is(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void 指定したHTTPステータスが設定される() {
        ErrorMessage error = ErrorMessages.create().status(HttpStatus.INTERNAL_SERVER_ERROR).get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getStatus(), CoreMatchers.is(500));
        assertThat(error.getMessage(), CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }

    @Test
    public void 指定したHTTPステータスのみが設定される() {
        ErrorMessage error = ErrorMessages.create().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getStatus(), CoreMatchers.is(500));
        assertThat(error.getMessage(), CoreMatchers.nullValue());
    }

    @Test
    public void エラーメッセージが設定される() {
        ErrorMessage error = ErrorMessages.create().message("メッセージを指定").get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getMessage(), CoreMatchers.is("メッセージを指定"));
    }

    @Test
    public void エラーコードが設定される() {
        ErrorMessage error = ErrorMessages.create().code("E-#999").get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getCode(), CoreMatchers.is("E-#999"));
    }

    @Test
    public void バリデーションメッセージが設定される() {
        ErrorMessage error = ErrorMessages.create().bind("id", "必須です。").get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getValidationMessages(), CoreMatchers.notNullValue());
        assertThat(error.getValidationMessages().size(), CoreMatchers.is(1));
        assertThat(error.getValidationMessages().get(0).getPath(), CoreMatchers.is("id"));
        assertThat(error.getValidationMessages().get(0).getMessage(), CoreMatchers.is("必須です。"));
    }

    @Test
    public void BindingResultでバリデーションメッセージが設定される() {
        class TargetBean {
        }
        TargetBean t = new TargetBean();
        BindingResult bind = new BeanPropertyBindingResult(t, "bean");
        bind.addError(new FieldError("bean", "id", "必須です。"));
        ErrorMessage error = ErrorMessages.create().bind(bind).get();
        assertThat(error, CoreMatchers.notNullValue());
        assertThat(error.getValidationMessages(), CoreMatchers.notNullValue());
        assertThat(error.getValidationMessages().size(), CoreMatchers.is(1));
        assertThat(error.getValidationMessages().get(0).getPath(), CoreMatchers.is("bean.id"));
        assertThat(error.getValidationMessages().get(0).getMessage(), CoreMatchers.is("必須です。"));
    }

}
