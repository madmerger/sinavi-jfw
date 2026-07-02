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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

/**
 * <p>
 * このクラスは、REST通信した際のレスポンスのステータスコードより例外をハンドリングする例外ハンドラです。
 * </p>
 * <p>
 * SpringMVCデフォルトの例外ハンドラでは、クライアント(4xx)とサーバ(5xx)、その他の3種類にしか分類されませんが、
 * この例外ハンドラを利用することにより、レスポンスのステータスコードを詳細に分類して例外をハンドリングすることが可能です。
 * </p>
 * <p>
 * ステータスコードと発生する例外は以下の通りです。
 * <ul>
 *  <li>クライアント4xx系のステータスコード</li>
 *  <ul>
 *   <li>400 : {@link BadRequestException}</li>
 *   <li>401 : {@link UnauthorizedException}</li>
 *   <li>403 : {@link ForbiddenException}</li>
 *   <li>404 : {@link NotFoundException}</li>
 *   <li>406 : {@link NotAcceptableException}</li>
 *   <li>407 : {@link ProxyAuthenticationRequiredException}</li>
 *   <li>408 : {@link RequestTimeoutException}</li>
 *   <li>415 : {@link UnsupportedMediaTypeException}</li>
 *   <li>422 : {@link UnprocessableEntityException}</li>
 *   <li>上記以外 : {@link HttpClientErrorException}</li>
 *  </ul>
 *  <li>サーバ5xx系のステータスコード</li>
 *  <ul>
 *   <li>500 : {@link InternalServerErrorException}</li>
 *   <li>501 : {@link ServiceUnavailableException}</li>
 *   <li>上記以外 : {@link HttpServerErrorException}</li>
 *  </ul>
 * </ul>
 * </p>
 * <p>
 * この例外ハンドラを利用するには、{@link org.springframework.web.client.RestTemplate#setErrorHandler(org.springframework.web.client.ResponseErrorHandler)}に登録する必要があります。
 * 以下に設定例を示します。
 * <pre class="brush:java">
 * &lt;bean id="responseErrorHandler" class="jp.co.ctc_g.jse.core.rest.springmvc.client.handler.RestClientResponseErrorHandler"/&gt;
 * &lt;bean id="template" class="org.springframework.web.client.RestTemplate"&gt;
 *  &lt;property name="errorHandler" ref="responseErrorHandler" /&gt; &lt;!-- ここに例外ハンドラを設定 --&gt;
 *  &lt;!-- MessageConverterやインタセプタは省略 --&gt;
 * &lt;/bean&gt;
 * </pre>
 * </p>
 * @see DefaultResponseErrorHandler
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class RestClientResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger L = LoggerFactory.getLogger(RestClientResponseErrorHandler.class);
    private static final ResourceBundle R = InternalMessages.getBundle(RestClientResponseErrorHandler.class);
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * デフォルトコンストラクタです。
     */
    public RestClientResponseErrorHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                handleClientError(statusCode, response);
                break;
            case SERVER_ERROR:
                handleServerError(statusCode, response);
                break;
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }

    /**
     * HTTPステータスコード:4xx(クライアントエラー)に対応した例外をスローします。ステータスコードと例外の対応は以下のとおりです。
     * @param statusCode HTTPステータス
     * @param response HTTPレスポンンス
     * @throws IOException I/O例外
     */
    protected void handleClientError(HttpStatus statusCode, ClientHttpResponse response) throws IOException {
        switch (statusCode) {
            case BAD_REQUEST:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0001"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new BadRequestException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case UNAUTHORIZED:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0002"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new UnauthorizedException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case FORBIDDEN:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0003"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new ForbiddenException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case NOT_FOUND:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0004"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new NotFoundException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case NOT_ACCEPTABLE:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0005"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new NotAcceptableException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case PROXY_AUTHENTICATION_REQUIRED:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0006"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new ProxyAuthenticationRequiredException(response.getHeaders(), readResponseBody(response),
                    readCharset(response));
            case REQUEST_TIMEOUT:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0007"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new RequestTimeoutException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case UNSUPPORTED_MEDIA_TYPE:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0008"),
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new UnsupportedMediaTypeException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case UNPROCESSABLE_ENTITY:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0009"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new UnprocessableEntityException(response.getHeaders(), readResponseBody(response), readCharset(response));
            default:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0010"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new HttpClientErrorException(statusCode, response.getStatusText(), response.getHeaders(),
                    readResponseBody(response), readCharset(response));
        }
    }

    /**
     * HTTPステータスコード:5xx(サーバエラー)に対応した例外をスローします。ステータスコードと例外の対応は以下のとおりです。
     * @param statusCode HTTPステータス
     * @param response HTTPレスポンス
     * @throws IOException I/O例外
     */
    protected void handleServerError(HttpStatus statusCode, ClientHttpResponse response) throws IOException {
        switch (statusCode) {
            case INTERNAL_SERVER_ERROR:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0011"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new InternalServerErrorException(response.getHeaders(), readResponseBody(response), readCharset(response));
            case SERVICE_UNAVAILABLE:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0012"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new ServiceUnavailableException(response.getHeaders(), readResponseBody(response), readCharset(response));
            default:
                if (L.isDebugEnabled()) {
                    L.debug(Strings.substitute(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0013"), 
                        Maps.hash("status", statusCode.toString())
                            .map("message", getResponseBodyAsString(response))));
                }
                throw new HttpServerErrorException(statusCode, response.getStatusText(), response.getHeaders(), readResponseBody(response), readCharset(response));
        }
    }

    private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getStatusCode().value());
        if (statusCode == null) {
            if (L.isDebugEnabled()) {
                L.debug(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0014"));
            }
            throw new UnknownHttpStatusCodeException(response.getStatusCode().value(), response.getStatusText(),
                response.getHeaders(), readResponseBody(response), readCharset(response));
        }
        return statusCode;
    }

    private byte[] readResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) { 
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException ex) {
            if (L.isDebugEnabled()) {
                L.debug(R.getString("D-SPRINGMVC-REST-CLIENT-HANDLER#0015"), ex);
            }
        }
        return new byte[0];
    }

    private Charset readCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharset() : null;
    }

    private String getResponseBodyAsString(ClientHttpResponse response) {
        try {
            Charset charset = readCharset(response);
            return new String(readResponseBody(response), charset != null ? charset.toString() : DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException ex) {
            throw new InternalException(RestClientResponseErrorHandler.class, "E-SPRINGMVC-REST-CLIENT-HANDLER#0001");
        }
    }
}
