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
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import jp.co.ctc_g.jfw.core.resource.Rs;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.taglib.ParameterAware;

/**
 * <p>
 * このクラスは、リソース値を操作するタグライブラリ機能を提供します。
 * </p>
 * <h4>タグの利用</h4>
 * <p>
 * ここでは、例として{@link JseRsOutTag}を取り上げます。その他のタグの場合でも、
 * このクラスを継承している場合には、ここでの解説は全て有効です。
 * </p>
 * <p>
 * このタグは、内部的に{@link jp.co.ctc_g.jfw.core.resource.Rs}を利用しており、リソース名の明示的な指定は必要ありません。
 * </p>
 * <pre class="brush:jsp">
 * &lt;jse:rs code="property.key" /&gt;
 * </pre>
 * <p>
 * また、プロパティファイル内のXML/HTMLエンティティのエスケープが必要な場合には、
 * {@code escapeRequiered}属性を{@code true}にします。
 * </p>
 * <pre class="brush:jsp">
 * &lt;jse:rs code="property.key" escapeRequired="true" /&gt;
 * </pre>
 * <p>
 * プレースホルダを動的に置換する必要がある場合は、ネストされた　{@link JseRsReplaceTag}を利用します。
 * 下記の場合、property.keyに対応するリソース値内の${foo}をbarに置き換えます。
 * もちろん、jse:replaceのvalue属性にはEL式を含めることができます。
 * </p>
 * <pre class="brush:jsp">
 * &lt;jse:rs code="property.key" escapeRequired="true"&gt;
 *     &lt;jse:replace name="foo" value="bar" /&gt;
 * &lt;/jse:rs&gt;
 * </pre>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JseRsTag extends TagSupport implements ParameterAware {

    private static final long serialVersionUID = -5546587321309553752L;

    private String code;
    private Boolean escapeRequired;
    private Map<String, String> replaces;

    /**
     * このクラスのインスタンスを生成します。
     */
    public JseRsTag() {
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
     * このクラスのインスタンスを再利用できるように初期化します。
     */
    protected void reset() {
        code = "";
        escapeRequired = true;
        replaces = null;
    }

    /**
     * リソースから取得したい値のキーを取得します。
     * @return リソースから取得したい値のキー
     */
    public String getCode() {
        return code;
    }

    /**
     * リソースから取得したい値のキーを返却します。
     * @param code リソースから取得したい値のキー
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * エスケープが必要かどうかを取得します。
     * @return エスケープが必要かどうか
     */
    public Boolean isEscapeRequired() {
        return escapeRequired;
    }

    /**
     * エスケープが必要かどうかを設定します。
     * @param escapeRequired エスケープが必要かどうか
     */
    public void setEscapeRequired(Boolean escapeRequired) {
        this.escapeRequired = escapeRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();
        Args.checkNotEmpty(getCode());
        String value = Rs.find(getCode());
        if (value == null) return EVAL_PAGE;
        if (!Maps.isEmpty(replaces)) {
            value = Strings.substitute(Rs.find(value), replaces);
        }
        if (value.equals(getCode())) {
            value = MessageCodesResolver.resolve(getCode());
        }
        if (escapeRequired) {
            value = Strings.escapeHTML(value);
        }
        if (!Strings.isEmpty(value)) {
            try {
                pageContext.getOut().print(value);
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        reset();
        return EVAL_PAGE;
    }

    /**
     * ネストされた{@link JseRsReplaceTag}がある場合、コールバックされます。
     * @param key リソースキー
     * @param value メッセージ
     * @see JseRsReplaceTag
     * @see ParameterAware
     */
    public void awareParameter(String key, String value) {
        if (replaces == null) {
            replaces = new HashMap<String, String>();
        }
        replaces.put(key, value);
    }

    /**
     * ネストされた{@link JseRsReplaceTag}がある場合、コールバックされます。
     * @param key リソースキー
     * @param values メッセージ
     * @see JseRsReplaceTag
     * @see ParameterAware
     */
    public void awareParameter(String key, String[] values) {
        if (replaces == null) {
            replaces = new HashMap<String, String>();
        }
        replaces.put(key, Strings.joinBy(",", values));
    }

}
