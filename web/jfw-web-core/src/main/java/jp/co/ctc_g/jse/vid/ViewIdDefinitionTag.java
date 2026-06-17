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

import java.util.Collection;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jse.core.framework.Controllers;
import jp.co.ctc_g.jse.core.taglib.ParameterAware;

/**
 * <p>
 * このタグクラスは、画面IDを定義するためのタグを提供します。
 * このクラスを利用すると、JSPで画面IDを容易に設定できます。
 * </p>
 * <h4>バージョン 3.1 からの変更内容について</h4>
 * <p>
 * バージョン 3.0以前のAPIでは記録されたリクエストパラメータを、デフォルトでパンクズリストのリンクに付加していました。<br/>
 * このようなAPIは場合によってセキュリティ的に保護が必要な情報を外部に露出してしまう結果となることから、<br/>
 * バージョン 3.1以降のAPIでは、デフォルトではパラメータを付加しないよう変更を加えました。
 * </p>
 * <h4>概要</h4>
 * <p>
 * 現在処理中のJSPが生成する画面にIDを付加する場合、以下のようにします。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:is id="VID#0001" /&gt;
 * </pre>
 * <p>
 * これにより、画面IDが<code>VID#0001</code>と指定されます。
 * {@link ViewId}については、
 * 当該Javadocをご参照ください（未参照の方には強くお勧めします）。
 * また、この画面IDを利用して画面遷移や機能実行の制約を実現するには、
 * {@link ViewIdConstraint}がお役にたちます。
 * </p>
 * <h4>パンクズリスト</h4>
 * <p>
 * パンクズリストとして表示される必要がある場合には、
 * <code>pankuzu</code>属性に<code>true</code>を指定する必要があります。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:is id="VID#0001" pankuzu="true" /&gt;
 * </pre>
 * この場合はパンクズに画面IDが表示されてしまうため、
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:is id="VID#0001" pankuzu="true" label="画面A" /&gt;
 * </pre>
 * <p>
 * とすることで、「画面A」がパンクズとして表示されます。
 * </p>
 * パンクズリストのURLはデフォルトで設定されますが、
 * 任意の指定も可能です。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:is id="VID#0001" pankuzu="true" label="画面A" url="/foo/bar/baz.do" /&gt;
 * </pre>
 * <p>
 * さらに、リクエストパラメータを追加することもできます。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:is id="VID#0001" pankuzu="true" label="画面A" url="/foo/bar/baz.do"&gt;
 *   &lt;vid:param name="name" value="value" /&gt;
 * &lt;/vid:is&gt;
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see ViewId
 * @see ViewIdConstraint
 * @see PankuzuTag
 */
public class ViewIdDefinitionTag extends TagSupport implements ParameterAware {

    private static final long serialVersionUID = -6955568666670258328L;

    private static final Collection<String> AVAILABLE_SCOPES =
            Lists.gen(Controllers.SCOPE_SESSION);
    
    private String id;
    private String label;
    private String url;
    private Boolean pankuzu;
    private String scope = Controllers.SCOPE_SESSION;
    private HashMap<String, String[]> paramters;
    private String include;
    private String exclude;

    /**
     * 画面IDを設定します。
     * @param id 画面ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * ラベルを設定します（パンクズ利用時）。
     * @param label ラベル
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * URLを設定します（パンクズ利用時）
     * @param url URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * この画面IDがパンクズとして表示されるかどうかを設定します
     * @param pankuzu パンクズとして表示される場合にtrue
     */
    public void setPankuzu(Boolean pankuzu) {
        this.pankuzu = pankuzu;
    }

    /**
     * 画面IDを取得します。
     * @return 画面ID
     */
    public String getId() {
        return id;
    }

    /**
     * ラベルを取得します（パンクズ利用時）。
     * @return ラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * URLを取得します（パンクズ利用時）。
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * この画面IDがパンクズとして表示されるかどうかを取得します。
     * @return パンクズとして表示される場合にtrue
     */
    public Boolean isPankuzu() {
        return pankuzu;
    }
    
    /**
     * この画面IDを保存するスコープを返却します。
     * @return この画面IDを保存するスコープ
     * @see Actions#SCOPE_CONVERSATION
     * @see Actions#SCOPE_SESSION
     */
    public String getScope() {
        return scope;
    }

    /**
     * この画面IDを保存するスコープを設定します。
     * @param scope この画面IDを保存するスコープ
     * @see Actions#SCOPE_CONVERSATION
     * @see Actions#SCOPE_SESSION
     */
    public void setScope(String scope) {
        Args.checkTrue(AVAILABLE_SCOPES.contains(scope));
        this.scope = scope;
    }

    /**
     * パンクズリスト生成時にURLに付加するパラメータを取得します。
     * @return パラメータ
     */
    public String getInclude() {
        return include;
    }

    /**
     * パンクズリスト生成時にURLに付加するパラメータを設定します。
     * @param include パラメータ
     */
    public void setInclude(String include) {
        this.include = include;
    }

    /**
     * 除外するパラメータを取得します。
     * @return exclude パラメータ
     */
    public String getExclude() {
        return exclude;
    }

    /**
     * 除外パラメータを指定します。
     * @param exclude 除外パラメータ
     */
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    /**
     * このクラスのインスタンスを生成します。
     */
    public ViewIdDefinitionTag() {
        super();
        reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();
        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();
        define();
        reset();
        return EVAL_PAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        super.release();
        reset();
    }

    /**
     * このインスタンスを再利用可能なようにリセットします。
     */
    protected void reset() {
        id = null;
        label = null;
        url = null;
        pankuzu = false;
        paramters = null;
        scope = Controllers.SCOPE_SESSION;
        include = null;
        exclude = null;
    }

    /**
     * 現在のこのインスタンスに設定されている情報を利用して、
     * 新しい画面IDオブジェクトを生成し、現在の画面IDとします。
     */
    protected void define() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ViewId vid = new ViewId(id);
        vid.setLabel(label);
        vid.setUrl(url);
        vid.setPankuzu(pankuzu);
        vid.setParams(paramters);
        vid.setInclude(include);
        vid.setExclude(exclude);
        vid.fill(request); // 未設定項目を埋める
        store(vid, request);
        reset();
    }
    
    /**
     * 指定された画面IDを保存します。
     * @param vid 画面ID
     * @param request 現在処理中のリクエスト
     */
    protected void store(ViewId vid, HttpServletRequest request) {
        if (Controllers.SCOPE_SESSION.equals(scope)) {
            ViewId.is(vid, request);
        } else {
            throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0007");
        }
    }

    /**
     * パラメータを構築します。
     * @param name キー
     * @param value 値
     */
    public void awareParameter(String name, String value) {
        awareParameter(name, new String[] {value});
    }

    /**
     * パラメータを構築します。
     * @param name キー
     * @param values 値
     */
    public void awareParameter(String name, String[] values) {
        if (paramters == null) {
            paramters = new HashMap<String, String[]>(5);
        }
        if (paramters.containsKey(name)) {
            String[] current = paramters.get(name);
            values = Arrays.merge(current, values);
        }
        paramters.put(name, values);
    }

}
