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

package jp.co.ctc_g.jse.core.rest.springmvc.client;

import jp.co.ctc_g.jse.core.rest.springmvc.client.Entity;
import jp.co.ctc_g.jse.core.rest.springmvc.client.RestClientTemplate;
import jp.co.ctc_g.jse.core.rest.springmvc.client.Target;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.InternalServerErrorException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.exception.NotFoundException;
import jp.co.ctc_g.jse.core.rest.springmvc.client.handler.RestClientResponseErrorHandler;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
// hamcrest removed;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
// ExpectedException removed - use assertThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.hamcrest.CoreMatchers;

public class RestClientTemplateTest {

    protected RestClientTemplate template;
    private static Server jettyServer;

    private static String helloWorld = "H\u00e9llo W\u00f6rld";

    private static String baseUrl;

    private static MediaType contentType;

    @BeforeAll
    public static void jetty起動() throws Exception {
        int port = SocketTestUtils.findAvailableTcpPort();
        jettyServer = new Server(port);
        baseUrl = "http://localhost:" + port;
        ServletContextHandler handler = new ServletContextHandler();
        byte[] bytes = helloWorld.getBytes("UTF-8");
        contentType = new MediaType("text", "plain", Collections.singletonMap("charset", "UTF-8"));
        handler.addServlet(new ServletHolder(new GetTestServlet(bytes, contentType)), "/getCode");
        handler.addServlet(new ServletHolder(new GetTestServlet(new byte[0], contentType)), "/getCode/nothing");
        handler.addServlet(new ServletHolder(new PostTestServlet(helloWorld, baseUrl + "/post/1", bytes, contentType)), "/post");
        handler.addServlet(new ServletHolder(new PutTestServlet(helloWorld, baseUrl + "/put/1", bytes, contentType)), "/put");
        handler.addServlet(new ServletHolder(new DeleteTestServlet(helloWorld, baseUrl + "/delete/1", bytes, contentType)), "/delete");
        handler.addServlet(new ServletHolder(new NoRequestDataDeleteTestServlet(baseUrl + "/delete/norequestdata/1", bytes, contentType)), "/delete");
        handler.addServlet(new ServletHolder(new ErrorTestServlet(404)), "/status/notfound");
        handler.addServlet(new ServletHolder(new ErrorTestServlet(500)), "/status/server");
        jettyServer.setHandler(handler);
        jettyServer.start();
    }

    @BeforeEach
    public void setup() {
        RestTemplate t = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        template = new RestClientTemplate(t);
    }

    @AfterAll
    public static void jetty停止() throws Exception {
        if (jettyServer != null) {
            jettyServer.stop();
        }
    }

    @Test
    public void インスタンスを設定できる() throws Exception {
        RestClientTemplate rct = new RestClientTemplate();
        rct.setDelegate(new RestTemplate());
        assertThat(rct.getDelegate(), CoreMatchers.isA(RestOperations.class));
    }

    @Test
    public void GETでレスポンスが取得できる() {
        String s = template.get(Target.target(baseUrl + "/{method}", "getCode"), String.class);
        assertThat(s, CoreMatchers.is(helloWorld));
    }

    @Test
    public void GETでエンティティが取得できる() {
        ResponseEntity<String> entity = template.getForEntity(Target.target(baseUrl + "/{method}", "getCode"), String.class);
        assertThat(entity.getBody(), CoreMatchers.is(helloWorld));
        assertThat(entity.getHeaders().isEmpty(), CoreMatchers.is(false));
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(contentType));
        assertThat(entity.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
    }

