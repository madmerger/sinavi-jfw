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
import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
 * このクラスは、{@link ConstraintViolationException}の例外ハンドラです。
 * </p>
 * <p>
 * 処理中に{@link ConstraintViolationException}が発生した場合は、
 * このハンドラが例外を補捉し、
 * クライアントへJSON形式で以下の例外メッセージを返します。
 * <pre class="brush:java">
 * {
 *  "status": 400,
 *  "code": "W-REST-CLIENT#400",
 *  "message": "不正なリクエストです。",
 *  "validationMessages": [
 *   {
 *    "path": "ExceptionHandlerResource.throwValidationException.String",
 *    "message": "必須入力です。"
 *   }
 *  ]
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
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger L = LoggerFactory.getLogger(BadRequestExceptionMapper.class);
    private static final ResourceBundle R = InternalMessages.getBundle(BadRequestExceptionMapper.class);

    /**
     * デフォルトコンストラクタです。
     */
    public ValidationExceptionMapper() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        if (L.isDebugEnabled()) {
            L.debug(R.getString("D-REST-JERSEY-MAPPER#0013"));
        }
        ErrorMessages errors = ErrorMessages.create()
            .status(ErrorCode.BAD_REQUEST.status())
            .code(ErrorCode.BAD_REQUEST.code())
            .resolve();
        ErrorMessage error = bind(errors, exception.getConstraintViolations());
        return Response.status(ErrorCode.BAD_REQUEST.status())
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    /**
     * バリデーションエラーメッセージを構築するメソッドです。
     * デフォルトでは、pathとmessageのみ設定します。
     * @param errors {@link ErrorMessage}
     * @param violations {@link ConstraintViolation}
     * @return ValidationMessagesを設定した{@link ErrorMessage}
     */
    protected ErrorMessage bind(final ErrorMessages errors, final Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            String root = violation.getRootBeanClass().getSimpleName();
            String path = violation.getPropertyPath().toString();
            errors.bind(root + (!"".equals(path) ? '.' + path : ""), violation.getMessage());
        }
        return errors.get();
    }

}
