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

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.framework.Controllers;

import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * このクラスはトークンIDのライフサイクルを管理します。<br/>
 * トークを利用するには、DI コンテナにこのクラスが設定されている必要があります。
 * </p>
 * <h4>トークン管理機構の設定方法</h4>
 * <p>
 * トークン管理機能を有効にするには{@code TokenManager}のDIコンテナへの登録と{@link TokenHandlerInterceptor}インターセプタの設定が必要です。<br/>
 * これは下記の通り{@code Spring MVC}のコンテキストXMLに設定を行います。<br/>
 * </p>
 * <pre>
 * &lt;bean id="tokenManager" class="jp.co.ctc_g.jse.core.token.TokenManager" /&gt;
 * 
 * &lt;bean name="requestDataValueProcessor" class="jp.co.ctc_g.jse.core.framework.JseRequestDataValueProcessor" &gt;
 *   &lt;property name="requestDataValueProcessors"&gt;
 *     &lt;list&gt;
 *       &lt;ref bean="tokenRequestDataValueProcessing" /&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="tokenRequestDataValueProcessing" class="jp.co.ctc_g.jse.core.token.TokenRequestDataValueProcessing" /&gt;
 * </pre>
 * <h4>アプリケーションにおける利用方法</h4>
 * <p>
 * アプリケーションの実装者は、このパッケージで提供されるAPIではなく{@link jp.co.ctc_g.jfw.core.framework.Token}注釈をコントローラのハンドラ・メソッドに対して付与することによって機能を利用します。<br/>
 * 流れは以下の通りです。<br/>
 * </p>
 * <p>
 * 登録画面や更新画面などの入力画面を要求するリクエストを処理するハンドラ・メソッドに対して{@link jp.co.ctc_g.jfw.core.framework.Token}注釈の{@code save}属性に{@code true}を定義します。
 * <pre>
 * &#64;Controller
 * public class FooClass {
 * 
 *     &#64;Token(save = true)
 *     public String readyToCreate() {
 *     ・・・
 * </pre>
 * これによってリクエストをハンドラ・メソッドが処理する直前に{@link TokenHandlerInterceptor}インターセプタによってトークンが生成されセッションに保存されます。
 * その後ハンドラ・メソッドが実行されJSPにディスパッチされると{@link TokenRequestDataValueProcessing}の機能を通して画面のフォーム要素としてトークIDを
 * 保有するhidden要素が 挿入されます。このように生成された画面から登録、あるいは、更新要求を受け付ける際にトークンのチェックを行うため該当のハンドラ・メソッドに対して
 * {@link jp.co.ctc_g.jfw.core.framework.Token}注釈の{@code check}属性に{@code true}を定義します。
 * <pre>
 * &#64;Controller
 * public class FooClass {
 * 
 *     &#64;Token(check = true)
 *     public String create() {
 *     ・・・
 * </pre>
 * トークンの保存処理と同様に、ハンドラ・メソッドにおけるDBへの登録、更新処理などが実行されるまえにトークンのチェック処理が実行され、チェック結果に従ってハンドラ・メソッドを実行するか
 * どうかが決定されます。
 * </p>
 * <h4>トークID生成器の切り替え方法について</h4>
 * <p>
 * デフォルトではトークンID生成器の実装として{@link DefaultIdGenerator}が設定されます。これを変更するには{@link IdGenerator}インタフェースの実装クラスを定義し、
 * {@code Spring MVC}のコンテキストXMLファイルに下記の通り設定を行います。
 * </p>
 * <pre>
 * &lt;bean id="tokenManager" class="jp.co.ctc_g.jse.core.token.TokenManager" &gt;
 *     &lt;property name="idGenerator"&gt;
 *         &lt;ref bean="fooIdGenerator" /&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="fooIdGenerator" class="jp.co.ctc_g.jse.core.token.FooIdGenerator" &gt;
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see TokenProcessor
 * @see IDGenerator
 */
public class TokenManager implements InitializingBean {

    /**
     * {@TokenManger}をリクエストスコープにセットする際に利用するキー文字列
     */
    public static final String TOKEN_MANAGER_ATTRIBUTE_KEY = TokenManager.class.getName() + ".TOKEN_MANAGER";

    /**
     * トークンIDを送る際のパラメータ・キー文字列
     */
    public static final String TOKEN_ATTRIBUTE_KEY = TokenManager.class.getName() + ".TOKEN";

    /** 
     * トークンIDをセッションに保存する際のキー文字列
     */
    public static final String SESSION_TOKEN_PARAMETER_NAME = "sessionToken";

    private IDGenerator idGenerator;

    private TokenProcessor sessionTokenProcessor;

    /**
     * デフォルトコンストラクタです。
     */
    public TokenManager() {}

    /**
     * <p>
     * トークンIDを生成し、指定されたスコープに対して保存します。
     * </p>
     * @param request {@link HttpServletRequest}インスタンス
     * @param scope トークンIDを保存するスコープ {@link Controllers.SCOPE_SESSION}のみ指定可能です。
     */
    void saveToken(HttpServletRequest request, String scope) {
        Args.checkNotNull(request);
        Args.checkNotNull(scope);
        TokenProcessor processor = getTokenProcessor(scope);
        synchronized (request.getSession().getId().intern()) {
             processor.saveToken(request);
        }
    }

    /**
     * <p>
     * クライアントから送信されたトークンIDと指定されたスコープに存在するトークンIDの比較を行います。<br/>
     * なお、トークンIDは比較終了後にセッションから削除されます。
     * </p>
     * @param request {@link HttpServletRequest}インスタンス
     * @param scope トークンIDを保存するスコープ {@link Controllers.SCOPE_SESSION}のみ指定可能です。
     *        {@link Controllers.SCOPE_SESSION}以外のスコープが指定された場合は、{@link InternalException}がスローされます。
     * @return トークンIDをチェックした結果不正な要求だと判断された場合 {@code false} が返却されます。
     */
    boolean isTokenValid(HttpServletRequest request, String scope) {
        Args.checkNotNull(request);
        TokenProcessor processor = getTokenProcessor(scope);
        synchronized (request.getSession().getId().intern()) {
             return processor.isTokenValid(request);
        }
    }

    /**
     * <p>
     * クライアントから送信されたトークンIDと指定されたスコープに存在するトークンIDの比較を行います。
     * {@link #isTokenValid(HttpServletRequest, String)}との違いはトークンチェック実行後にセッションに保存されている
     * トークIDのライフサイクルをコントロールできることです。
     * </p>
     * @param request {@link HttpServletRequest}インスタンス
     * @param scope トークンIDを保存するスコープ {@link Controllers.SCOPE_SESSION}のみ指定可能です。
     *        {@link Controllers.SCOPE_SESSION}以外のスコープが指定された場合は、{@link InternalException}がスローされます。
     * @param reset {@code reset}に{@code true}を設定するとチェック実行後に
     * トークンIDが削除され(デフォルトの動作)、{@code false}を設定するとトークンIDはセッションに残り続けます。
     * @return トークンIDをチェックした結果不正な要求だと判断された場合 {@code false} が返却されます。
     */
    boolean isTokenValid(HttpServletRequest request, String scope, boolean reset) {
        Args.checkNotNull(request);
        TokenProcessor processor = getTokenProcessor(scope);
        synchronized (request.getSession().getId().intern()) {
             return processor.isTokenValid(request, reset);
        }
    }

    String getToken(HttpServletRequest request, String scope) {
        Args.checkNotNull(request);
        TokenProcessor processor = getTokenProcessor(scope);
        return processor.getCurrentToken(request);
    }

    Map<String, String> getTokenMap(HttpServletRequest request) {
        Args.checkNotNull(request);
        String sessionToken = getTokenProcessor(Controllers.SCOPE_SESSION).getCurrentToken(request);
        Map<String, String> tokenMap = null;
        if (!Strings.isEmpty(sessionToken))
            tokenMap = Maps.hash(SESSION_TOKEN_PARAMETER_NAME, sessionToken);
        return tokenMap;
    }

    /**
     * <p>
     * トークIDの生成器である{@link IDGenerator}インタフェースの実装クラスを設定します。
     * </p>
     * @param idGenerator トークンIDの生成器である{@link IDGenerator}インスタンス
     */
    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * <p>
     * {@link TokenProcessor}インスタンスを設定します。<br/>
     * デフォルトでは{@link SessionTokenProcessor}のインスタンスが設定されていますが、これを上書きします。
     * </p>
     * @param tokenProcessor {@link TokenProcessor}インスタンス
     */
    public void setSessionTokenProcessor(TokenProcessor tokenProcessor) {
        this.sessionTokenProcessor = tokenProcessor;
    }

    private TokenProcessor getTokenProcessor(String scope) {
        if (Controllers.SCOPE_SESSION.equals(scope)) {
            return sessionTokenProcessor;
        } else {
            throw new InternalException(TokenManager.class, "E-TOKEN#0001");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (idGenerator == null)
            idGenerator = new IDGenerator.DefaultIdGenerator();

        if (sessionTokenProcessor == null)
            sessionTokenProcessor = new SessionTokenProcessor(idGenerator);
    }
}