    @Test
    public void GETでレスポンスがないときはNULLを返す() {
        String s = template.get(Target.target(baseUrl + "/getCode/nothing"), String.class);
        assertThat(s, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void POSTでレスポンスが取得できる() throws URISyntaxException {
        String s = template.post(Target.target(baseUrl + "/{method}", "post"), Entity.text(helloWorld), String.class);
        assertThat(s, CoreMatchers.is(helloWorld));
    }

    @Test
    public void POSTでエンティティが取得できる() throws URISyntaxException {
        ResponseEntity<String> entity = template.postForEntity(Target.target(baseUrl + "/{method}", "post"), Entity.text(helloWorld), String.class);
        assertThat(entity.getBody(), CoreMatchers.is(helloWorld));
        assertThat(entity.getHeaders().isEmpty(), CoreMatchers.is(false));
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(contentType));
        assertThat(entity.getStatusCode(), CoreMatchers.is(HttpStatus.CREATED));
    }

    @Test
    public void PUTでレスポンスが取得できる() throws URISyntaxException {
        String s = template.put(Target.target(baseUrl + "/{method}", "put"), Entity.text(helloWorld), String.class);
        assertThat(s, CoreMatchers.is(helloWorld));
    }

    @Test
    public void PUTでエンティティが取得できる() throws URISyntaxException {
        ResponseEntity<String> entity = template.putForEntity(Target.target(baseUrl + "/{method}", "put"), Entity.text(helloWorld), String.class);
        assertThat(entity.getBody(), CoreMatchers.is(helloWorld));
        assertThat(entity.getHeaders().isEmpty(), CoreMatchers.is(false));
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(contentType));
        assertThat(entity.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
    }

    @Test
    public void DELETEでレスポンスが取得できる() throws URISyntaxException {
        String s = template.delete(Target.target(baseUrl + "/{method}", "delete"), Entity.text(helloWorld), String.class);
        assertThat(s, CoreMatchers.is(helloWorld));
    }

    @Test
    public void リクエストデータがなくてもDELETEでレスポンスが取得できる() throws Exception {
        String s = template.delete(Target.target(baseUrl + "/{method}", "delete"), String.class);
        assertThat(s, CoreMatchers.is(helloWorld));
    }

    @Test
    public void DELETEでエンティティが取得できる() throws URISyntaxException {
        ResponseEntity<String> entity = template.deleteForEntity(Target.target(baseUrl + "/{method}", "delete"), Entity.text(helloWorld),
                                                                 String.class);
        assertThat(entity.getBody(), CoreMatchers.is(helloWorld));
        assertThat(entity.getHeaders().isEmpty(), CoreMatchers.is(false));
        assertThat(entity.getHeaders().getContentType(), CoreMatchers.is(contentType));
        assertThat(entity.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
    }

    @Test
    public void URLが見つからないときに404が返ってくる() {
        assertThrows(NotFoundException.class, () -> {
            RestTemplate rt = new RestTemplate();
            rt.setErrorHandler(new RestClientResponseErrorHandler());
            template.setDelegate(rt);
            template.get(Target.target(baseUrl + "/status/notfound"), String.class);
        });
    }

    @Test
    public void IntenalServerエラーの500が返ってくる() {
        assertThrows(InternalServerErrorException.class, () -> {
            RestTemplate rt = new RestTemplate();
            rt.setErrorHandler(new RestClientResponseErrorHandler());
            template.setDelegate(rt);
            template.get(Target.target(baseUrl + "/status/server"), String.class);
        });
    }

    @SuppressWarnings("serial")
    private static class ErrorTestServlet extends GenericServlet {

        private final int sc;

        private ErrorTestServlet(int sc) {
            this.sc = sc;
        }

        @Override
        public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            ((HttpServletResponse) response).sendError(sc);
        }
    }

    @SuppressWarnings("serial")
    private static class GetTestServlet extends HttpServlet {

        private final byte[] buf;

        private final MediaType contentType;

        private GetTestServlet(byte[] buf, MediaType contentType) {
            this.buf = buf;
            this.contentType = contentType;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            if (contentType != null) {
                response.setContentType(contentType.toString());
            }
            response.setContentLength(buf.length);
            FileCopyUtils.copy(buf, response.getOutputStream());
        }
    }

    @SuppressWarnings("serial")
    private static class PostTestServlet extends HttpServlet {

        private final String s;

        private final String location;

        private final byte[] buf;

        private final MediaType contentType;

        private PostTestServlet(String s, String location, byte[] buf, MediaType contentType) {
            this.s = s;
            this.location = location;
            this.buf = buf;
            this.contentType = contentType;
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            assertTrue(request.getContentLength() > 0, "Invalid request content-length");
            assertNotNull(request.getContentType(), "No content-type");
            String body = FileCopyUtils.copyToString(request.getReader());
            assertEquals(s, body, "Invalid request body");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setHeader("Location", location);
            response.setContentLength(buf.length);
            response.setContentType(contentType.toString());
            FileCopyUtils.copy(buf, response.getOutputStream());
        }
    }

    @SuppressWarnings("serial")
    private static class PutTestServlet extends HttpServlet {

        private final String s;

        private final String location;

        private final byte[] buf;

        private final MediaType contentType;

        private PutTestServlet(String s, String location, byte[] buf, MediaType contentType) {
            this.s = s;
            this.location = location;
            this.buf = buf;
            this.contentType = contentType;
        }

        @Override
        protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            assertTrue(request.getContentLength() > 0, "Invalid request content-length");
            assertNotNull(request.getContentType(), "No content-type");
            String body = FileCopyUtils.copyToString(request.getReader());
            assertEquals(s, body, "Invalid request body");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", location);
            response.setContentLength(buf.length);
            response.setContentType(contentType.toString());
            FileCopyUtils.copy(buf, response.getOutputStream());
        }

    }

    @SuppressWarnings("serial")
    private static class DeleteTestServlet extends HttpServlet {

        private final String s;

        private final String location;

        private final byte[] buf;

        private final MediaType contentType;

        private DeleteTestServlet(String s, String location, byte[] buf, MediaType contentType) {
            this.s = s;
            this.location = location;
            this.buf = buf;
            this.contentType = contentType;
        }

        @Override
        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String body = FileCopyUtils.copyToString(request.getReader());
            assertEquals(s, body, "Invalid request body");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", location);
            response.setContentLength(buf.length);
            response.setContentType(contentType.toString());
            FileCopyUtils.copy(buf, response.getOutputStream());
        }

    }

    @SuppressWarnings("serial")
    private static class NoRequestDataDeleteTestServlet extends HttpServlet {

        private final String location;

        private final byte[] buf;

        private final MediaType contentType;

        private NoRequestDataDeleteTestServlet(String location, byte[] buf, MediaType contentType) {
            this.location = location;
            this.buf = buf;
            this.contentType = contentType;
        }

        @Override
        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Location", location);
            response.setContentLength(buf.length);
            response.setContentType(contentType.toString());
            FileCopyUtils.copy(buf, response.getOutputStream());
        }

    }

}
