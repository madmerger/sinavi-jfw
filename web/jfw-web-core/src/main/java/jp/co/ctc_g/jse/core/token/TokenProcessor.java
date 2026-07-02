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

package jp.co.ctc_g.jse.core.token;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * このクラスはトークの制御を行うためのAPIを規定するインタフェースです。<br/>
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see SessionTokenProcessor
 */
public interface TokenProcessor {

    /**
     * トークンの妥当性チェックを行います。
     * @param request リクエスト
     * @return トークンが一致した場合は<code>true</code>
     */
    boolean isTokenValid(HttpServletRequest request);

    /**
     * トークンの妥当性チェックを行います。
     * @param request リクエスト
     * @param reset トークンを削除するかどうかを指定します。<code>true</code>を指定した場合、トークンの妥当性チェック実行後にトークンを削除します。
     * @return トークンが一致した場合は<code>true</code>
     */
    boolean isTokenValid(HttpServletRequest request, boolean reset);

    /**
     * 保存されているトークを返却します。
     * @param request リクエスト
     * @return トークン
     */
    String getCurrentToken(HttpServletRequest request);

    /**
     * 保存されているリセットします。
     * @param request リクエスト
     */
    void resetToken(HttpServletRequest request);

    /**
     * トークンを保存します。
     * @param request リクエスト
     */
    void saveToken(HttpServletRequest request);

}
