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

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * <p>
 * このクラスはトークンIDをフォームのhiddenパラメータとして設定する機能を提供する{@link RequestDataValueProcessor}インタフェースの実装です。<br/>
 * この機構は{@code Spring MVC} {@code spring-form tag library}の{@code form}タグによって利用されます。その他のタグライブラリやHTMLネイティブな{@code form}タグに
 * よってJSPを実装した場合は、この機構によるトークIDの自動挿入は行われません。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see TokenProcessor
 * @see IDGenerator
 */
public class TokenRequestDataValueProcessing implements RequestDataValueProcessor {

    @Autowired
    private TokenManager manager;

    /**
     * デフォルトコンストラクタです。
     */
    public TokenRequestDataValueProcessing() {}

    /**
     * {@inheritDoc}
     */
    public String processAction(HttpServletRequest request, String action) {
        // do nothing.
        return action;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String processAction(HttpServletRequest request, String action, String httpMethod) {
        return action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        // do nothing.
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        return manager.getTokenMap(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processUrl(HttpServletRequest request, String url) {
        // do nothing.
        return url;
    }

}
