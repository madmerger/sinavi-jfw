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

import java.util.ResourceBundle;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jse.core.rest.entity.ErrorMessage;
import jp.co.ctc_g.jse.core.rest.jersey.util.ErrorCode;
import jp.co.ctc_g.jse.core.rest.jersey.util.ErrorMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * このクラスは、{@link ServiceUnavailableException}の例外ハンドラです。
 * </p>
 * <p>
 * 処理中に{@link ServiceUnavailableException}が発生した場合は、
 * このハンドラが例外を補捉し、
 * クライアントへJSON形式で以下の例外メッセージを返します。
 * <pre class="brush:java">
 * {
 *  "id": "bc062d62-7d81-42fa-a4d8-25d28e005b6f",
 *  "status": 503,
 *  "code": "E-REST-SERVER#503",
 *  "message": "現在、サービスが利用できない状態です。"
 * }
 * </pre>
 * </p>
 * <p>
 * この例外ハンドラのプライオリティは{@link Priorities#USER}です。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
@Provider
@Priority(Priorities.USER)
public class ServiceUnavailableExceptionMapper implements ExceptionMapper<ServiceUnavailableException> {

    private static final Logger L = LoggerFactory.getLogger(ServiceUnavailableExceptionMapper.class);
    private static final ResourceBundle R = InternalMessages.getBundle(ServiceUnavailableExceptionMapper.class);

    /**
     * デフォルトコンストラクタです。
     */
    public ServiceUnavailableExceptionMapper() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(final ServiceUnavailableException exception) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-REST-JERSEY-MAPPER#0011"));
        }
        ErrorMessage error = ErrorMessages.create(exception)
            .id()
            .code(ErrorCode.SERVICE_UNAVAILABLE.code())
            .resolve()
            .get();
        L.error(error.log(), exception);
        return Response.status(exception.getResponse().getStatusInfo())
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

}
