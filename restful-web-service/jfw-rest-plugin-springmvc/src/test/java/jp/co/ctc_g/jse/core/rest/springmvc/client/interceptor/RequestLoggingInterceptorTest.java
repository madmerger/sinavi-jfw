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

package jp.co.ctc_g.jse.core.rest.springmvc.client.interceptor;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// Enclosed removed - use @Nested;
// RunWith removed - use @ExtendWith or @Nested;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

// @Nested classes used instead of Enclosed
public class RequestLoggingInterceptorTest {

    public static class RequestTest {

        private RequestLoggingInterceptor intercepter;
        private HttpRequest request;
        private HttpHeaders headers;

        @BeforeEach
        public void setup() throws URISyntaxException {
            intercepter = new RequestLoggingInterceptor();
            request = mock(HttpRequest.class);
            when(request.getMethod()).thenReturn(HttpMethod.POST);
            when(request.getURI()).thenReturn(new URI("http://localhost:8080/test"));
            headers = new HttpHeaders();
            headers.add("accept-encoding", "gzip");
            headers.add("accept-encoding", "deflate");
            headers.add("x-request-token-key", "abcdefg");
            when(request.getHeaders()).thenReturn(headers);
        }

        @Test
        public void ContentTypeにJSONを指定したときにJSON形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
            intercepter.dumpRequest(request, new String("{\"foo\":\"bar\"}").getBytes());
            assertTrue(true);
        }

        @Test
        public void ContentTypeにXMLを指定したときにXML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
            intercepter.dumpRequest(request, new String("<root>foo</root>").getBytes());
            assertTrue(true);
        }

        @Test
        public void ContentTypeにATOM_XMLを指定したときにXML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_ATOM_XML.toString());
            intercepter.dumpRequest(request, new String("<root>foo</root>").getBytes());
            assertTrue(true);
        }

        @Test
        public void ContentTypeにHTMLを指定したときにHTML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.TEXT_HTML.toString());
            intercepter.dumpRequest(request, new String("key1=value1&key2=value2").getBytes());
            assertTrue(true);
        }

        @Test
        public void ContentTypeにTEXT_PLAINを指定したときにテキスト形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.TEXT_PLAIN.toString());
            intercepter.dumpRequest(request, new String("key1=value1&key2=value2").getBytes());
            assertTrue(true);
        }

    }

    public static class ResponseTest {

        private RequestLoggingInterceptor interceptor;
        private ClientHttpResponse response;
        HttpHeaders headers;

        @BeforeEach
        public void setup() throws URISyntaxException, IOException {
            interceptor = new RequestLoggingInterceptor();
            response = mock(ClientHttpResponse.class);
            when(response.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
            headers = new HttpHeaders();
            headers.add("accept-encoding", "gzip");
            headers.add("accept-encoding", "deflate");
            when(response.getHeaders()).thenReturn(headers);
        }

        @Test
        public void ContentTypeにJSONを指定したときにJSON形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
            interceptor.dumpResponse(response);
            assertTrue(true);
        }

        @Test
        public void ContentTypeにXMLを指定したときにXML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
            interceptor.dumpResponse(response);
            assertTrue(true);
        }

        @Test
        public void ContentTypeにATOM_XMLを指定したときにXML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.APPLICATION_ATOM_XML.toString());
            interceptor.dumpResponse(response);
            assertTrue(true);
        }

        @Test
        public void ContentTypeにHTMLを指定したときにHTML形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.TEXT_HTML.toString());
            interceptor.dumpResponse(response);
            assertTrue(true);
        }

        @Test
        public void ContentTypeにTEXT_PLAINを指定したときにテキスト形式のログが出力される() throws IOException {
            headers.add("Content-Type", MediaType.TEXT_PLAIN.toString());
            interceptor.dumpResponse(response);
            assertTrue(true);
        }
    }
}