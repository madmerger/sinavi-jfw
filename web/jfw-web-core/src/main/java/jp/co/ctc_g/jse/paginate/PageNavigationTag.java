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

package jp.co.ctc_g.jse.paginate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Lists;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.PartialList;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.internal.WebCoreInternals;
import jp.co.ctc_g.jse.core.taglib.ParameterAware;
import jp.co.ctc_g.jse.core.util.web.TagUtils;

import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * <p>
 * このクラスは、ページングの際のナビゲーションを表示するためのタグサポートクラスです。
 * ナビゲーションとは、
 * </p>
 * <pre>
 * &lt;&lt; &lt; 1 2 3 4 5 ... &gt; &gt;&gt;
 * </pre>
 * <p>
 * といったインタフェースを指します。
 * </p>
 * <h4>用語の定義</h4>
 * <p>
 * ページングの際のユーザインタフェース要素を適切に表現できるように、
 * 各要素に対して名前を与えておきます。
 * </p>
 * <table>
 *  <thead>
 *   <tr>
 *    <th>デフォルト記号</th>
 *    <th>名前</th>
 *    <th>役割</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>&lt;&lt;</td>
 *    <td>先頭ページリンク</td>
 *    <td>最初のページをリクエストするリンクです。</td>
 *   </tr>
 *   <tr>
 *    <td>&lt;</td>
 *    <td>前ページリンク</td>
 *    <td>1つ前のページをリクエストするリンクです。</td>
 *   </tr>
 *   <tr>
 *    <td>1 や 2 などの数字</td>
 *    <td>ページ番号リンク</td>
 *    <td>クリックすることでその番号のページをリクエストするリンクです。</td>
 *   </tr>
 *   <tr>
 *    <td>...</td>
 *    <td>オミッション</td>
 *    <td>
 *      ページ数が最大表示可能ページ数よりも多いことを示す記号です。
 *      表示される位置により、前オミッション/後オミッション、と区別することにします。
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>&gt;</td>
 *    <td>次ページリンク</td>
 *    <td>1つ後のページをリクエストするリンクです。</td>
 *   </tr>
 *   <tr>
 *    <td>&gt;</td>
 *    <td>末尾ページリンク</td>
 *    <td>最後のページをリクエストするリンクです。</td>
 *   </tr>
 *  </tbody>
 * </table>
 * <h4>ページングの挙動</h4>
 * <p>
 * ページングの挙動について説明します。
 * </p>
 * <dl>
 *  <dt>最初のページが表示されている場合</dt>
 *  <dd>
 *      もし、ページ数が最大表示可能ページ数より少ないのであれば、
 *      全てのページ番号を表示します。
 *      その際、先頭ページリンク、前ページリンク、現在のページ番号リンク、次ページリンク、末尾ページリンクはクリック不可能な状態となります。
 *      オミッションも表示されません。
 *      もし、ページ数が最大表示可能ページ数よりも多いのであれば、
 *      最大表示可能ページ数分のページ番号リンクが表示され、オミッションが付加れされます。
 *      その際、先頭ページリンク、前ページリンク、現在のページ番号リンクは、クリック不可能な状態で表示されます。
 *  </dd>
 *  <dt>後に表示されていないページがある場合</dt>
 *  <dd>
 *      最大表示可能ページ数分のページ番号リンクが表示され、後オミッションが付加れされます。
 *      その際、先頭ページリンク、前ページリンク、現在のページ番号リンク以外は、クリック可能な状態で表示されます。
 *  </dd>
 *  <dt>前後に表示されていないページがある場合</dt>
 *  <dd>
 *      最大表示可能ページ数分のページ番号リンクが表示され、オミッションが付加れされます。
 *      その際、現在のページ番号リンク以外は、クリック可能な状態で表示されます。
 *      また、オミッションも前後に付加されます。
 *  </dd>
 *  <dt>前に表示されていないページがある場合</dt>
 *  <dd>
 *      最大表示可能ページ数分のページ番号リンクが表示され、前オミッションが付加れされます。
 *      ただし、最大表示可能ページ数よりも、「全てのページ数 - 現在ページ番号」が小さい場合には、
 *      後者の数値が優先されます。つまり、表示可能ページ数が5ページ/全部で10ページ/現在8ページ目を表示中の場合、
 *      <strong>表示されるページ番号は、6ページから10ページです。</strong>。
 *      ただし、表示可能件数以下のページ数が表示されることはありません。
 *      次ページリンク、末尾ページリンク、現在のページ番号リンク以外は、クリック可能な状態で表示されます。
 *  </dd>
 *  <dt>最後のページが表示されている場合</dt>
 *  <dd>
 *      もし、ページ数が最大表示可能ページ数より少ないのであれば、
 *      全てのページ番号リンクを表示します。
 *      その際、先頭ページリンク、前ページリンク、現在のページ番号リンク、次ページリンク、末尾ページリンクはクリック不可能な状態となります。
 *      オミッションも表示されません。
 *      もし、ページ数が最大表示可能ページ数よりも多いのであれば、
 *      最大表示可能ページ数の半分のページ数が表示されます（切り上げ）、前オミッションが付加れされます。
 *      その際、次ページリンク、末尾ページリンク、現在のページ番号リンクは、クリック不可能な状態で表示されます。
 *  </dd>
 *   <dt>前後に表示されていないページがなく、先頭でも末尾でもない場合</dt>
 *  <dd>
 *      最大表示可能ページ数分のページ番号リンクが表示されます。
 *      先頭ページリンク、前ページリンク、次ページリンク、末尾ページリンク、ページ番号リンクはクリック可能な状態で表示されます。
 *      現在のページ番号リンクだけは、クリック不可能な状態で表示されます。
 *  </dd>
 * </dl>
 *
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
 *    <td>display_count</td>
 *    <td>{@link java.lang.Integer}</td>
 *    <td>
 *     表示するページ番号リンクの最大数です。
 *    </td>
 *    <td>
 *     10
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>page_parameter_name</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     リクエストパラメータに付加されるページ番号のパラメータ名です。
 *    </td>
 *    <td>
 *     pageNumber
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>page_number_variable_name</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     ページナビゲーションタグ内でページ番号を参照するための変数名です。
 *    </td>
 *    <td>
 *     page
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_head</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     先頭ページリンクの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     &lt;&lt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_previous</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     前ページリンクの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     &lt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_page</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     ページリンクの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     ${page}
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_next</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     次ページリンクの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     &gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_tail</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     末尾ページリンクの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     &gt;&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>navigation_omission</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     オミッションの文字列です。HTMLとして解釈されます。
 *    </td>
 *    <td>
 *     ...
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_link</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     HTMLでのリンクを作成する際に利用するテンプレート文字列です。
 *    </td>
 *    <td>
 *     &lt;a href="${href}"&gt;${content}&lt;/a&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_head</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     先頭ページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_head"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_previous</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     次ページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_previous"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_page</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     非選択状態のページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_page"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_page_selected</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     選択状態のページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_page_selected"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_next</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     次ページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_next"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_tail</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     末尾ページリンクのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_tail"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *   <tr>
 *    <td>template_omission</td>
 *    <td>{@link java.lang.String}</td>
 *    <td>
 *     オミッションのテンプレートです。
 *    </td>
 *    <td>
 *     &lt;span class="jfw_paginate_navi_omission"&gt;${content}&lt;/span&gt;
 *    </td>
 *   </tr>
 *  </tbody>
 * </table>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class PageNavigationTag extends AbstractHtmlElementTag implements ParameterAware {

    private static final long serialVersionUID = 1L;

    /**
     * 表示するページ番号リンクの最大数です。
     */
    protected static final Integer DISPLAY_COUNT;

    /**
     * リクエストパラメータに付加されるページ番号のパラメータ名です。
     */
    protected static final String PAGE_PARAMETER_NAME;

    /**
     * ページナビゲーションタグ内でページ番号を参照するための変数名です。
     */
    protected static final String PAGE_NUMBER_VARIABLE_NAME;

    /**
     * 先頭ページリンクの文字列です。
     */
    protected static final String NAVIGATION_HEAD;

    /**
     * 前ページリンクの文字列です。
     */
    protected static final String NAVIGATION_PREVIOUS;

    /**
     * ページリンクの文字列です。
     */
    protected static final String NAVIGATION_PAGE;

    /**
     * 次ページリンクの文字列です。
     */
    protected static final String NAVIGATION_NEXT;

    /**
     * 末尾ページリンクの文字列です。
     */
    protected static final String NAVIGATION_TAIL;

    /**
     * オミッション文字列です。
     */
    protected static final String NAVIGATION_OMISSION;

    /**
     * HTMLでのリンク（アンカー）のテンプレートです。
     */
    protected static final String TEMPLATE_LINK;

    /**
     * 先頭ページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_HEAD;

    /**
     * 前ページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_PREVIOUS;

    /**
     * 非選択状態のページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_PAGE;

    /**
     * 選択状態のページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_PAGE_SELECTED;

    /**
     * 次ページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_NEXT;

    /**
     * 末尾ページリンクのテンプレートです。
     */
    protected static final String TEMPLATE_TAIL;

    /**
     * オミッションのテンプレートです。
     */
    protected static final String TEMPLATE_OMISSION;

    /**
     * このナビゲーションタグで繰り返す対象となるリストです。
     */
    private PartialList<?> partial;

    /**
     * ページリンクの接続先actionです。
     * urlが指定されていない場合は、必ず指定する必要があります。
     */
    private String action;

    /**
     * ページリンクの接続先URLです。
     * actionが指定されていない場合は、必ず指定する必要があります。
     */
    private String url;

    /**
     * 1つのナビゲーションタグで表示されるページリンクの最大数です。
     */
    private Integer displayCount;

    /**
     * ページリンクのリクエストパラメータにページ番号を付加する際の、リクエストパラメータ名です。
     */
    private String param;

    /**
     * ネストされた{@link JspFragment}における、
     * 現在の出力対象のページ番号を格納する変数名です。
     */
    private String var;

    /**
     * ネストされた{@link PageNavigationParameterTag}により指定された追加リクエストパラメータ群です。
     */
    private Map<String, String[]> additionalParameters;

    /**
     * 先頭ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment head;

    /**
     * 前ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment previous;

    /**
     * ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment page;

    /**
     * オミッションのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment omission;

    /**
     * 次ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment next;

    /**
     * 末尾ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    private transient JspFragment tail;

    static {
        Config c = WebCoreInternals.getConfig(PageNavigationTag.class);
        DISPLAY_COUNT = Integer.parseInt(c.find("display_count"));
        PAGE_PARAMETER_NAME = c.find("page_parameter_name");
        PAGE_NUMBER_VARIABLE_NAME = c.find("page_number_variable_name");
        NAVIGATION_HEAD = c.find("navigation_head");
        NAVIGATION_PREVIOUS = c.find("navigation_previous");
        NAVIGATION_PAGE = c.find("navigation_page");
        NAVIGATION_NEXT = c.find("navigation_next");
        NAVIGATION_TAIL = c.find("navigation_tail");
        NAVIGATION_OMISSION = c.find("navigation_omission");
        TEMPLATE_LINK = c.find("template_link");
        TEMPLATE_HEAD = c.find("template_head");
        TEMPLATE_PREVIOUS = c.find("template_previous");
        TEMPLATE_PAGE = c.find("template_page");
        TEMPLATE_PAGE_SELECTED = c.find("template_page_selected");
        TEMPLATE_NEXT = c.find("template_next");
        TEMPLATE_TAIL = c.find("template_tail");
        TEMPLATE_OMISSION = c.find("template_omission");
    }

    /**
     * デフォルトコンストラクタ。
     */
    public PageNavigationTag() {
        super();
        reset();
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
     * {@inheritDoc}
     */
    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        if (Lists.isEmpty(partial)) return EVAL_PAGE;
        int beginIndex = 0;
        int endIndex = 0;
        // ToDo tagWriterを用いた記述に後ほど、改訂。
        // TagWriter tagWriter = createTagWriter();
        if (partial.getPartCount() <= displayCount) {
            beginIndex = 1;
            endIndex = partial.getPartCount();
        } else {
            // 真中
            int baseDivision = Math.round((float) displayCount / 2);
            // 表示するべき最初のページ
            beginIndex = partial.getPartIndex() - baseDivision + 1;
            if (beginIndex < 1) beginIndex = 1;
            // 表示するべき最後のページ
            endIndex = displayCount + beginIndex - 1;
            if (endIndex > partial.getPartCount()) {
                endIndex = partial.getPartCount();
                beginIndex = endIndex - displayCount + 1;
            }
        }
        // 最初
        additionalParameters.put(param, new String[] {String.valueOf(1)});
        renderHead(beginIndex,endIndex, computeUrl());
        // 前へ
        additionalParameters.put(param, new String[] {
            String.valueOf(partial.getPartIndex() > 1 ? partial.getPartIndex() - 1 : 1)
        });
        renderPrevious(beginIndex,endIndex, computeUrl());
        // 省略記号(omission)を描画
        renderHeadOmission(beginIndex, endIndex);
        // ページ
        for (int index = beginIndex; index <= endIndex; index++) {
            additionalParameters.put(param, new String[] {String.valueOf(index)});
            renderPage(beginIndex, endIndex, index, computeUrl());
        }
        // 省略記号(omission)を描画
        renderTailOmission(beginIndex, endIndex);
        // 次へ
        additionalParameters.put(param, new String[] {
                String.valueOf(partial.getPartIndex() < partial.getPartCount() ?
                        partial.getPartIndex() + 1 : partial.getPartCount())
        });
        renderNext(beginIndex, endIndex, computeUrl());
        // 最後
        additionalParameters.put(param, new String[] {String.valueOf(partial.getPartCount())});
        renderTail(beginIndex, endIndex, computeUrl());
        // ページ番号削除
        additionalParameters.remove(param);
        // 処理終了なのでクリーン
        reset();
        return EVAL_PAGE;
    }

    private String computeUrl() throws JspException {
        if (Strings.isEmpty(url) && Strings.isEmpty(action)) {
            throw new InternalException(PageNavigationTag.class, "E-PAGINATE#0001");
        }
        String href = null;
        String linkUrl = Strings.isEmpty(action) ? TagUtils.processUrl(url, getRequestContext(), pageContext) : TagUtils.processAction(action, getRequestContext(), pageContext);
        href = TagUtils.createUrl(linkUrl, additionalParameters, pageContext, false, false);
        return href;
    }

    /**
     * 先頭へリンクを描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @param href URL
     * @throws JspException 予期しない例外
     */
    protected void renderHead(int beginIndex, int endIndex, String href) throws JspException {
        String content = invokeFragmentIfNeeded(head, NAVIGATION_HEAD);
        String html = partial.getPartIndex() <= 1 ?
                createUnlinkedHtml(TEMPLATE_HEAD, content) :
                createLinkedHtml(TEMPLATE_HEAD, href, content);
        renderInternal(html);
    }

    /**
     * 前へリンクを描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @param href URL
     * @throws JspException 予期しない例外
     */
    protected void renderPrevious(int beginIndex, int endIndex, String href) throws JspException {
        String content = invokeFragmentIfNeeded(previous, NAVIGATION_PREVIOUS);
        String html = partial.getPartIndex() <= 1 ?
                createUnlinkedHtml(TEMPLATE_PREVIOUS, content) :
                createLinkedHtml(TEMPLATE_PREVIOUS, href, content);
        renderInternal(html);
    }

    /**
     * ページリンクを描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @param currentIndex 現在のページ番号
     * @param href URL
     * @throws JspException 予期しない例外
     */
    protected void renderPage(
            int beginIndex,
            int endIndex,
            int currentIndex,
            String href) throws JspException {
        String content = invokeFragmentIfNeeded(page, NAVIGATION_PAGE, currentIndex);
        String html = partial.getPartIndex() == currentIndex ?
                createUnlinkedHtml(TEMPLATE_PAGE_SELECTED, content) :
                createLinkedHtml(TEMPLATE_PAGE, href, content);
        renderInternal(html);
    }

    /**
     * 次へリンクを描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @param href URL
     * @throws JspException 予期しない例外
     */
    protected void renderNext(int beginIndex, int endIndex, String href) throws JspException {
        String content = invokeFragmentIfNeeded(next, NAVIGATION_NEXT);
        String html = partial.getPartIndex() >= partial.getPartCount() ?
                createUnlinkedHtml(TEMPLATE_NEXT, content) :
                createLinkedHtml(TEMPLATE_NEXT, href, content);
        renderInternal(html);
    }

    /**
     * 最後へリンクを描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @param href URL
     * @throws JspException 予期しない例外
     */
    protected void renderTail(int beginIndex, int endIndex, String href) throws JspException {
        String content = invokeFragmentIfNeeded(tail, NAVIGATION_TAIL);
        String html = partial.getPartIndex() >= partial.getPartCount() ?
                createUnlinkedHtml(TEMPLATE_TAIL, content) :
                createLinkedHtml(TEMPLATE_TAIL, href, content);
        renderInternal(html);
    }

    /**
     * 先頭の省略記号を描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @throws JspException 予期しない例外
     */
    protected void renderHeadOmission(int beginIndex, int endIndex) throws JspException {
        if (beginIndex > 1) {
            String content = invokeFragmentIfNeeded(omission, NAVIGATION_OMISSION);
            String html = createUnlinkedHtml(TEMPLATE_OMISSION, content);
            renderInternal(html);
        }
    }

    /**
     * 最後の省略記号を描画します。
     * @param beginIndex 開始番号
     * @param endIndex 終了番号
     * @throws JspException 予期しない例外
     */
    protected void renderTailOmission(int beginIndex, int endIndex) throws JspException {
        if (endIndex < partial.getPartCount()) {
            String content = invokeFragmentIfNeeded(omission, NAVIGATION_OMISSION);
            String html = createUnlinkedHtml(TEMPLATE_OMISSION, content);
            renderInternal(html);
        }
    }

    private String invokeFragmentIfNeeded(
            JspFragment fragment,
            String defaultValue) throws JspException {
        return invokeFragmentIfNeeded(fragment, defaultValue, Integer.MIN_VALUE);
    }

    private String invokeFragmentIfNeeded(
            JspFragment fragment,
            String defaultValue,
            int currentIndex) throws JspException {
        PageContext p = pageContext;
        StringWriter w = new StringWriter();
        if (fragment != null) {
            if (currentIndex > 0) p.setAttribute(var, currentIndex);
            try {
                fragment.invoke(w);
            } catch (IOException e) {throw new JspException(e);}
            if (currentIndex > 0) p.removeAttribute(var);
        } else {
            w.append(Strings.substitute(
                    defaultValue,
                    Maps.hash(var, String.valueOf(currentIndex))));
        }
        return w.toString();
    }

    protected String createUnlinkedHtml(String template, String content) {
        String html = Strings.substitute(
                template,
                Maps.hash("content", content));
        return html;
    }

    protected String createLinkedHtml(String template, String href, String content) {
        String html = Strings.substitute(
                TEMPLATE_LINK,
                Maps.hash("content", content).map("href", href));
        html = Strings.substitute(
                template,
                Maps.hash("content", html));
        return html;
    }

    private void renderInternal(String html) throws JspException {
        try {
            pageContext.getOut().print(html);
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    /**
     * 変数を初期化します。
     */
    protected void reset() {
        partial = null;
        url = null;
        action = null;
        displayCount = DISPLAY_COUNT;
        param = PAGE_PARAMETER_NAME;
        var = PAGE_NUMBER_VARIABLE_NAME;
        head = null;
        previous = null;
        page = null;
        omission = null;
        next = null;
        tail = null;
        additionalParameters = new HashMap<String, String[]>(5);
    }

    /**
     * ネストされた{@link PageNavigationParameterTag}からコールバックを受け付けます。
     * @param name キー
     * @param value 値
     * @see ParameterAware
     */
    public void awareParameter(String name, String value) {
        awareParameter(name, new String[] {value});
    }

    /**
     * ネストされた{@link PageNavigationParameterTag}からコールバックを受け付けます。
     * @param name キー
     * @param values 値
     * @see ParameterAware
     */
    public void awareParameter(String name, String[] values) {
        assert additionalParameters != null;
        if (additionalParameters.containsKey(name)) {
            String[] current = additionalParameters.get(name);
            values = Arrays.merge(current, values);
        }
        additionalParameters.put(name, values);
    }

    /**
     * {@link PartialList} インスタンスを返却します。
     * @return リスト
     */
    public PartialList<?> getPartial() {
        return partial;
    }

    /**
     * {@link PartialList} インスタンスを設定します。
     * @param partial {@link PartialList} インスタンス
     */
    public void setPartial(PartialList<?> partial) {
        this.partial = partial;
    }

    /**
     * パスを取得します。
     * @return パス
     */
    public String getAction() {
        return action;
    }

    /**
     * パスを設定します。
     * @param action パス
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * URLを返却します。
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * URLを設定します。
     * @param url URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 表示件数を返却します。
     * @return 表示件数
     */
    public Integer getDisplayCount() {
        return displayCount;
    }

    /**
     * 表示件数を設定します。
     * @param displayCount 表示件数
     */
    public void setDisplayCount(Integer displayCount) {
        this.displayCount = displayCount;
    }

    /**
     * ページナビゲーションのリンクに付加されるパラメータを返却します。
     * @return パラメータ
     */
    public String getParam() {
        return param;
    }

    /**
     * ページナビゲーションのリンクに付加するパラメータを設定します。
     * @param param パラメータ
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * ネストされた{@link JspFragment}にエクスポートされる
     * 現在の出力対象のページ番号を格納する変数名を返却します。
     * @return 現在の出力対象のページ番号を格納する変数名
     */
    public String getVar() {
        return var;
    }

    /**
     *      * ネストされた{@link JspFragment}にエクスポートされる
     * 現在の出力対象のページ番号を格納する変数名を設定します。
     * @param var 現在の出力対象のページ番号を格納する変数名
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * 先頭ページリンクのテンプレートを変更するJSPフラグメントを返却します。
     * @return 先頭ページリンクのテンプレートを変更するJSPフラグメント
     */
    public JspFragment getHead() {
        return head;
    }

    /**
     *      * 先頭ページリンクのテンプレートを変更するJSPフラグメントを設定します。
     * @param head 先頭ページリンクのテンプレートを変更するJSPフラグメント
     */
    public void setHead(JspFragment head) {
        this.head = head;
    }

    /**
     * 前ページリンクのテンプレートを変更するJSPフラグメントを返却します。
     * @return 前ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    public JspFragment getPrevious() {
        return previous;
    }

    /**
     * 前ページリンクのテンプレートを変更するJSPフラグメントを設定します。
     * @param previous 前ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    public void setPrevious(JspFragment previous) {
        this.previous = previous;
    }

    /**
     * ページリンクのテンプレートを変更するJSPフラグメントを返却します。
     * @return ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    public JspFragment getPage() {
        return page;
    }

    /**
     * ページリンクのテンプレートを変更するJSPフラグメントを設定します。
     * @param page ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    public void setPage(JspFragment page) {
        this.page = page;
    }

    /**
     * オミッションのテンプレートを変更するJSPフラグメントを返却します。
     * @return オミッションのテンプレートを変更するJSPフラグメント
     */
    public JspFragment getOmission() {
        return omission;
    }

    /**
     * オミッションのテンプレートを変更するJSPフラグメントを設定します。
     * @param omission オミッションのテンプレートを変更するJSPフラグメント
     */
    public void setOmission(JspFragment omission) {
        this.omission = omission;
    }

    /**
     * 次ページリンクのテンプレートを変更するJSPフラグメントを返却します。
     * @return 次ページリンクのテンプレートを変更するJSPフラグメント
     */
    public JspFragment getNext() {
        return next;
    }

    /**
     * 次ページリンクのテンプレートを変更するJSPフラグメントを設定します。
     * @param next 次ページリンクのテンプレートを変更するJSPフラグメント
     */
    public void setNext(JspFragment next) {
        this.next = next;
    }

    /**
     * 末尾ページリンクのテンプレートを変更するJSPフラグメントを返却します。
     * @return 末尾ページリンクのテンプレートを変更するJSPフラグメント
     */
    public JspFragment getTail() {
        return tail;
    }

    /**
     * 末尾ページリンクのテンプレートを変更するJSPフラグメントを設定します。
     * @param tail 末尾ページリンクのテンプレートを変更するJSPフラグメントです。
     */
    public void setTail(JspFragment tail) {
        this.tail = tail;
    }
}
