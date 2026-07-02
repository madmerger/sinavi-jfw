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

package jp.co.ctc_g.jfw.profill.util;

import java.io.IOException;
import java.security.Principal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * このクラスは、現在のリクエストに関連付けられている {@link Principal} を
 * {@link PrincipalKeeper} に設定するフィルタです。
 * </p>
 * @see Principal
 * @see PrincipalKeeper
 */
public class PrincipalFilter implements Filter {

    /**
     * デフォルトコンストラクタです。
     */
    public PrincipalFilter() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * {@inheritDoc}
     * <p>
     * メソッド開始時に {@link PrincipalKeeper#setPrincipal(Principal)} を、
     * メソッド終了時に同メソッドに対して <code>null</code>を設定しています。
     * </p>
     */
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        Principal principal = r.getUserPrincipal();
        if (principal != null) {
            PrincipalKeeper.setPrincipal(principal);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            PrincipalKeeper.setPrincipal(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
}
