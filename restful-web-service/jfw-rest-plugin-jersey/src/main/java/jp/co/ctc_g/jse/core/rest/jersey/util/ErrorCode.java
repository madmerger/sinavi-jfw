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

package jp.co.ctc_g.jse.core.rest.jersey.util;

import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * この列挙型はHTTPステータスごとのエラーコードとメッセージキーを表現します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public enum ErrorCode {

    /**
     * ステータスコード：400
     * メッセージコード：W-REST-CLIENT#400
     */
    BAD_REQUEST(Response.Status.BAD_REQUEST.getStatusCode(), "W-REST-CLIENT#400"),

    /**
     * ステータスコード：401
     * メッセージコード：W-REST-CLIENT#401
     */
    UNAUTHORIZED(Response.Status.UNAUTHORIZED.getStatusCode(), "W-REST-CLIENT#401"),

    /**
     * ステータスコード：403
     * メッセージコード：W-REST-CLIENT#403
     */
    FORBIDDEN(Response.Status.FORBIDDEN.getStatusCode(), "W-REST-CLIENT#403"),

    /**
     * ステータスコード：404
     * メッセージコード：W-REST-CLIENT#404
     */
    NOT_FOUND(Response.Status.NOT_FOUND.getStatusCode(), "W-REST-CLIENT#404"),

    /**
     * ステータスコード：405
     * メッセージコード：W-REST-CLIENT#405
     */
    METHOD_NOT_ALLOWED(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), "W-REST-CLIENT#405"),

    /**
     * ステータスコード：406
     * メッセージコード：W-REST-CLIENT#406
     */
    NOT_ACCEPTABLE(Response.Status.NOT_ACCEPTABLE.getStatusCode(), "W-REST-CLIENT#406"),

    /**
     * ステータスコード：407
     * メッセージコード：W-REST-CLIENT#407
     */
    PROXY_AUTHENTICATION_REQUIRED(Response.Status.PROXY_AUTHENTICATION_REQUIRED.getStatusCode(), "W-REST-CLIENT#407"),

    /**
     * ステータスコード：408
     * メッセージコード：W-REST-CLIENT#408
     */
    REQUEST_TIMEOUT(Response.Status.REQUEST_TIMEOUT.getStatusCode(), "W-REST-CLIENT#408"),

    /**
     * ステータスコード：415
     * メッセージコード：W-REST-CLIENT#415
     */
    UNSUPPORTED_MEDIA_TYPE(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "W-REST-CLIENT#415"),

    /**
     * ステータスコード：499(ダミー)、このステータスコードが利用されることはありません。
     * メッセージコード：W-REST-SERVER#499
     */
    CLIENT_UNKNOWN_ERROR(499, "W-REST-CLIENT#499"),

    /**
     * ステータスコード：500
     * メッセージコード：E-REST-SERVER#500
     */
    INTERNAL_SERVER_ERROR(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "E-REST-SERVER#500"),

    /**
     * ステータスコード：503
     * メッセージコード：E-REST-SERVER#503
     */
    SERVICE_UNAVAILABLE(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), "E-REST-SERVER#503"),

    /**
     * ステータスコード：599(ダミー)
     * メッセージコード：E-REST-SERVER#599
     */
    SERVER_UNKNOWN_ERROR(599, "E-REST-SERVER#599"),

    /**
     * ステータスコード：999(ダミー)
     * メッセージコード：E-REST-SERVER#999
     */
    UNKNOWN_ERROR(999, "E-REST-SERVER#999");

    private int status;

    private String code;

    private ErrorCode(int status, String code) {
        this.status = status;
        this.code = code;
    }

    private static final Map<Integer, ErrorCode> LOOKUP = new HashMap<Integer, ErrorCode>();
    static {
        for (ErrorCode e : ErrorCode.values()) {
            LOOKUP.put(e.status, e);
        }
    }

    /**
     * ステータスコードに応じたエラーコードを返却します。
     * @param status HTTPステータス
     * @return エラーコード
     */
    public static ErrorCode get(int status) {
        ErrorCode e = LOOKUP.get(Integer.valueOf(status));
        if (e != null) {
            return e;
        } else {
            if (Integer.valueOf(status).toString().startsWith("4")) {
                e = ErrorCode.CLIENT_UNKNOWN_ERROR;
            } else if (Integer.valueOf(status).toString().startsWith("5")) {
                e = ErrorCode.SERVER_UNKNOWN_ERROR;
            } else {
                e = ErrorCode.UNKNOWN_ERROR;
            }
            return e;
        }
    }

    /**
     * エラーコードを返却します。
     * @return エラーコード
     */
    public String code() {
        return code;
    }

    /**
     * ステータスコードを返します。
     * @return ステータスコード
     */
    public int status() {
        return status;
    }

}
