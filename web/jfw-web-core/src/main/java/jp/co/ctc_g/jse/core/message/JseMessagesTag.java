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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;
import jp.co.ctc_g.jse.core.message.MessageContext.Scope;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;

/**
 * <p>
 * 画面にメッセージを表示するカスタムタグライブラリです。 
 * {@link MessageContext} からリクエストスコープ、フラッシュスコープ、セッションスコープに含まれる
 * メッセージを抽出し、表示します。
 * </p>
 * <h4>概要</h4>
 * <p>
 * このカスタムタグライブラリは、次の順序でメッセージを出力します。
 * </p>
 * <ol>
 * <li>エラーメッセージ</li>
 *  <ol>
 *  <li>リクエストスコープに格納されたエラーメッセージ</li>
 *  <li>フラッシュスコープに格納されたエラーメッセージ</li>
 *  <li>セッションスコープに格納されたエラーメッセージ</li>
 *  </ol>
 * <li>インフォメーションメッセージ</li>
 *  <ol>
 *  <li>リクエストスコープに格納されたエラーメッセージ</li>
 *  <li>フラッシュスコープに格納されたエラーメッセージ</li>
 *  <li>セッションスコープに格納されたエラーメッセージ</li>
 *  </ol>
 * <li>バリデーションメッセージ</li>
 *  <ol>
 *  <li>リクエストスコープに格納されたエラーメッセージ</li>
 *  <li>フラッシュスコープに格納されたエラーメッセージ</li>
 *  </ol>
 * </ol>
 * <p>
 * また、メッセージは<code>FrameworkResources</code>に定義された値の中に出力されます。
 * </p>
 * <ul>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.header:&lt;div class="jfw_messages"&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.footer:&lt;/div&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.info.prefix:&lt;p class="jfw_msg_style"&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.info.suffix:&lt;p&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.error.prefix:&lt;p class="jfw_err_msg_style"&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.error.suffix:&lt;/p&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.validation.prefix:&lt;p id="${property}_list_per_element" class="${property}_${constraintName} jfw_val_msg_style"&gt;</li>
 * <li>jp.co.ctc_g.jse.core.message.JseMessagesTag.validation.suffix:&lt;p&gt;</li>
 * </ul>
 * <p>
 * 例えば、エラーメッセージであれば、
 * <pre>
 * &lt;div class="jfw_messages"&gt;
 * &lt;p class="jfw_err_msg_style"&gt;
 * エラーメッセージ
 * &lt;/p&gt;
 * &lt;/div&gt;
 * </pre>
 * のようにメッセージがタグで囲まれます。
 * また、入力エラーの場合は少し特殊で、
 * <pre>
 * &lt;div class="jfw_messages"&gt;
 * &lt;p id="terminalId_list_per_element" class="terminalId_required jfw_val_msg_style"&gt;
 * 必須入力エラー
 * &lt;/p&gt;
 * &lt;/div&gt;
 * </pre>
 * のようにIDには入力エラーが発生したドメインのプロパティが含まれ、class属性にはプロパティとバリデータが含まれて出力されます。
 * これはクライアントサイドのrowseとの連携を考慮して出力しています。
 * </p>
 * <h4>プロパティの指定</h4>
 * <p>
 * <code>modelName</code>属性と<code>path</code>属性は抽出するメッセージと紐付いたモデル名・プロパティの値を指定します。
 * この属性を指定すると、指定したモデルのプロパティを持つメッセージのみが表示されます。
 *  通常、この属性はバリデーションエラーの属性を指定するために利用します。
 * </p>
 * <h4>出力を抑止する指定</h4>
 * <p>
 * <code>ignorePath</code>属性は特定のメッセージを抑止したいときに指定します。
 * この属性を指定すると、指定されたプロパティで格納されているメッセージを出力しません。
 * また、この属性は複数のプロパティを指定することが可能で、カンマ区切りで複数のプロパティ("hoge,bar")を指定してください。 なお、
 * <code>property</code>属性と<code>ignorePath</code>
 * 属性の両方に同じプロパティが指定されたときは出力を抑止しますので、ご注意ください。
 * </p>
 * <h4>HTMLエスケープ</h4>
 * <p>
 * <code>filter</code>属性はHTMLエスケープの有無を指定します。デフォルトでは<code>true</code>に設定されています。
 * HTMLエスケープは{@link Strings#escapeHTML(CharSequence)}を利用してエスケープされます。
 * 安易な指定をするとセキュリティ脆弱性につながりますので、安全だと検証されているときだけ利用してください。
 * </p>
 * <p>
 * <code>onlyMsg</code>属性は、メッセージだけを表示するかどうかを制御します。デフォルトでは<code>false</code>
 * に設定されています。
 * </p>
 * <h4>スコープ指定</h4>
 * <p>
 * <code>scope</code>属性は出力したいスコープ(request/flash/session)を指定します。デフォルトはすべてのスコープに格納しているメッセージを出力します。
 * もし、セッションに格納されているメッセージやリクエストに格納されたメッセージを別々に指定したい場合は、スコープを指定することによって、
 * メッセージを抑止することができます。
 * </p>
 * <h4>クラスコンフィグオーバライド</h4>
 * <p>
 * 以下の{@link Config クラスコンフィグオーバライド}用のキーが公開されています。
 * </p>
 * <table class="property_file_override_info">
 * <thead>
 * <tr>
 * <th>キー</th>
 * <th>型</th>
 * <th>効果</th>
 * <th>デフォルト</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>header</td>
 * <td>String</td>
 * <td>
 * メッセージ出力のヘッダー要素を出力します。</td>
 * <td>
 * &lt;div class="jfw_messages"&gt;</td>
 * </tr>
 * <tr>
 * <td>footer</td>
 * <td>String</td>
 * <td>
 * メッセージ出力のフッター要素を出力します。</td>
 * <td>
 * &lt;/div&gt;</td>
 * </tr>
 * <tr>
 * <td>info.prefix</td>
 * <td>String</td>
 * <td>
 * 通知メッセージ用のテンプレート(接頭)です。</td>
 * <td>
 * &lt;p class="jfw_msg_style"&gt;</td>
 * </tr>
 * <tr>
 * <td>info.suffix</td>
 * <td>String</td>
 * <td>
 * 通知メッセージ用のテンプレート(接尾)のです。</td>
 * <td>
 * &lt;/p&gt;</td>
 * </tr>
 * <tr>
 * <td>error.prefix</td>
 * <td>String</td>
 * <td>
 * エラーメッセージ用のテンプレート(接頭)です。</td>
 * <td>
 * &lt;p class="jfw_err_msg_style"&gt;</td>
 * </tr>
 * <tr>
 * <td>error.suffix</td>
 * <td>String</td>
 * <td>
 * エラーメッセージ用のテンプレート(接尾)のです。</td>
 * <td>
 * &lt;/p&gt;</td>
 * </tr>
 * <tr>
 * <td>validation.prefix</td>
 * <td>String</td>
 * <td>
 * バリデーションエラーメッセージ用のテンプレート(接頭)です。</td>
 * <td>
 * &lt;p id="${property}_list_per_element" class="${property}_${constraintName} jfw_val_msg_style"&gt;</td>
 * </tr>
 * <tr>
 * <td>validation.suffix</td>
 * <td>String</td>
 * <td>
 * バリデーションエラーエラーメッセージ用のテンプレート(接尾)のです。</td>
 * <td>
 * &lt;/p&gt;</td>
 * </tr>
 * </tbody>
 *
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JseMessagesTag extends SimpleTagSupport {

    protected static final String ERROR_PREFIX;

    protected static final String ERROR_SUFFIX;

    protected static final String FOOTER;

    protected static final String HEADER;

    protected static final String INFO_PREFIX;

    protected static final String INFO_SUFFIX;

    protected static final String VALIDATION_PREFIX;

    protected static final String VALIDATION_SUFFIX;

    static {
        Config config = WebCoreInternals.getConfig(JseMessagesTag.class);
        HEADER = config.find("header");
        FOOTER = config.find("footer");
        INFO_PREFIX = config.find("info.prefix");
        INFO_SUFFIX = config.find("info.suffix");
        ERROR_PREFIX = config.find("error.prefix");
        ERROR_SUFFIX = config.find("error.suffix");
        VALIDATION_PREFIX = config.find("validation.prefix");
        VALIDATION_SUFFIX = config.find("validation.suffix");
    }

    protected boolean filter = true;

    protected boolean onlyMsg = false;

    protected String path = null;

    protected String ignorePath = null;

    protected String modelName = null;

    protected String scope = null;

    /**
     * デフォルトコンストラクタです。
     */
    public JseMessagesTag() {}

    /**
     * filter属性を設定します。
     * @param filter <code>true</code>:HTMLエスケープする、<code>false</code>:HTMLエスケープしない
     */
    public void setFilter(boolean filter) {

        this.filter = filter;
    }

    /**
     * only属性を設定します。
     * @param onlyMsg <code>true</code>:メッセージのみ出力する、<code>false</code>:div要素などを出力する
     */
    public void setOnlyMsg(boolean onlyMsg) {

        this.onlyMsg = onlyMsg;
    }

    /**
     * path属性を設定します。
     * @param path プロパティ名
     */
    public void setPath(String path) {

        this.path = path;
    }

    /**
     * ignorePath属性を設定します。
     * @param ignorePath 出力させないプロパティ名
     */
    public void setIgnorePath(String ignorePath) {

        this.ignorePath = ignorePath;
    }

    /**
     * modelName属性を設定します。
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * scope属性を設定します。
     * スコープはrequest/session/flashです。
     * ※大文字小文字の区別はありません。
     * 複数指定する場合はカンマで指定します。
     * @param scope スコープ
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * リクエストに格納されているメッセージを取得します。
     * @param messageKey キー({@link MessageContext#messageKeys})
     * @return　メッセージ
     * @throws IOException 予期しないIO例外
     * @throws JspException 予期しないIO例外
     */
    protected Messages extractMessagesAsList(String messageKey) throws IOException, JspException {

        Messages messages = null;
        Messages requestMessages = (Messages) getJspContext().getAttribute(messageKey, PageContext.REQUEST_SCOPE);
        if (requestMessages != null && !requestMessages.isEmpty()) {
            messages = requestMessages;
        }
        return messages;
    }
    
    /**
     * フラッシュスコープに格納されているメッセージを取得します。
     * @param messageKey キー({@link MessageContext#messageKeys})
     * @return　メッセージ
     * @throws IOException 予期しないIO例外
     * @throws JspException 予期しないIO例外
     */
    protected Messages extractMessagesAsListByFlash(String messageKey) throws IOException, JspException {
        FlashMap flash = (FlashMap) getJspContext().getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE, PageContext.REQUEST_SCOPE);
        Messages messages = (Messages) flash.get(messageKey);
        return messages;
    }
    
    /**
     * セッションスコープに格納されているメッセージを取得します。
     * @param messageKey キー({@link MessageContext#messageKeys})
     * @return　メッセージ
     * @throws IOException 予期しないIO例外
     * @throws JspException 予期しないIO例外
     */
    protected Messages extractMessagesAsListBySession(String messageKey) throws IOException, JspException {
        return (Messages) getJspContext().getAttribute(messageKey, PageContext.SESSION_SCOPE);
    }

    /**
     * メッセージを出力します。
     * @param out JspWriter
     * @param messages メッセージ
     * @param prefix メッセージのプリフィックス
     * @param suffix メッセージのサフィックス
     * @throws IOException 予期しないIO例外
     */
    protected void printMessages(StringBuilder buffer, List<Message> messages, String prefix, String suffix) throws IOException {

        List<String> ignorePaths = null;
        if (Strings.isEmpty(ignorePath)) {
            ignorePaths = Collections.<String> emptyList();
        } else {
            ignorePaths = Arrays.asList(ignorePath.split(","));
        }

        for (Message message : messages) {
            if (!Strings.isEmpty(modelName) && !modelName.equals(message.getModelName()))
                continue;

            if (ignorePaths.contains(message.getProperty())
                    || (!Strings.isEmpty(path) && !path.equals(message.getProperty())))
                continue;

            if (MessageContext.MESSGE_IGNORE_KEY.equals(message.toString()))
                continue;

            if (!onlyMsg) {
                String p = prefix;
                if (!Strings.isEmpty(message.getConstraintName())) {
                    p = Strings.substitute(
                            prefix,
                            Maps.hash("property", message.getProperty()).map(
                                    "constraintName",
                                    message.getConstraintName().toLowerCase()));
                }
                buffer.append(p);
            }
            buffer.append(filter ? Strings.escapeHTML(message.toString()) : message);
            if (!onlyMsg) {
                buffer.append(suffix);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        StringBuilder buffer = new StringBuilder();
        for (String key : MessageContext.MESSAGES_KEY) {
            Messages messages = extractMessagesAsList(key);
            if (messages != null && !messages.isEmpty()) {
                switch (messages.getMessageType()) {
                case ERROR:
                    printMessages(buffer, messages.getMessageItemList(), ERROR_PREFIX, ERROR_SUFFIX);
                    break;
                case INFORMATION:
                    printMessages(buffer, messages.getMessageItemList(), INFO_PREFIX, INFO_SUFFIX);
                    break;
                case VALIDATION:
                default:
                    printMessages(buffer, messages.getMessageItemList(), VALIDATION_PREFIX, VALIDATION_SUFFIX);
                    break;
                }
            }
        }
        if (!onlyMsg && buffer.length() > 0) {
            out.print(HEADER + buffer.toString() + FOOTER);
        } else {
            out.print(buffer.toString());
        }
    }

    protected Scope[] judge() {
        if (scope == null) return new Scope[]{Scope.REQUEST, Scope.FLASH, Scope.SESSION};
        String[] scopes = scope.split(",");
        List<Scope> results = new ArrayList<Scope>();
        for (Scope sp : Scope.values()) {
            for (String s : scopes) {
                if (sp.name().equalsIgnoreCase(s)) results.add(sp);
            }
        }
        return results.toArray(new Scope[0]);
    }

}
