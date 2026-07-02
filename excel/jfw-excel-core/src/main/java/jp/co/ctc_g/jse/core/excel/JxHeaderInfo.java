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

package jp.co.ctc_g.jse.core.excel;

/**
 * <p>
 * このクラスは、ヘッダ情報を保持します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxHeaderInfo {

    private String headerLabel;
    private int headerRange;
    private int rowIndex;

    /**
     * コンストラクタです。
     * @param headerLabel ヘッダラベルです。
     * @param headerRange ヘッダの範囲です。
     * @param rowIndex ヘッダ行番号です。
     */
    protected JxHeaderInfo(String headerLabel, int headerRange, int rowIndex) {
        this.headerLabel = headerLabel;
        this.headerRange = headerRange;
        this.rowIndex = rowIndex;
    }

    /**
     * ヘッダラベルを返します。
     * @return ヘッダラベル
     */
    protected String getHeaderLabel() {
        return headerLabel;
    }

    /**
     * ヘッダの範囲を返します。
     * @return ヘッダの範囲
     */
    protected int getHeaderRange() {
        return headerRange;
    }

    /**
     * ヘッダの行番号を返します。
     * @return 行番号
     */
    protected int getRowIndex() {
        return rowIndex;
    }

}
