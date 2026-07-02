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
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Priorities;
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
 * このクラスは、{@link InternalServerErrorException}の例外ハンドラです。
 * </p>
 * <p>
 * 処理中に{@link InternalServerErrorException}が発生した場合は、
 * このハンドラが例外を補捉し、
 * クライアントへJSON形式で以下の例外メッセージを返します。
 * <pre class="brush:java">
 * {
 *  "id": "5e214aab-d149-4d79-a871-0e81faa150b9",
 *  "status": 500,
 *  "code": "E-REST-SERVER#500",
 *  "message": "予期しない例外が発生しました。"
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
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {

    private static final Logger L = LoggerFactory.getLogger(InternalServerErrorExceptionMapper.class);
    private static final ResourceBundle R = InternalMessages.getBundle(InternalServerErrorExceptionMapper.class);

    /**
     * デフォルトコンストラクタです。
     */
    public InternalServerErrorExceptionMapper() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(final InternalServerErrorException exception) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-REST-JERSEY-MAPPER#0004"));
        }
        ErrorMessage error = ErrorMessages.create(exception)
            .id()
            .code(ErrorCode.INTERNAL_SERVER_ERROR.code())
            .resolve()
            .get();
        L.error(error.log(), exception);
        return Response.status(exception.getResponse().getStatusInfo())
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
