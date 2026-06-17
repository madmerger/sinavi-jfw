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
import org.springframework.web.servlet.support.RequestContextUtils;

public class JseInformationsTagTest {

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
    public void インフォメーションメッセージがセットされてい場合は何も出力されない() throws Exception {

        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();
        
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void インフォメーションメッセージがセットされ出力される() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_INFORMATIONS_TAG_TEST#0001");
        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">I-JX_INFORMATIONS_TAG_TEST#0001</p></div>"));
    }

    @Test
    public void インフォメーションメッセージがセットされメッセージのみ出力される() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_INFORMATIONS_TAG_TEST#0001");
        JseInformationsTag tag = new JseInformationsTag();
        tag.setOnlyMsg(true);
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("I-JX_INFORMATIONS_TAG_TEST#0001"));
    }
    
    @Test
    public void インフォメーションメッセージがフラッシュスコープにセットされ出力される() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToFlash("I-JX_INFORMATIONS_TAG_TEST#0001");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        request.setAttribute(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH, flashMap.get(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH));
        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">I-JX_INFORMATIONS_TAG_TEST#0001</p></div>"));
    }

    @Test
    public void インフォメーションメッセージがリクエストとフラッシュスコープにセットされ出力される() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveInformationMessageToRequest("I-JX_INFORMATIONS_TAG_TEST#0001");
        context.saveInformationMessageToFlash("I-JX_INFORMATIONS_TAG_TEST#0002");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        request.setAttribute(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH, flashMap.get(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH));
        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is("<div class=\"jfw_messages\">"
            + "<p class=\"jfw_msg_style\">I-JX_INFORMATIONS_TAG_TEST#0001</p>"
            + "<p class=\"jfw_msg_style\">I-JX_INFORMATIONS_TAG_TEST#0002</p></div>"));
    }

    @Test
    public void エラーメッセージがセットされ出力されない() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToRequest("E-JX_INFORMATIONS_TAG_TEST#0001");
        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    public void エラーメッセージがフラッシュスコープにセットされ出力されない() throws Exception {

        MessageContext context = new MessageContext(request);
        context.saveErrorMessageToFlash("E-JX_ERRORS_TAG_TEST#0001");
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        request.setAttribute(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH, flashMap.get(MessageContext.INFORMATION_MESSAGE_KEY_TO_FLASH));
        JseInformationsTag tag = new JseInformationsTag();
        tag.setJspContext(page);
        tag.doTag();

        assertThat(response.getContentAsString(), is(""));
    }
}
