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

package jp.co.ctc_g.jse.core.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import jp.co.ctc_g.jfw.core.resource.MessageSourceLocator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;

public class JseMessagesTagTest {

    private MockPageContext page;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;

    @BeforeAll
    public static void setupClass() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/jp/co/ctc_g/jse/core/message/JseMessagesTagTest");
        MessageSourceLocator.set(messageSource);
    }

    @BeforeEach
    public void setup() throws Exception {

        MockServletContext sc = new MockServletContext();
        request = new MockHttpServletRequest(sc);
        response = new MockHttpServletResponse();
        page = new MockPageContext(sc, request, response);
        request.setAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
    }

    @Test
    public void testWithoutError() throws Exception {

        JseMessagesTag tag = new JseMessagesTag();
        tag.setJspContext(page);
        tag.doTag();
        
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void testWithValidationError() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_MESSAGES_TAG_TEST#001");
        context.saveValidationMessageToRequest("Validation Error.", "test", "Required", "model");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setPath("test");
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p id=\"test_list_per_element\" class=\"test_required jfw_val_msg_style\">Validation Error.</p></div>"));
    }

    @Test
    public void testWithValidationErrorWithOnlyMsg() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_MESSAGES_TAG_TEST#001");
        context.saveValidationMessageToRequest("Validation Error.", "test", "Required", "model");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setPath("test");
        tag.setOnlyMsg(true);
        tag.doTag();

        assertThat(response.getContentAsString(), is("Validation Error."));
    }

    @Test
    public void testWithInfo() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_MESSAGES_TAG_TEST#001");
        context.saveInformationMessageToRequest("I-JX_MESSAGES_TAG_TEST#002");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">I-JX_MESSAGES_TAG_TEST#001</p>"
            + "<p class=\"jfw_msg_style\">I-JX_MESSAGES_TAG_TEST#002</p></div>"));
    }

    @Test
    public void testWithError() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-JX_MESSAGES_TAG_TEST#001");
        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_err_msg_style\">E-JX_MESSAGES_TAG_TEST#001</p></div>"));
    }

    @Test
    public void testWithInfoAndError() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_MESSAGES_TAG_TEST#001");
        context.saveErrorMessageToRequest("E-JX_MESSAGES_TAG_TEST#001");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">I-JX_MESSAGES_TAG_TEST#001</p>"
            + "<p class=\"jfw_err_msg_style\">E-JX_MESSAGES_TAG_TEST#001</p></div>"));
    }

    @Test
    public void filter属性がtrueのときにサニタイズされる() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-FILTER#0001");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setFilter(true);
        tag.doTag();
        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">&lt;&gt;&quot;&amp;</p></div>"));
    }

    @Test
    public void ignorePropertiesに指定したときに指定されたプロパティが出力されないこと() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToRequest("validation error 1", "name", "Required", "model");
        context.saveValidationMessageToRequest("validation error 2", "address", "Required", "model");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setOnlyMsg(true);
        tag.setIgnorePath("address");
        tag.doTag();

        assertThat(response.getContentAsString(), is("validation error 1"));
    }

    @Test
    public void ignorePropertiesに複数指定されたときには値が出力されないこと() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToRequest("validation error 1", "name", "Required", "model");
        context.saveValidationMessageToRequest("validation error 2", "address", "Required", "model");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setOnlyMsg(true);
        tag.setIgnorePath("name,address");
        tag.doTag();

        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void ignorePropertiesとpropertyの両方に指定されたときには値が出力されないこと() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveValidationMessageToRequest("validation error 1", "name", "Required", "model");
        context.saveValidationMessageToRequest("validation error 2", "address", "Required", "model");

        JseMessagesTag tag = new JseMessagesTag();
        tag.setFilter(false);
        tag.setJspContext(page);
        tag.setOnlyMsg(true);
        tag.setPath("name");
        tag.setIgnorePath("name");
        tag.doTag();

        assertThat(response.getContentAsString(), is(""));
    }
}
