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

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import jp.co.ctc_g.jse.core.message.MessageContext.Scope;

/**
 * <p>
 * エラー・メッセージを表示する機能を提供します。<br/>
 * {@link JseMessagesTag}は全メッセージを出力しますが、このタグライブラリはエラーのメッセージのみを出力します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JseErrorsTag extends JseMessagesTag {

    /**
     * デフォルトコンストラクタです。
     */
    public JseErrorsTag() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        Scope[] scopes = judge();
        if (scopes.length == 0) return;
        
        StringBuilder buffer = new StringBuilder();
        for (Scope s : scopes) {
            printMessages(buffer, s);
        }

        if (!onlyMsg && buffer.length() > 0) {
            out.print(HEADER + buffer.toString() + FOOTER);
        } else {
            out.print(buffer.toString());
        }
    }
    
    protected void printMessages(StringBuilder buffer, Scope s) throws JspException, IOException {
        switch (s) {
        case FLASH:
            printMessagesToFlash(buffer);
            break;
        case SESSION:
            printMessagesToSession(buffer);
            break;
        case REQUEST:
        default:
            printMessagesToRequest(buffer);
            break;
        }
    }
    
    protected void printMessagesToRequest(StringBuilder buffer) throws JspException, IOException {
        Messages messagesToRequest = extractMessagesAsList(MessageContext.ERROR_MESSAGE_KEY_TO_REQUEST);
        if (messagesToRequest != null && !messagesToRequest.isEmpty())
            printMessages(buffer, messagesToRequest.getMessageItemList(), ERROR_PREFIX, ERROR_SUFFIX);
    }
    
    protected void printMessagesToFlash(StringBuilder buffer) throws JspException, IOException {
        Messages messagesToFlash = extractMessagesAsListByFlash(MessageContext.ERROR_MESSAGE_KEY_TO_FLASH);
        if (messagesToFlash != null && !messagesToFlash.isEmpty())
            printMessages(buffer, messagesToFlash.getMessageItemList(), ERROR_PREFIX, ERROR_SUFFIX);
    }
    
    protected void printMessagesToSession(StringBuilder buffer) throws JspException, IOException {
        Messages messagesToSession = extractMessagesAsListBySession(MessageContext.ERROR_MESSAGE_KEY_TO_SESSION);
        if (messagesToSession != null && !messagesToSession.isEmpty())
            printMessages(buffer, messagesToSession.getMessageItemList(), ERROR_PREFIX, ERROR_SUFFIX);
    }
}
