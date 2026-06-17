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

package jp.co.ctc_g.jse.core.rest.springmvc.client.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.BadRequestException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.ForbiddenException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.InternalServerErrorException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.NotAcceptableException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.NotFoundException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.ProxyAuthenticationRequiredException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.RequestTimeoutException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.ServiceUnavailableException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.UnauthorizedException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.UnprocessableEntityException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.UnsupportedMediaTypeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// ExpectedException removed - use assertThrows;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

// @Nested classes used instead of Enclosed
public class RestClientResponseErrorHandlerTest {

    
    public static class ClientErrorTest {

        private RestClientResponseErrorHandler handler;
        private ClientHttpResponse response;
        @BeforeEach
        public void setup() throws IOException {
            handler = new RestClientResponseErrorHandler();
            response = mock(ClientHttpResponse.class);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=UTF-8");
            when(response.getHeaders()).thenReturn(headers);
            when(response.getBody()).thenReturn(new ByteArrayInputStream("Body Message".getBytes()));
        }

        @Test
        public void BadRequestExceptionがスローされる() throws IOException {
            BadRequestException ex = assertThrows(BadRequestException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("400 BAD_REQUEST"));
        }

        @Test
        public void UnauthorizedExceptionがスローされる() throws IOException {
            UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("401 UNAUTHORIZED"));
        }

        @Test
        public void ForbiddenExceptionがスローされる() throws IOException {
            ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("403 FORBIDDEN"));
        }

        @Test
        public void NotAcceptableExceptionがスローされる() throws IOException {
            NotAcceptableException ex = assertThrows(NotAcceptableException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.NOT_ACCEPTABLE);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("406 NOT_ACCEPTABLE"));
        }

        @Test
        public void NotFoundExceptionがスローされる() throws IOException {
            NotFoundException ex = assertThrows(NotFoundException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("404 NOT_FOUND"));
        }

        @Test
        public void ProxyAuthenticationRequiredExceptionがスローされる() throws IOException {
            ProxyAuthenticationRequiredException ex = assertThrows(ProxyAuthenticationRequiredException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("407 PROXY_AUTHENTICATION_REQUIRED"));
        }

        @Test
        public void RequestTimeoutExceptionがスローされる() throws IOException {
            RequestTimeoutException ex = assertThrows(RequestTimeoutException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.REQUEST_TIMEOUT);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("408 REQUEST_TIMEOUT"));
        }

        @Test
        public void UnsupportedMediaTypeExceptionがスローされる() throws IOException {
            UnsupportedMediaTypeException ex = assertThrows(UnsupportedMediaTypeException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("415 UNSUPPORTED_MEDIA_TYPE"));
        }

        @Test
        public void UnproccesableEntityExceptionがスローされる() throws IOException {
            UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("422 UNPROCESSABLE_ENTITY"));
        }

        @Test
        public void HttpClientErrorExceptionがスローされる() throws IOException {
            HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.PAYMENT_REQUIRED);
                when(response.getStatusText()).thenReturn(HttpStatus.PAYMENT_REQUIRED.name());
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("402 PAYMENT_REQUIRED"));
        }
    }

    
    public static class ServerErrorTest {

        private RestClientResponseErrorHandler handler;
        private ClientHttpResponse response;
        @BeforeEach
        public void setup() throws IOException {
            handler = new RestClientResponseErrorHandler();
            response = mock(ClientHttpResponse.class);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=UTF-8");
            when(response.getHeaders()).thenReturn(headers);
            when(response.getBody()).thenReturn(new ByteArrayInputStream("Body Message".getBytes()));
        }

        @Test
        public void InternalServerErrorExceptionがスローされる() throws IOException {
            InternalServerErrorException ex = assertThrows(InternalServerErrorException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("500 INTERNAL_SERVER_ERROR"));
        }

        @Test
        public void ServiceUnavailableExceptionがスローされる() throws IOException {
            ServiceUnavailableException ex = assertThrows(ServiceUnavailableException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("503 SERVICE_UNAVAILABLE"));
        }

        @Test
        public void HttpServerErrorExceptionがスローされる() throws IOException {
            HttpServerErrorException ex = assertThrows(HttpServerErrorException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
                when(response.getStatusText()).thenReturn(HttpStatus.BAD_GATEWAY.name());
                handler.handleError(response);
            });
            assertThat(ex.getMessage(), is("502 BAD_GATEWAY"));
        }
    }

    
    public static class UnknownStatusTest {

        private RestClientResponseErrorHandler handler;
        private ClientHttpResponse response;
        @BeforeEach
        public void setup() {
            handler = new RestClientResponseErrorHandler();
            response = mock(ClientHttpResponse.class);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            when(response.getHeaders()).thenReturn(headers);
        }

        @Test
        public void 変換対象外のステータスコードの場合はRestClientExceptionがスローされる() throws IOException {
            assertThrows(RestClientException.class, () -> {
                when(response.getStatusCode()).thenReturn(HttpStatus.UPGRADE_REQUIRED);
                handler.handleError(response);
            });
        }
    }
}
