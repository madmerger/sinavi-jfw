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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import jp.co.ctc_g.jse.core.framework.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class TokenRequestDataValueProcessingTest {

    private MockHttpServletRequest request;

    @BeforeEach
    public void setup() throws Exception {
        request = new MockHttpServletRequest();
    }

    @Test
    public void トークンが保存されていない場合nullが返却される()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        Map<String, String> hiddenParameterMap = t.getExtraHiddenFields(request);        

        assertThat(hiddenParameterMap, is(nullValue()));
    }

    @Test
    public void セッションスコープに保存したトークンのhidden用パラメータが取得できる()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        Map<String, String> hiddenParameterMap = t.getExtraHiddenFields(request);        

        assertThat(hiddenParameterMap.keySet(), hasItem(TokenManager.SESSION_TOKEN_PARAMETER_NAME));
        assertThat(hiddenParameterMap.get(TokenManager.SESSION_TOKEN_PARAMETER_NAME), is(manager.getToken(request, Controllers.SCOPE_SESSION)));
    }

    @Test
    public void セッションスコープに保存したトークンがリセットされhidden用パラメータが取得できない()
            throws Exception {

        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();
        manager.saveToken(request, Controllers.SCOPE_SESSION);
        manager.isTokenValid(request, Controllers.SCOPE_SESSION);

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        Map<String, String> hiddenParameterMap = t.getExtraHiddenFields(request);

        assertThat(hiddenParameterMap, is(nullValue()));
    }
    
    @Test
    public void URLがそのまま返却される()
            throws Exception {
        
        String inputURL = "/foo/bar";
        
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        String processedUrl = t.processUrl(request, inputURL);

        assertThat(processedUrl, is(inputURL));
    }
    
    @Test
    public void actionがそのまま返却される()
            throws Exception {
        
        String inputAction = "/foo/bar";
        
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        String processedAction = t.processAction(request, inputAction);

        assertThat(processedAction, is(inputAction));
    }
    
    @Test
    public void フォームフィールドの値がそのまま返却される()
            throws Exception {
        
        String inputFormFieldValue = "test";
        
        TokenManager manager = new TokenManager();
        manager.afterPropertiesSet();

        TokenRequestDataValueProcessing t = new TokenRequestDataValueProcessing();
        Field managerField = t.getClass().getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(t, manager);
        String processedFormFieldValue = t.processFormFieldValue(request, "name", inputFormFieldValue, "type");

        assertThat(processedFormFieldValue, is(inputFormFieldValue));
    }
}
