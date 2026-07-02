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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ErrorMessageTest {

    public static class ErrorMessageConvertTest {

        protected ObjectMapper mapper;
        protected ErrorMessage error;

        @Before
        public void setup() {
            mapper = new ObjectMapper();
            error = new ErrorMessage();
            error.setStatus(400);
            error.setMessage("Bad Request");
        }

        @Test
        public void プロパティ値がNULLの場合はJSON形式に含まれない() throws Exception {
            String value = mapper.writeValueAsString(error);
            assertThat(value, notNullValue());
            assertThat(value, is("{\"status\":400,\"message\":\"Bad Request\"}"));
        }

        @Test
        public void デフォルトのログメッセージが取得できる() {
            error.setCode("E-TEST#0001");
            error.setId("abcdefg");
            String log = error.log();
            assertThat(log, notNullValue());
            assertThat(log, is("400 E-TEST#0001 abcdefg Bad Request "));
        }

        @Test
        public void ログ出力対象のプロパティが設定されていない場合は空文字列を返す() {
            error.setStatus(-1);
            error.setCode(null);
            error.setId(null);
            error.setMessage(null);
            String log = error.log();
            assertThat(log, notNullValue());
            assertThat(log, is(""));
        }

        @Test
        public void すべての値がJSON形式にコンバートできる() throws Exception {
            error.setCode("12345");
            String uid = UUID.randomUUID().toString();
            error.setId(uid);
            String value = mapper.writeValueAsString(error);
            assertThat(value, notNullValue());
            assertThat(value, is("{\"id\":\"" + uid
                + "\",\"status\":400,\"code\":\"12345\",\"message\":\"Bad Request\"}"));
        }
    }

    public static class ValidationMessageConvertTest {

        protected ObjectMapper mapper;
        protected ErrorMessage error;

        @Before
        public void setup() {
            mapper = new ObjectMapper();
            error = new ErrorMessage();
            error.setStatus(400);
            error.setMessage("Bad Request");
            List<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
            msgs.add(new ValidationMessage("id", "必須です。"));
            msgs.add(new ValidationMessage("name", "100文字以内で入力してください。"));
            error.setValidationMessages(msgs);
        }

        @Test
        public void バリデーションエラーのメッセージをコンバートできる() throws Exception {
            String value = mapper.writeValueAsString(error);
            assertThat(value, notNullValue());
            assertThat(value,
                       is("{\"status\":400,\"message\":\"Bad Request\",\"validationMessages\":[{\"path\":\"id\",\"message\":\"必須です。\"},{\"path\":\"name\",\"message\":\"100文字以内で入力してください。\"}]}"));
        }
    }

}
