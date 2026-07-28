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

import com.github.takezoe.xlsbeans.annotation.RecordTerminal;

/**
 * <p>
 * このクラスは、同一の構造の表がシート内で繰り返し出現する場合に垂直方向に連続する列をマッピングします。
 * </p>
 * <p>
 * XLSBeansのIterateTableは水平方向のマッピングのみの対応のため、このクラスを独自に定義しています。
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

    /**
     * {@link JxVerticalRecords}の型を取得するメソッドです。
     * @return {@link JxVerticalRecords}の型
     */
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    /**
     * {@link JxVerticalRecords#optional()}で指定されたオプションを取得するメソッドです。
     * @return {@link JxVerticalRecords#optional()}で指定されたオプション
     */
    public boolean optional() {
        return optional;
    }

    /**
     * テーブルのラベルを取得するメソッドです。
     * @return テーブルのラベル
     */
    public String tableLabel() {
        return tableLabel;
    }

    /**
     * {@link JxVerticalRecords#terminateLabel()}で指定された読み込みの終了となるラベルを取得するメソッドです。
     * @return {@link JxVerticalRecords#terminateLabel()}で指定された読み込みの終了となるラベル
     */
    public String terminateLabel() {
        return terminateLabel;
    }

    /**
     * ヘッダの列インデクスを取得するメソッドです。
     * @return ヘッダの列インデクス
     */
    public int headerColumn() {
        return headerColumn;
    }

    /**
     * ヘッダの行インデクスを取得するメソッドです。
     * @return ヘッダの行インデクス
     */
    public int headerRow() {
        return headerRow;
    }

    /**
     * {@link JxVerticalRecords#recordClass()}で指定されたレコードとして読み込む型を取得するメソッドです。
     * @return {@link JxVerticalRecords#recordClass()}で指定されたレコードとして読み込む型
     */
    public Class<?> recordClass() {
        return recordClass;
    }

    /**
     * {@link JxVerticalRecords#terminal()}で指定された終了タイプを取得するメソッドです。
     * @return {@link com.github.takezoe.xlsbeans.annotation.RecordTerminal}
     */
    public RecordTerminal terminal() {
        return terminal;
    }

    /**
     * {@link JxVerticalRecords#range()}で指定されたヘッダの範囲を取得するメソッドです。
     * @return {@link JxVerticalRecords#range()}で指定されたヘッダの範囲
     */
    public int range() {
        return range;
    }

    /**
     * {@link JxVerticalRecords#headerLimit()}で指定されたヘッダの範囲を取得するメソッドです。
     * @return {@link JxVerticalRecords#headerLimit()}で指定されたヘッダの範囲
     */
    public int headerLimit() {
        return headerCount;
    }

}
