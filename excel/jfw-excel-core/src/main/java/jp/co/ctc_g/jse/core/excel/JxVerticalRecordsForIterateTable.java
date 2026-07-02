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

import java.lang.annotation.Annotation;

/**
 * <p>
 * このクラスは、同一の構造の表がシート内で繰り返し出現する場合に垂直方向に連続する列をマッピングします。
 * </p>
 * @see jp.co.ctc_g.jse.core.excel.JxVerticalRecords
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxVerticalRecordsForIterateTable implements JxVerticalRecords {

    private int headerColumn = -1;
    private int headerRow = -1;
    private boolean optional = false;
    private int range = -1;
    private Class<?> recordClass = null;
    private String tableLabel = "";
    private RecordTerminal terminal = null;
    private Class<? extends Annotation> annotationType = null;
    private String terminateLabel = null;
    private int headerCount = 0;

    /**
     * デフォルトコンストラクタです。
     */
    public JxVerticalRecordsForIterateTable() {}

    /**
     * コンストラクタです。
     * @param rec JxVerticalRecordsアノテーションの定義情報
     * @param headerColumn ヘッダの列インデクス
     * @param headerRow ヘッダの行インデクス
     */
    public JxVerticalRecordsForIterateTable(JxVerticalRecords rec, int headerColumn, int headerRow) {
        this.headerColumn = headerColumn;
        this.headerRow = headerRow;
        this.optional = rec.optional();
        this.range = rec.range();
        this.recordClass = rec.recordClass();
        this.tableLabel = "";
        this.terminal = rec.terminal();
        this.annotationType = rec.annotationType();
        this.terminateLabel = rec.terminateLabel();
        this.headerCount = rec.headerLimit();
    }

    /** {@inheritDoc} */
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    /** {@inheritDoc} */
    public boolean optional() {
        return optional;
    }

    /** {@inheritDoc} */
    public String tableLabel() {
        return tableLabel;
    }

    /** {@inheritDoc} */
    public String terminateLabel() {
        return terminateLabel;
    }

    /** {@inheritDoc} */
    public int headerColumn() {
        return headerColumn;
    }

    /** {@inheritDoc} */
    public int headerRow() {
        return headerRow;
    }

    /** {@inheritDoc} */
    public Class<?> recordClass() {
        return recordClass;
    }

    /** {@inheritDoc} */
    public RecordTerminal terminal() {
        return terminal;
    }

    /** {@inheritDoc} */
    public int range() {
        return range;
    }

    /** {@inheritDoc} */
    public int headerLimit() {
        return headerCount;
    }

}
