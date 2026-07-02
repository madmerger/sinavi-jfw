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

package jp.co.ctc_g.jse.vid;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Beans;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.framework.Controllers;

/**
 * <p>
 * このクラスは、{@link ViewId 画面ID}オブジェクトのプロパティを出力するためのタグライブラリです。
 * </p>
 * <h4>概要</h4>
 * <p>
 * 例えば、画面IDを出力する場合、以下のようにします。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:out property="id" /&gt;
 * </pre>
 * <p>
 * このようにすることで、現在の画面IDが出力されます。
 * プロパティ名を直接記述することを嫌う場合、{@link ConstantsStaticImportTag}と連携させると効果的です。
 * </p>
 * <pre class="brush:jsp">
 * &lt;conz:import from="jp.co.ctc_g.jfw.vid.ViewId.ID" /&gt;
 * &lt;vid:out property="${ID}" /&gt;
 * </pre>
 * <p>
 * もし、1つ前の画面IDを出力したい場合には、<code>history</code>プロパティが役に立ちます。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:out property="id" history="1" /&gt;
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see ViewId
 */
public class ViewIdOutTag extends SimpleTagSupport {

    private static final Collection<String> AVAILABLE_SCOPES =
            Lists.gen(Controllers.SCOPE_SESSION);

    private String property;
    private Integer history;
    private Boolean escapeRequired;
    private String scope;

    /**
     * このクラスのインスタンスを生成します。
     */
    public ViewIdOutTag() {
        super();
        property = "";
        history = 0;
        escapeRequired = true;
    }

    /**
     * 読み取るプロパティ名を設定します。
     * @param property 読み取るプロパティ名
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * 参照する画面ID履歴を指定します。
     * 省略した場合は0、つまり現在を参照します。
     * @param history 参照する画面ID履歴
     */
    public void setHistory(Integer history) {
        this.history = history;
    }

    /**
     * 出力する値にエスケープが必要かどうかを指定します。
     * デフォルトは<code>true</code>で、エスケープされます。
     * @param escape エスケープするかどうか
     */
    public void setEscapeRequired(Boolean escape) {
        this.escapeRequired = escape;
    }

    /**
     * 読み取るプロパティ名を設定します。
     * @return 読み取るプロパティ名
     */
    public String getProperty() {
        return property;
    }

    /**
     * 参照する画面ID履歴を取得します。
     * まだ指定されていない場合、0つまり現在を返却します。
     * @return 参照する画面ID履歴
     */
    public Integer getHistory() {
        return history;
    }

    /**
     * エスケープが必要かどうかを返却します。
     * @return エスケープが必要な場合<code>true</code>
     */
    public Boolean isEscapeRequired() {
        return escapeRequired;
    }

    /**
     * この画面IDを保存するスコープを返却します。
     * @return この画面IDを保存するスコープ
     */
    public String getScope() {
        return scope;
    }

    /**
     * この画面IDを保存するスコープを設定します。
     * @param scope この画面IDを保存するスコープ
     */
    public void setScope(String scope) {
        Args.checkTrue(AVAILABLE_SCOPES.contains(scope));
        this.scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        if (Strings.isEmpty(property)) {
            return;
        }
        PageContext context = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        ViewId vid = getPastViewId(request);
        if (vid == null) {
            return;
        }
        String value = readProperty(vid, property);
        if (escapeRequired) {
            value = Strings.escapeHTML(value);
        }
        context.getOut().print(value);
    }

    /**
     * 指定された画面IDオブジェクトの指定されたプロパティ値を読み取ります。
     * @param vid 画面ID
     * @param propertyName プロパティ
     * @return 読み取られた値
     */
    protected String readProperty(ViewId vid, String propertyName) {
        String value = (String) Beans.readPropertyValueNamed(propertyName, vid);
        return value;
    }

    private ViewId getPastViewId(HttpServletRequest request) {
        ViewId vid = null;
        if (Controllers.SCOPE_SESSION.equals(scope)) {
            vid = ViewId.history(history, request);
        } else {
            throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0007");
        }
        return vid;
    }
}
