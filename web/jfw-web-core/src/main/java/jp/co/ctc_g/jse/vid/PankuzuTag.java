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

import static jp.co.ctc_g.jfw.core.util.Args.checkTrue;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.framework.Controllers;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;

/**
 * <p>
 * このクラスは、画面IDによるパンクズリストを表示するためのタグクラスです。
 * </p>
 * <h4>概要</h4>
 * <p>
 * パンクズリストとは、ユーザが遷移してきた画面の履歴をリスト化して表示するユーザインタフェースパターンの種類です。
 * これにより、ユーザは任意の画面に戻ることができるようになります。
 * パンクズリスト自体はよく利用されるパターンですが、
 * これをプログラムにハードコードしてしまうと、
 * 画面遷移順序が変更された場合の修正コストの増大が懸念されます。
 * そこで、このクラスは画面IDを利用して動的にパンクズリストを表示します。
 * 表示は簡単です。
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:pankuzu /&gt;
 * </pre>
 * <p>
 * これだけです。デフォルトでは、
 * 例えばユーザが画面A、画面B、画面Cと遷移した場合、
 * 以下のように表示されます。
 * </p>
 * <pre>
 * 画面A > 画面B > 画面C
 * </pre>
 * <h4>画面IDオブジェクトの設定</h4>
 * <p>
 * パンクズリストを表示するには、画面IDオブジェクトが必要です。
 * 画面IDオブジェクトの設定により、
 * パンクズリストがクリックされた際のURL及び付帯するパラメータや、
 * パンクズとして表示される際の文字列などが決まります。
 * 画面IDオブジェクトを生成するには、2つの方法があります。
 * <ul>
 *   <li>JSPでタグライブラリを利用して設定</li>
 *   <li>コントローラで{@code ViewId} API を利用して設定</li>
 * </ul>
 * <h5>JSPでタグライブラリを利用して設定</h5>
 * JSPで{@link ViewIdDefinitionTag}を利用して画面IDオブジェクトを設定します。。
 * {@code id} 属性に画面IDを設定することにより画面IDオブジェクトを設定することができます。
 * パンクズリストを表示するにはこれだけでは不十分で、{@code pankuzu} 属性に{@code true}を設定する必要があります。<br/>
 * この状態でパンクズリストをこの状態で表示するとリンクの文字列に{@code id} 属性として指定した {@code #VID#001}を利用します。
 * これをカスタマイズするには、{@code label} 属性にリンクとして表示させたい値を設定します。下記のコード例の場合は、{@code 画面A}
 * がリンクの文字列として利用されます。
 * <pre class="brush:jsp">
 * &lt;vid:is id="#VID#0001" label="画面A" pankuzu="true"&gt;
 *   &lt;vid:param name="name" value="value" /&gt;
 * &lt;/vid:is&gt;
 * </pre>
 * パンクズリストについては、{@link ViewIdDefinitionTag}にも利用する際に有用な情報が記載されています。
 * 合わせて確認して下さい。
 * <h5>コントローラで{@code ViewId} API を利用して設定</h5>
 * コントローラで{@code ViewId} API を利用して設定する際の例は下記の通りです。
 * この実装例は、"JSPでタグライブラリを利用して設定"する際に示した実装例と等価です。<br/>
 * なお、コントローラで生成した画面IDオブジェクトは該当のコントローラが呼び出す画面に設定されます。
 * <pre class="brush:java">
 * ViewId vid = new ViewId("VID#0001");
 * vid.setLabel("画面A"); // パンクズとして表示される文字列
 * vid.setUrl(request.getContextPath() + "/a.do"); // パンクズがクリックされた場合のリクエストURL
 * vid.setPankuzu(true); // パンクズとして表示されるかどうか
 * vid.setParams(Maps.hash("name", "value")); // URLのリクエストパラメータ
 * ViewId.is(vid, request);
 * </pre>
 * </p>
 * <h4>パンクズアイテムのカスタマイズ</h4>
 * <p>
 * パンクズアイテムの表示を変更はJSP-BODY要素を編集することで容易に実現できます。
 * 例えば、アンカーではなくボタンで遷移したいのであれば（この例が酷いものであることは承知しています）、
 * </p>
 * <pre class="brush:jsp">
 * &lt;vid:pankuzu var="vid"&gt;
 *   &lt;form action=${vid.url}${vid.query}"&gt;
 *     &lt;input type="button" value="${vid.label}" /&gt;
 *   &lt;/form&gt;
 * &lt;/vid:pankuzu&gt;
 * </pre>
 * <p>
 * というように実現できます。
 * このようなカスタムされたJSP-BODYをデフォルトとしたいのであれば、
 * クラスコンフィグオーバライドのpankuzu_templateを利用してください。
 * </p>
 * <h4>エンティティエスケープ</h4>
 * <p>
 * {@see ViewId#getLabel() 画面IDオブジェクトのラベルプロパティ}は、
 * パンクズタグのボディを指定しない場合に限りHTMLエンティティがエスケープされます。
 * これは、クラスコンフィグオーバライドをした場合でも同様です。
 * パンクズタグのボディを指定した場合には、
 * JSTLのファンクションタグ(fn:escapeXML)を利用するなどでエスケープしてください。
 * </p>
 * <h4>リクエストパラメータのパーセントエスケープ</h4>
 * <p>
 * リクエストパラメータのパーセントエスケープ（URLエンコード）は、
 * {@see ViewId#getQuery()}の仕様に準じます。
 * </p>
 * <h4>クラスコンフィグオーバライド</h4>
 * <p>
 * 以下の{@link Config クラスコンフィグオーバライド}用のキーが公開されています。
 * </p>
 * <table class="property_file_override_info">
 *  <thead>
 *   <tr>
 *    <th>キー</th>
 *    <th>型</th>
 *    <th>効果</th>
 *    <th>デフォルト</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>ignore_current_pankuzu_item</td>
 *    <td>java.lang.Boolean</td>
 *    <td>
 *      現在の画面をパンクズとして表示しないかどうかです。
 *      trueの場合、表示しません。
 *    </td>
 *    <td>
 *     true
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>drill_down_symbol</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      パンクズリストのドリルダウン記号です。
 *      ドリルダウン記号とは、パンクズリスト間に表示される文字列のことです。
 *    </td>
 *    <td>
 *     &gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>append_drill_down_symbol_at_head</td>
 *    <td>java.lang.Boolean</td>
 *    <td>
 *      パンクズリストの先頭にドリルダウン記号を付けるかどうかです。
 *      trueの場合、ドリルダウン記号が付加されます。
 *    </td>
 *    <td>
 *     false
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>append_drill_down_symbol_at_tail</td>
 *    <td>java.lang.Boolean</td>
 *    <td>
 *      パンクズリストの末尾にドリルダウン記号を付けるかどうかです。
 *      trueの場合、ドリルダウン記号が付加されます。
 *    </td>
 *    <td>
 *     false
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>pankuzu_template</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      パンクズリストに表示されるアイテムのHTMLテンプレートです。
 *      これはJSP時のBODY要素指定と同じ意味になります。
 *      テンプレート内の${url},${query},${label}は、
 *      それぞれ画面IDオブジェクトの対応するプロパティで置換されます。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_vid_pankuzu_item"&gt;&lt;a href="${url}${query}"&gt;${label}&lt;/a&gt;&lt;/span&gt;
 *    </td>
 *   </tr>
 *  </tbody>
 * </table>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class PankuzuTag extends SimpleTagSupport {

    private static final String DEFAULT_VAR = "viewId";
    private static final String DEFAULT_VAR_STATUS = "status";
    
    private static final Collection<String> AVAILABLE_SCOPES =
            Lists.gen(Controllers.SCOPE_SESSION);

    private static final boolean IGNORE_CURRENT_PANKUZU_ITEM;
    private static final String DRILL_DOWN_SYMBOL;
    private static final boolean APPEND_DRILL_DOWN_SYMBOL_AT_HEAD;
    private static final boolean APPEND_DRILL_DOWN_SYMBOL_AT_TAIL;
    private static final String DEFAULT_TEMPLATE;

    private String var;
    private String varStatus;
    
    private String scope = Controllers.SCOPE_SESSION;

    static {
        Config c = WebCoreInternals.getConfig(PankuzuTag.class);
        IGNORE_CURRENT_PANKUZU_ITEM = Boolean.valueOf(c.find("ignore_current_pankuzu_item"));
        DRILL_DOWN_SYMBOL = c.find("drill_down_symbol");
        APPEND_DRILL_DOWN_SYMBOL_AT_HEAD = Boolean.valueOf(c.find("append_drill_down_symbol_at_head"));
        APPEND_DRILL_DOWN_SYMBOL_AT_TAIL = Boolean.valueOf(c.find("append_drill_down_symbol_at_tail"));
        DEFAULT_TEMPLATE = c.find("pankuzu_template");
    }

    /**
     * このクラスのインスタンスを生成します。
     */
    public PankuzuTag() {
        super();
        var = DEFAULT_VAR;
        varStatus = DEFAULT_VAR_STATUS;
    }

    /**
     * BODY評価時に画面IDオブジェクトをpageスコープに格納する際の属性名を指定します。
     * @param var 属性名
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * BODY評価時のループ情報オブジェクトをpageスコープに格納する際の属性名を指定します。
     * @param varStatus 属性名
     */
    public void setVarStatus(String varStatus) {
        this.varStatus = varStatus;
    }

    /**
     * BODY評価時に画面IDオブジェクトをpageスコープに格納する際の属性名を取得します。
     * @return 属性名
     */
    public String getVar() {
        return var;
    }

    /**
     * BODY評価時のループ情報オブジェクトをpageスコープに格納する際の属性名を取得します。
     * @return 属性名
     */
    public String getVarStatus() {
        return varStatus;
    }
    
    /**
     * パンくずが保存されているスコープを返却します。
     * @return この画面IDを保存するスコープ
     */
    public String getScope() {
        return scope;
    }

    /**
     * パンくずが保存されているスコープを設定します。
     * @param scope この画面IDを保存するスコープ
     */
    public void setScope(String scope) {
        checkTrue(AVAILABLE_SCOPES.contains(scope));
        this.scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        JspFragment template = getJspBody();
        if (template != null) {
            render(template);
        } else {
            render();
        }
    }

    /**
     * パンクズリストを表示します。
     * このメソッドはJSP-BODYを評価せずに、
     * 既存のHTMLテンプレート、あるいはクラスコンフィグオーバライドされたHTMLテンプレートを利用します。
     * @throws JspException JSP処理中にエラーが発生した場合
     * @throws IOException 入出力エラーの場合
     */
    protected void render() throws JspException, IOException {
        render(new JspFragment() {
            @Override public JspContext getJspContext() {
                return PankuzuTag.this.getJspContext();
            }
            @Override public void invoke(Writer out) throws JspException, IOException {
                JspContext context = getJspContext();
                ViewId vid = (ViewId) context.getAttribute(var);
                String encodedUrl = encodeURL(vid.getUrl());
                String item = Strings.substitute(DEFAULT_TEMPLATE, Maps
                            .hash("url", encodedUrl)
                            .map("label", Strings.escapeHTML(vid.getLabel()))
                            .map("query", trimQueryMarkerIfGetRequestParameterExists(encodedUrl, vid.getQuery())));
                context.getOut().print(item);
            }
        });
    }
    
    String encodeURL(String url) {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        return response.encodeURL(url);
    }
    
    String trimQueryMarkerIfGetRequestParameterExists(String encodedUrl, String query) {
        if (encodedUrl.contains("?")) {
            return query.replace("?", ViewId.QUERIES_JOIN_WORD);
        }
        return query;
    }

    /**
     * パンクズリストを表示します。
     * パンクズアイテムを表現するHTMLは、指定されたフラグメントにより生成されることを期待しています。
     * @param template パンクズアイテム（HTML）を生成するフラグメント
     * @throws JspException JSP処理中にエラーが発生した場合
     * @throws IOException 入出力エラーの場合
     */
    protected void render(JspFragment template) throws JspException, IOException {
        PageContext context = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        List<ViewId> items = new ArrayList<ViewId>(ViewId.size(request));
        ViewId current = getCurrentViewId(request);
        for (ViewId vid : getCurrentContainer(request)) {
            if (vid.isPankuzu()) {
                if (IGNORE_CURRENT_PANKUZU_ITEM && vid.equals(current)) {
                    continue;
                } else {
                    items.add(vid);
                }
            }
        }
        int size = items.size();
        // パンクズサイズが0以下の場合は終了
        if (size <= 0) return;
        PankuzuLoopStatus status = new PankuzuLoopStatus();
        context.setAttribute(varStatus, status);
        Iterator<ViewId> iterator = items.iterator();
        for (int index = 0; iterator.hasNext() && index < size; index++) {
            ViewId vid = iterator.next();
            // ループステータス更新
            status.setIndex(index);
            status.setCount(index + 1);
            status.setFirst(index == 0);
            status.setLast(index == size - 1);
            status.setOdd(status.getCount() % 2 != 0);
            status.setEven(status.getCount() % 2 == 0);
            // ヘッドにドリルダウン付加
            if (status.isFirst() && APPEND_DRILL_DOWN_SYMBOL_AT_HEAD) {
                getJspContext().getOut().print(DRILL_DOWN_SYMBOL);
            }
            // 指定された名前でViewIdをスコープにバインド
            context.setAttribute(var, vid);
            template.invoke(null);
            context.removeAttribute(var);
            // テイルにドリルダウン付加
            if (!status.isLast() || (status.isLast() && APPEND_DRILL_DOWN_SYMBOL_AT_TAIL)) {
                getJspContext().getOut().print(DRILL_DOWN_SYMBOL);
            }
        }
        context.removeAttribute(varStatus);
    }
    
    private ViewId getCurrentViewId(HttpServletRequest request) {
        ViewId vid = null;
        if (Controllers.SCOPE_SESSION.equals(scope)) {
            vid = ViewId.current(request);
        } else {
            throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0007");
        }
        return vid;
    }
    
    private Iterable<ViewId> getCurrentContainer(HttpServletRequest request) {
        Iterable<ViewId> vids = null;
        if (Controllers.SCOPE_SESSION.equals(scope)) {
            vids = ViewId.container(request);
        } else {
            throw new InternalException(ViewIdConstraintHandlerInterceptor.class, "E-VID#0007");
        }
        return vids;
    }

    /**
     * <p>
     * このクラスは、{@link PankuzuTag}がパンクズリストを表示処理のループカウンタです。
     * </p>
     * <p>
     * このカウンタは{@link PankuzuTag}の処理中、pageスコープ内に格納されています。
     * デフォルト名は<code>status</code>ですが、{@link PankuzuTag}タグの<code>varStatus</code>属性にて、
     * 任意の名称に変更できます。
     * 例えば、以下のようにアクセスします。
     * </p>
     * <pre class="brush:jsp">
     * &lt;vid:pankuzu&gt;
     *   &lt;div&gt;${status.count}&lt;/div&gt;
     * &lt;/vid:pankuzu&gt;
     * </pre>
     * <p>
     * この場合、パンクズリストを表示する代わりに、パンクズアイテムの数だけ数字を増分して表示します。
     * </p>
     */
    public static class PankuzuLoopStatus {

        private int index;
        private int count;
        private boolean first;
        private boolean last;
        private boolean odd;
        private boolean even;

        /**
         * このクラスのインスタンスを生成します。
         */
        protected PankuzuLoopStatus() {
        }

        /**
         * 現在のループインデクス（0からはじまる）を設定します。
         * @param index 現在のループインデクス
         */
        protected void setIndex(int index) {
            this.index = index;
        }

        /**
         * 現在のカウンタ（1からはじまる）を設定します。
         * @param count 現在のカウンタ
         */
        protected void setCount(int count) {
            this.count = count;
        }

        /**
         * 初回ループかどうかを設定します。
         * @param first 初回の場合にtrue
         */
        protected void setFirst(boolean first) {
            this.first = first;
        }

        /**
         * 最終ループかどうかを設定します。
         * @param last 最終の場合にtrue
         */
        protected void setLast(boolean last) {
            this.last = last;
        }

        /**
         * 奇数回のループかどうかを設定します。
         * @param odd 奇数回の場合にtrue
         */
        protected void setOdd(boolean odd) {
            this.odd = odd;
        }

        /**
         * 偶数回のループかどうかを設定します。
         * @param even 偶数回の場合にtrue
         */
        protected void setEven(boolean even) {
            this.even = even;
        }

        /**
         * 現在のループインデクス（0からはじまる）を取得します。
         * @return 現在のループインデクス
         */
        public int getIndex() {
            return index;
        }

        /**
         * 現在のカウント（1からはじまる）を取得します。
         * @return 現在のカウント
         */
        public int getCount() {
            return count;
        }

        /**
         * 初回ループかどうかを返却します。
         * @return 初回ループの場合にtrue
         */
        public boolean isFirst() {
            return first;
        }

        /**
         * 最終ループかどうかを返却します。
         * @return 最終ループの場合にtrue
         */
        public boolean isLast() {
            return last;
        }

        /**
         * 現在のループが奇数回かどうかを返却します。
         * @return 奇数回の場合にtrue
         */
        public boolean isOdd() {
            return odd;
        }

        /**
         * 現在のループが偶数回かどうかを返却します。
         * @return 偶数会の場合にtrue
         */
        public boolean isEven() {
            return even;
        }
    }
}
