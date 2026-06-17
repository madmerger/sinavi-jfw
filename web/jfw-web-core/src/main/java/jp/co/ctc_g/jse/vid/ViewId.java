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


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <p>
 * このクラスは、画面IDを表現するバリューオブジェクトです。
 * </p>
 * <h4>手動での画面ID設定</h4>
 * <p>
 * 通常の利用においては、画面IDはJSPで定義されると想定しています。
 * ですので、このクラスを利用して明示的に画面IDを生成することは稀かもしれません。
 * ただし、<strong>モーダルダイアログを利用するなどして、
 * サーバに通知することなく画面が遷移する場合、
 * ユーザに表示されている画面IDとサーバが管理している画面IDが一致しなくなる可能性があります</strong>。
 * そのような場合は、明示的に画面IDの追加や削除をする必要があります。
 * </p>
 * <pre class="brush:java">
 * ViewId vid = new ViewId("VID#0001");
 * ViewId.is(vid, request);
 * </pre>
 * <p>
 * このようにすると、現在表示されている画面
 * （より正確には、この処理が完了した際にユーザに表示されている画面）のIDを設定できます。
 * </p>
 * <h4>画面ID管理方式</h4>
 * <p>
 * 画面IDは{@link javax.servlet.http.HttpSession セッション}内のスタックで管理されています。
 * {@link ViewId#is(ViewId, HttpServletRequest)}がコールされるたびに、
 * 指定された画面IDオブジェクトがスタックの上位に配置されます。
 * ただし、指定された画面IDオブジェクトが既にスタックの中に存在していた場合、
 * 当該画面IDまでにスタックに積まれた画面IDオブジェクトを破棄します。
 * これは、画面を戻る処理であり、パンクズリスト表示において重要な挙動です。
 * </p>
 * <p>
 * 既にスタック内に存在する画面IDが再び指定された場合の挙動は、上記の通りですが、
 * この時、デフォルトでは既存画面IDを新しい画面IDで<strong>上書きしません</strong>。
 * もし、上書きが必要であれば、クラスコンフィグオーバライドを利用してください。
 * </p>
 * <h4>等価性について</h4>
 * <p>
 * 画面IDが同じであるということは、{@link #equals(Object)}が<code>true</code>であることです。
 * より具体的には、コンストラクタに渡される画面ID文字列が等しいことを意味します。
 * また、{@link #hashCode()}についても、画面ID文字列が利用されています（よって、画面IDオブジェクトはハッシュキーとして適当な実装です）。
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
 *    <td>container_key</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      画面IDオブジェクトのスタックを{@link HttpSession セッション}で管理する際の属性キーです。
 *    </td>
 *    <td>
 *     jp.co.ctc_g.jfw.vid.ViewId.CONTAINER_KEY
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>queries_join_word</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      リクエストパラメータを結合する際の文字列です。
 *    </td>
 *    <td>
 *     &nbsp;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>override_if_container_has_already_managed_same_view_id</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      既にスタック内に存在する画面IDが再び指定された場合に、
 *      その画面IDを新しい画面IDで上書きするかどうかを指定します。
 *    </td>
 *    <td>
 *     false
 *    </td>
 *   </tr>
 *  </tbody>
 * </table>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class ViewId implements Serializable {

    private static final long serialVersionUID = 1832342195395194537L;

    private static final String ALL_PARAMETERS_INCLUDE_MARK = "*";

    /**
     * 画面IDを保持しているコンテナオブジェクトが、
     * {@link HttpSession}に保存される際に利用する属性キーです。
     */
    public static final String VIEW_ID_CONTAINER_KEY;

    /**
     * {@link HttpSession}に保存される際に利用する画面IDの属性キーです。
     */
    public static final String ID = "id";

    /**
     * {@link HttpSession}に保存される際に利用するラベルの属性キーです。
     */
    public static final String LABEL = "label";

    /**
     * {@link HttpSession}に保存される際に利用するURLの属性キーです。
     */
    public static final String URL = "url";

    /**
     * {@link HttpSession}に保存される際に利用するQuery文字列の属性キーです。
     */
    public static final String QUERY = "query";

    /**
     * リクエストパラメータクエリ結合用の文字です。デフォルトは &amp; です。
     */
    public static final String QUERIES_JOIN_WORD;

    private static final String PERCENT_ESCAPE_ENCODING_FOR_QUERY;

    private static final boolean OVERRIDE_THE_SAME_ONE;

    private static ViewIdStore.Factory factory = new ViewIdStore.Factory();

    static {
        Config c = WebCoreInternals.getConfig(ViewId.class);
        VIEW_ID_CONTAINER_KEY = c.find("container_key");
        QUERIES_JOIN_WORD = c.find("queries_join_word");
        OVERRIDE_THE_SAME_ONE = Boolean.valueOf(c
            .find("override_if_container_has_already_managed_same_view_id"));
        PERCENT_ESCAPE_ENCODING_FOR_QUERY = c.find("percent_escape_encoding_for_query");
    }

    /*
     * 画面ID
     */
    private String id;

    /*
     * パンクズで表示される、この画面の名前
     */
    private String label;

    /*
     * パンクズからリクエストされる、この画面のURL
     */
    private String url;

    /*
     * パンクズからリクエストされる際の付帯パラメータ
     */
    private Map<String, String[]> params;

    /*
     * パンクズとして表示されるかどうか
     */
    private boolean pankuzu;

    /*
     * この画面IDオブジェクトが凍結済みかどうか
     */
    private boolean frozen;

    /* 
     * 生成するURLのクエリ文字列に含めるパラメータを指定する正規表現
     */
    private String include;

    /* 
     * 生成するURLのクエリ文字列に含めないパラメータを指定する正規表現
     */
    private String exclude;

    /**
     * デフォルトコンストラクタです。
     */
    public ViewId() {}

    /**
     * 指定されたIDを画面IDとして、このクラスのインスタンスを生成します。
     * 画面IDに表示上意味のない文字を指定することはできません。
     * @param id 画面ID
     */
    public ViewId(String id) {

        Args.checkNotBlank(id);
        this.id = id;
    }

    /**
     * 指定された<code>ViewId</code>オブジェクトを現在の画面IDとして登録します。
     * @param self 現在の画面IDオブジェクト
     * @param request 現在処理中のリクエスト
     */
    public static void is(ViewId self, HttpServletRequest request) {

        is(self, factory.create(request));
    }

    private static void is(ViewId self, ViewIdStore store) {

        Args.checkNotNull(self);
        self.freeze();
        synchronized (store.semaphore()) {
            LinkedList<ViewId> ids = store.find();
            assert ids != null;
            if (ids.contains(self)) {
                int index = ids.indexOf(self);
                for (int i = ids.size() - 1; i > index; i--) {
                    ids.remove(i);
                }
                if (OVERRIDE_THE_SAME_ONE) {
                    ids.set(index, self);
                }
            } else {
                ids.add(self);
            }
            store.store(ids);
        }
    }

    /**
     * 現在の画面IDを返却します。
     * 初回画面の場合や、セッションが破棄されている場合など、
     * 正常な画面IDを返却できない場合は<code>null</code>を返却します。
     * @param request 現在処理中のリクエスト
     * @return 画面IDオブジェクト
     */
    public static ViewId current(HttpServletRequest request) {

        return current(factory.create(request));
    }

    private static ViewId current(ViewIdStore store) {

        ViewId id = null;
        synchronized (store.semaphore()) {
            LinkedList<ViewId> ids = store.find(false);
            id = ids != null && !ids.isEmpty() ? ids.getLast() : null;
        }
        return id;
    }

    /**
     * 画面IDの履歴を返却します。
     * 履歴は数値で示し、0は{@link ViewId#current(HttpServletRequest)}と等価です。
     * 正常な画面IDを返却できない場合は<code>null</code>を返却します。
     * @param history 履歴
     * @param request 現在処理中のリクエスト
     * @return 画面IDオブジェクト
     */
    public static ViewId history(int history, HttpServletRequest request) {

        return history(history, factory.create(request));
    }

    private static ViewId history(int history, ViewIdStore store) {

        ViewId id = null;
        synchronized (store.semaphore()) {
            LinkedList<ViewId> ids = store.find(false);
            id = (ids != null && !ids.isEmpty() && ids.size() > history) ? ids.get(ids.size()
                - history - 1) : null;
        }
        return id;
    }

    /**
     * 画面IDのイテレータを返却します。
     * このイテレータは画面IDスタックの底から順に繰り返します。
     * 有効な画面IDを含んだイテレータを返却できない場合には、空のイテレータを返却します。
     * @param request 現在処理中のリクエスト
     * @return 画面IDオブジェクトのイテレータ
     */
    public static Iterator<ViewId> iterator(HttpServletRequest request) {

        return container(request).iterator();
    }

    /**
     * 画面IDの{@link java.lang.Iterable Iterable}オブジェクトを返却します。
     * この{@link java.lang.Iterable Iterable}オブジェクトは画面IDスタックの底から順に繰り返します。
     * このメソッドは<code>null</code>を返却することはなく、
     * 有効な画面IDを含んだ{@link java.lang.Iterable Iterable}オブジェクトを返却できない場合には、
     * 空の{@link java.lang.Iterable Iterable}オブジェクトを返却します。
     * @param request 現在処理中のリクエスト
     * @return 画面IDオブジェクトのイテレータ
     */
    public static Iterable<ViewId> container(HttpServletRequest request) {

        return container(factory.create(request));
    }

    @SuppressWarnings("unchecked")
    private static Iterable<ViewId> container(ViewIdStore store) {

        Iterable<ViewId> iterable = null;
        synchronized (store.semaphore()) {
            LinkedList<ViewId> ids = store.find(false);
            iterable = ids != null ?
                    Collections.unmodifiableList((LinkedList<ViewId>) ids.clone()) :
                    Collections.<ViewId>emptyList();
        }
        return iterable;
    }

    /**
     * 画面IDスタックのサイズを返却します。
     * {@link HttpSession}内に画面IDスタックが存在しない場合、サイズ0を返却します。
     * @param request 現在処理中のリクエスト
     * @return 画面IDオブジェクトのイテレータ
     */
    public static int size(HttpServletRequest request) {

        return size(factory.create(request));
    }

    private static int size(ViewIdStore store) {

        int size = 0;
        synchronized (store.semaphore()) {
            LinkedList<ViewId> ids = store.find(false);
            size = ids != null ? ids.size() : 0;
        }
        return size;
    }

    /**
     * {@link HttpSession}に登録されている画面IDを全て削除します。
     * @param request 現在処理中のリクエスト
     */
    public static void clear(HttpServletRequest request) {

        clear(factory.create(request));
    }

    private static void clear(ViewIdStore store) {

        synchronized (store.semaphore()) {
            store.remove();
        }
    }

    /**
     * {@link HttpSession}に登録されている指定された画面IDの画面IDオブジェクトを削除します。
     * @param request 現在処理中のリクエスト
     * @param id 画面ID
     */
    public static void clear(HttpServletRequest request, String id) {
        
        clear(factory.create(request), id);
    }
    
    private static void clear(ViewIdStore store, String id) {
        synchronized (store.semaphore()) {
            store.remove(id);
        }
    }
    
    /**
     * このインスタンスに、ラベル、URL、リクエストパラメータを設定します。
     * 既に設定されている場合は、その項目を無視します。
     * 例えば、既にラベルが{@link #setLabel(String)}により設定されていた場合、
     * このメソッドにより上書きされることはありません。
     * ラベルが未設定の場合、ラベルにはこのインスタンスのidプロパティが設定されます。
     * URLが未設定の場合、リクエストキー&quot;javax.servlet.forward.request_uri&quot;に対応する属性値が設定されます。
     * リクエストパラメータが未設定の場合、{@link HttpServletRequest#getParameterMap()}が設定されます。
     * @param request 現在のリクエスト
     * @return このインスタンス
     */
    public ViewId fill(HttpServletRequest request) {

        if (Strings.isBlank(id)) {
            throw new InternalException(ViewIdDefinitionTag.class, "E-VID#0004");
        }
        // ラベルが指定されていない場合はIDにする
        label = label != null ? label : id;
        // URLが指定されていない場合は、現在のURL
        url = url != null ? url : (String) request
            .getAttribute("javax.servlet.forward.request_uri");
        // パラメータが指定されていない、かつ、付加するパラメータがincludeによって指定されている場合は現在のパラメータを設定
        if (params == null) {
            Map<String, String[]> requestedParameters = new HashMap<String, String[]>(request.getParameterMap());
            if (requestedParameters.size() > 0) {
                if (!Strings.isEmpty(exclude)) {
                    params = excludeParameter(requestedParameters);
                } else if (!Strings.isEmpty(include)) {
                    params = includeParameter(requestedParameters);
                } else {
                    params = new HashMap<String, String[]>();
                }
            }
        }
        return this;
    }

    private Map<String, String[]> excludeParameter(Map<String, String[]> requestedParameters) {
        Map<String, String[]> storedParameters = new HashMap<String, String[]>();
        List<String> keys = Arrays.asList(exclude.split(","));
        Set<String> sets = requestedParameters.keySet();
        for (String key : sets) {
            if (!keys.contains(key)) {
                storedParameters.put(key, (String[]) requestedParameters.get(key));
            }
        }
        return storedParameters;
    }

    private Map<String, String[]> includeParameter(Map<String, String[]> requestedParameters) {
        if (ALL_PARAMETERS_INCLUDE_MARK.equals(include)) {
            return requestedParameters;
        } else {
            Map<String, String[]> storedParameters = new HashMap<String, String[]>();
            List<String> keys = Arrays.asList(include.split(","));
            Set<String> sets = requestedParameters.keySet();
            for (String key : sets) {
                if (keys.contains(key)) {
                    storedParameters.put(key, (String[]) requestedParameters.get(key));
                }
            }
            return storedParameters;
        }
    }

    /**
     * このインスタンスを凍結します。
     * 凍結されたインスタンスは、
     * 例えば{@link #setId(String)}や{@link #setLabel(String)}などの設定メソッドを利用しても
     * 状態を変更することはできません。
     * 凍結されているにもかかわらず状態を変更しようとした場合、
     * {@link InternalException}が例外コード<code>E-VID#0001</code>を伴って放出されます。
     * @see #checkFrozen()
     * @see #isFrozen()
     */
    protected void freeze() {

        this.frozen = true;
    }

    /**
     * このインスタンスが凍結状態かどうかをチェックし、
     * 凍結状態であった場合には、
     * {@link InternalException}に例外コード<code>E-VID#0001</code>を伴って放出します。
     * @throws {@link InternalException} 既に凍結状態であった場合
     */
    protected void checkFrozen() {

        if (isFrozen()) {
            throw new InternalException(ViewId.class, "E-VID#0001", Maps.hash("id", id));
        }
    }

    /**
     * このインスタンスが既に凍結されているかどうかを返却します。
     * @return 凍結されている場合はtrue
     */
    public boolean isFrozen() {

        return frozen;
    }

    /**
     * 設定されている画面IDを返却します。
     * @return 画面ID
     */
    public String getId() {

        return id;
    }

    /**
     * 画面IDを設定します。
     * インスタンスが既に凍結済みの場合は{@link InternalException}が発生します。
     * @param id 画面ID
     */
    public void setId(String id) {

        checkFrozen();
        this.id = id;
    }

    /**
     * 設定されているラベルを返却します。
     * ラベルは、画面IDに関連付けられたユーザ表示文字列です。
     * 主としてパンクズ表示に利用されます。
     * @return ラベル
     */
    public String getLabel() {

        return label;
    }

    /**
     * ラベルを設定します。
     * ラベルは、画面IDに関連付けられたユーザ表示文字列です。
     * 主としてパンクズ表示に利用されます。
     * インスタンスが既に凍結済みの場合は{@link InternalException}が発生します。
     * @param label ラベル
     */
    public void setLabel(String label) {

        checkFrozen();
        this.label = label;
    }

    /**
     * 設定されているURLを返却します。
     * 主としてパンクズリンク押下時のリクエスト先URLとして利用されます。
     * @return URL
     */
    public String getUrl() {

        return url;
    }

    /**
     * URLを設定します。
     * 主としてパンクズリンク押下時のリクエスト先URLとして利用されます。
     * インスタンスが既に凍結済みの場合は{@link InternalException}が発生します。
     * @param url URL
     */
    public void setUrl(String url) {

        checkFrozen();
        this.url = url;
    }

    /**
     * 設定されているリクエストパラメータを返却します。
     * @return リクエストパラメータ
     */
    public Map<String, String[]> getParams() {

        return Collections.unmodifiableMap(params);
    }

    /**
     * 設定されているリクエストパラメータのクエリ文字列表現を返却します。
     * クエリ文字列は、あらかじめ先頭に?を付加した状態で返却します。
     * もしクエリ文字列が空文字の場合、?は付加せず、空文字を返却します。
     * このメソッドは、リクエストパラメータをパーセントエスケープ（URLエンコード）します。
     * @return リクエストパラメータのクエリ文字列表現
     * @see java.net.URLEncoder
     */
    public String getQuery() {

        return getQuery(true);
    }

    /**
     * 設定されているリクエストパラメータのクエリ文字列表現を返却します。
     * クエリ文字列は、あらかじめ先頭に?を付加した状態で返却します。
     * もしクエリ文字列が空文字の場合、?は付加せず、空文字を返却します。
     * @param needToApplyPercentEscapeToQueryParameters パーセントエスケープ（URLエンコード）するかどうか
     * @return パーセントエスケープ済みのリクエストパラメータのクエリ文字列表現
     * @see java.net.URLEncoder
     */
    public String getQuery(boolean needToApplyPercentEscapeToQueryParameters) {

        if (Maps.isEmpty(params))
            return "";
        List<String> queries = new LinkedList<String>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                if (needToApplyPercentEscapeToQueryParameters) {
                    try {
                        queries.add(Strings.joinBy("=", URLEncoder
                                .encode(key, PERCENT_ESCAPE_ENCODING_FOR_QUERY), URLEncoder
                                .encode(value, PERCENT_ESCAPE_ENCODING_FOR_QUERY)));
                    } catch (UnsupportedEncodingException e) {
                        throw new InternalException(ViewId.class, "E-VID#0005",
                                Maps.hash("encoding", PERCENT_ESCAPE_ENCODING_FOR_QUERY));
                    }
                } else {
                    queries.add(Strings.joinBy("=", key, value));
                }
            }
        }
        String query = Strings.joinBy(QUERIES_JOIN_WORD, queries);
        return query.length() > 0 ? "?" + query : "";
    }

    /**
     * リクエストパラメータを設定します。
     * インスタンスが既に凍結済みの場合は{@link InternalException}が発生します。
     * @param params リクエストパラメータ
     */
    public void setParams(HashMap<String, String[]> params) {

        checkFrozen();
        this.params = params;
    }

    /**
     * このインスタンスが、パンクズとして表示されるかどうかを返却します。
     * @return パンクズとして表示される場合はtrue
     */
    public boolean isPankuzu() {

        return pankuzu;
    }

    /**
     * このインスタンスが、パンクズとして表示されるかどうかを返却します。
     * インスタンスが既に凍結済みの場合は{@link InternalException}が発生します。
     * @param pankuzu パンクズとして表示される場合はtrue
     */
    public void setPankuzu(boolean pankuzu) {

        checkFrozen();
        this.pankuzu = pankuzu;
    }

    /**
     * 生成するURLのクエリ文字列に含めるパラメータを指定する正規表現を設定します。
     * @param include 生成するURLのクエリ文字列に含めるパラメータを指定する正規表現
     */
    public void setInclude(String include) {
        checkFrozen();
        this.include = include;
    }

    /**
     * 生成するURLのクエリ文字列に含めないパラメータを指定する正規表現を設定します。
     * @param exclude 生成するURLのクエリ文字列に含めないパラメータを指定する正規表現
     */
    public void setExclude(String exclude) {
        checkFrozen();
        this.exclude = exclude;
    }

    /**
     * {@inheritDoc}
     * このインスタンスと指定されたインスタンスが等しいかどうかを検証します。
     * 実装の詳細については、このクラスのクラス説明を参照してください。
     */
    @Override
    public boolean equals(Object obj) {

        boolean eq = false;
        if (obj != null && obj instanceof ViewId) {
            ViewId another = (ViewId) obj;
            String anotherId = another.getId();
            assert anotherId != null;
            eq = id.equals(anotherId);
        }
        return eq;
    }

    /**
     * {@inheritDoc}
     * このインスタンスと指定されたインスタンスが等しいかどうかを検証します。
     * 実装の詳細については、このクラスのクラス説明を参照してください。
     */
    @Override
    public int hashCode() {

        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }

}
