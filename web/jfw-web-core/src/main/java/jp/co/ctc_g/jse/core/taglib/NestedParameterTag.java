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

package jp.co.ctc_g.jse.core.taglib;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspTag;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Arrays;
import jp.co.ctc_g.jfw.core.util.Strings;

/**
 * <p>
 * この抽象クラスは、{@link ParameterAware}に関連付けられる汎用キーバリュー設定用タグです。
 * このクラスの利用方法については、{@link ParameterAware}を参照してください。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 * @see ParameterAware
 */
public abstract class NestedParameterTag extends SimpleTagSupport {

    private String name;

    private String value;

    private String[] values;

    /**
     * このクラスのインスタンスを生成します。
     */
    protected NestedParameterTag() {}

    /**
     * キー名を取得します。
     * @return キー名
     */
    public String getName() {
        return name;
    }

    /**
     * キー名を設定します。
     * @param name キー名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 値を取得します。
     * もし、複数の値が設定されている場合、
     * カンマで結合された文字列として返却されます。
     * @return 値
     */
    public String getValue() {
        return !Arrays.isEmpty(values) ? Strings.joinBy(",", values) : value;
    }

    /**
     * 値を設定します。
     * 1つのキーに複数の値を設定したい場合、カンマで区切る必要があります。
     * @param value 値
     */
    public void setValue(String value) {
        if (Strings.isEmpty(value)) {
            this.value = "";
            this.values = null;
        } else {
            String[] vs = Strings.split(",", value);
            if (!Arrays.isEmpty(vs)) {
                if (vs.length == 1) {
                    this.value = vs[0];
                    this.values = null;
                } else {
                    this.values = vs;
                    this.value = null;
                }
            }
        }
    }

    /**
     * 複数の値を取得します（設定されている場合）。
     * @return 複数の値
     */
    protected String[] getValues() {
        return this.values;
    }

    /**
     * 複数の値を設定します。
     * @param values 複数の値
     */
    protected void setValues(String[] values) {
        this.values = values;
    }

    /**
     * {@link ParameterAware}を探して、コールバックします。
     * まず、直接の親タグが{@link ParameterAware}かどうか判定します。
     * そうであれば、コールバックして終了します。
     * もし、直接の親タグが{@link ParameterAware}ない場合、
     * {@link SimpleTagSupport#findAncestorWithClass(JspTag, Class)}を利用して、
     * ルートまで{@link ParameterAware}を探して辿ります。
     * それでも見つからない場合、処理を終了します。
     * @throws JspException {@link JspException}
     * @throws IOException {@link IOException}
     */
    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        Args.checkNotEmpty(getName());
        JspTag s = getParent();
        if (!ParameterAware.class.isInstance(s)) {
            s = SimpleTagSupport.findAncestorWithClass(this, ParameterAware.class);
        }
        if (s == null) return;
        ParameterAware parent = (ParameterAware) s;
        if (getValues() != null) {
            parent.awareParameter(name, getValues());
        } else {
            parent.awareParameter(name, getValue());
        }

    }
}
