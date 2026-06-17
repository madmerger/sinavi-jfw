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

package jp.co.ctc_g.jse.core.rest.jersey.exception.mapper;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.hasItem;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;
import jp.co.ctc_g.jse.core.rest.entity.ValidationMessage;
import jp.co.ctc_g.jse.core.rest.jersey.exception.mapper.test_resource.Bar;
import jp.co.ctc_g.jse.core.rest.jersey.exception.mapper.test_resource.Hoge;
import jp.co.ctc_g.jse.core.rest.jersey.filter.LocaleContextFilter;
import jp.co.ctc_g.jse.core.rest.jersey.resolver.ValidationConfigurationContextResolver;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
// hamcrest removed;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// RunWith removed - use @ExtendWith or @Nested;

// @Nested classes used instead of Enclosed
public class ExceptionMapperTest {
    
    @BeforeAll
    public static void setup() {
        Locale.setDefault(new Locale("ja","JP"));
    }

    public static class BadRequestExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void BadRequestのErrorMessageが取得できる() {
            Response response = target("/bad")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(400));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#400"));
            assertThat(entity.getMessage(), CoreMatchers.is("不正なリクエストです。"));
        }

    }

    public static class ForbiddenExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ForbiddenExceptionのErrorMessageが取得できる() {
            Response response = target("/forbidden")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(403));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#403"));
            assertThat(entity.getMessage(), CoreMatchers.is("リソースへのアクセス権限がありません。"));
        }

    }

    public static class InternalServerErrorExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void InternalServerErrorExceptionのErrorMessageが取得できる() {
            Response response = target("/inter")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(500));
            assertThat(entity.getCode(), CoreMatchers.is("E-REST-SERVER#500"));
            assertThat(entity.getMessage(), CoreMatchers.is("予期しない例外が発生しました。"));
        }

    }

    public static class NotAcceptableExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void NotAcceptableExceptionのErrorMessageが取得できる() {
            Response response = target("/accept")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(406));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#406"));
            assertThat(entity.getMessage(), CoreMatchers.is("Accept関連のヘッダに受理できない内容が含まれています。"));
        }

    }

    public static class NotAllowedExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void NotAllowedExceptionのErrorMessageが取得できる() {
            Response response = target("/allowed")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(405));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#405"));
            assertThat(entity.getMessage(), CoreMatchers.is("リクエストが許可されていません。"));
        }

    }

    public static class NotAuthorizedExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void NotAuthorizedExceptionのErrorMessageが取得できる() {
            Response response = target("/auth")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(401));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#401"));
            assertThat(entity.getMessage(), CoreMatchers.is("認証に失敗しました。"));
        }

    }

    public static class NotFoundExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void NotFoundExceptionのErrorMessageが取得できる() {
            Response response = target("/not_found")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(404));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#404"));
            assertThat(entity.getMessage(), CoreMatchers.is("リソースが見つかりません。"));
        }

    }

    public static class NotSupportedExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void NotSupportedExceptionのErrorMessageが取得できる() {
            Response response = target("/support")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(415));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#415"));
            assertThat(entity.getMessage(), CoreMatchers.is("サポートされていないメディアタイプです。"));
        }

    }

    public static class ClientErrorExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ステータスコードが401の場合メッセージが解決される() {
            Response response = target("/error/401")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(401));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#401"));
            assertThat(entity.getMessage(), CoreMatchers.is("認証に失敗しました。"));
        }

        @Test
        public void ステータスコードが407の場合メッセージが解決される() {
            Response response = target("/error/407")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(407));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#407"));
            assertThat(entity.getMessage(), CoreMatchers.is("プロキシ認証が必要です。"));
        }

        @Test
        public void ステータスコードが408の場合メッセージが解決される() {
            Response response = target("/error/408")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(408));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#408"));
            assertThat(entity.getMessage(), CoreMatchers.is("リクエストがタイムアウトしました。"));
        }

        @Test
        public void ステータスコードが417の場合メッセージが解決される() {
            Response response = target("/error/417")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(417));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#499"));
            assertThat(entity.getMessage(), CoreMatchers.is("クライアントサイドの例外が発生しました。"));
        }

    }

    public static class ServerErrorExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ステータスコードが505の場合メッセージが解決される() {
            Response response = target("/error/505")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(505));
            assertThat(entity.getCode(), CoreMatchers.is("E-REST-SERVER#599"));
            assertThat(entity.getMessage(), CoreMatchers.is("サーバサイドで例外が発生しました。"));
        }

    }

    public static class ServiceUnavailableExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ServerUnavailableExceptionのErrorMessageが取得できる() {
            Response response = target("/unavailable")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(503));
            assertThat(entity.getCode(), CoreMatchers.is("E-REST-SERVER#503"));
            assertThat(entity.getMessage(), CoreMatchers.is("現在、サービスが利用できない状態です。"));
        }

    }
    
    public static class ApplicationRecoverableExceptionがハンドリングできる extends JerseyTest {
        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }
        
        @Test
        public void ApplicationRecoverableExceptionのErrorMessageが取得できる() {
            Response response = target("/recover")
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .acceptLanguage(Locale.JAPANESE)
                .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.nullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(400));
            assertThat(entity.getCode(), CoreMatchers.is("コード"));
            assertThat(entity.getMessage(), CoreMatchers.is("コード"));
        }
    }
    
    public static class ApplicationUnrecoverableExceptionがハンドリングできる extends JerseyTest {
        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }
        
        @Test
        public void ApplicationRecoverableExceptionのErrorMessageが取得できる() {
            Response response = target("/unrecover")
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .acceptLanguage(Locale.JAPANESE)
                .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(500));
            assertThat(entity.getCode(), CoreMatchers.is("コード"));
            assertThat(entity.getMessage(), CoreMatchers.is("コード"));
        }
    }
    
    public static class SystemExceptionがハンドリングできる extends JerseyTest {
        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }
        
        @Test
        public void SystemExceptionのErrorMessageが取得できる() {
            Response response = target("/system")
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .acceptLanguage(Locale.JAPANESE)
                .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(500));
            assertThat(entity.getCode(), CoreMatchers.is("コード"));
            assertThat(entity.getMessage(), CoreMatchers.is("コード"));
        }
    }

    public static class Throwableがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ThrowableのErrorMessageが取得できる() {
            Response response = target("/throw")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(500));
            assertThat(entity.getCode(), CoreMatchers.is("E-REST-SERVER#999"));
            assertThat(entity.getMessage(), CoreMatchers.is("予期しない例外が発生しました。"));
        }

    }
    
    public static class ConstraintViolationExceptionがハンドリングできる extends JerseyTest {

        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void ネストしたビーンのバリデーションを実行したときにエラーメッセージが取得できる() {
            List<Bar> bars = new ArrayList<Bar>();
            bars.add(new Bar("id"));
            bars.add(new Bar(""));
            Hoge hoge = new Hoge("", bars);
            Response response = target("/validate/hoge")
                    .request()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.JAPANESE)
                    .post(Entity.entity(hoge, MediaType.APPLICATION_JSON), Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getId(), CoreMatchers.nullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(400));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#400"));
            assertThat(entity.getMessage(), CoreMatchers.is("不正なリクエストです。"));
            List<ValidationMessage> vs = entity.getValidationMessages();
            assertThat(vs.size(), CoreMatchers.is(2));
            String[] paths = new String[] {
                vs.get(0).getPath(),
                vs.get(1).getPath()
            };
            String[] values = new String[] {
                vs.get(0).getMessage(),
                vs.get(1).getMessage()
            };
            assertThat(Arrays.asList(paths), hasItem("ValidationExceptionResourceTest.valid.Hoge.id"));
            assertThat(Arrays.asList(paths), hasItem("ValidationExceptionResourceTest.valid.Hoge.bars[1].id"));
            assertThat(Arrays.asList(values), hasItem("必須入力です。"));
        }

    }

    public static class ヘッダのロケールに応じてメッセージが変更される extends JerseyTest {
        @Override
        protected Application configure() {
            return config();
        }

        @Override
        protected void configureClient(ClientConfig clientConfig) {
            configClient(clientConfig);
        }

        @Test
        public void BadRequestのErrorMessageが取得できる() {
            Response response = target("/bad")
                    .request()
                    .header(HttpHeaders.CONTENT_LANGUAGE, "en")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptLanguage(Locale.ENGLISH)
                    .get(Response.class);
            ErrorMessage entity = response.readEntity(ErrorMessage.class);
            assertThat(entity, CoreMatchers.notNullValue());
            assertThat(entity.getStatus(), CoreMatchers.is(400));
            assertThat(entity.getCode(), CoreMatchers.is("W-REST-CLIENT#400"));
            assertThat(entity.getMessage(), CoreMatchers.is("Bad Request"));
        }

    }

    public static Application config() {
        return new ResourceConfig().property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true)
            .packages("jp.co.ctc_g.jse.core.rest.jersey")
            .register(ValidationConfigurationContextResolver.class)
            .register(LocaleContextFilter.class)
            .register(ObjectMapperProviderTest.class)
            .register(JacksonFeature.class);
    }

    public static void configClient(ClientConfig config) {
        config.register(LoggingFeature.class)
            .register(LocaleContextFilter.class)
            .register(ObjectMapperProviderTest.class)
            .register(JacksonFeature.class);
    }

}
