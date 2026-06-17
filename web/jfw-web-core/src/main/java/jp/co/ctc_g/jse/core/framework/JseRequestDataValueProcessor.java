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

package jp.co.ctc_g.jse.core.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * <p>
 * このクラスは、{@link RequestDataValueProcessor}の実装です。</br>
 * ビューによるレンダリング処理、または、リダイレクト処理前にURLのクエリパラメータ値やフォームフィールドの値を
 * 暗黙的に設定します。</br>
 * {@code Spring MVC}の各種タグライブラリを利用することにより、トークンチェック機能の動作に必要な下記の情報を、URLのクエリパラメータ値やフォームフィールドの値に暗黙的に設定します。</br>
 * </p>
 * <ul>
 *   <li>Token ID</li>
 * </ul>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JseRequestDataValueProcessor implements RequestDataValueProcessor {

    private List<RequestDataValueProcessor> requestDataValueProcessors = new ArrayList<RequestDataValueProcessor>();

    /**
     * デフォルトコンストラクタです。
     */
    public JseRequestDataValueProcessor() {}

    /**
     * {@inheritDoc}
     */
    public String processAction(HttpServletRequest request, String action) {
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
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        Map<String, String> extraHiddenFields = new HashMap<String, String>();
        for (RequestDataValueProcessor requestDataValueProcessor : requestDataValueProcessors) {
            Map<String, String> m = requestDataValueProcessor.getExtraHiddenFields(request);
            if (m != null) {
                extraHiddenFields.putAll(m);
            }
        }
        return extraHiddenFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processUrl(HttpServletRequest request, String url) {
        for (RequestDataValueProcessor requestDataValueProcessor : requestDataValueProcessors) {
            url = requestDataValueProcessor.processUrl(request, url);
        }
        return url;
    }

    /**
     * {@link RequestDataValueProcesso} インスタンスのリストを設定します。
     * @param requestDataValueProcessors {@link RequestDataValueProcesso} インスタンスのリスト
     */
    public void setRequestDataValueProcessors(List<RequestDataValueProcessor> requestDataValueProcessors) {
        this.requestDataValueProcessors = requestDataValueProcessors;
    }

    /**
     * {@link RequestDataValueProcesso} インスタンスのリストを返却します。
     * @return {@link RequestDataValueProcesso} インスタンスのリスト
     */
    protected List<RequestDataValueProcessor> getDefaultRequestDataValueProcessors() {
        return requestDataValueProcessors;
    }

}
