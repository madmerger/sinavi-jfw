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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.takezoe.xlsbeans.NeedPostProcess;
import com.github.takezoe.xlsbeans.Utils;
import com.github.takezoe.xlsbeans.XLSBeansConfig;
import com.github.takezoe.xlsbeans.annotation.IterateTables;
import com.github.takezoe.xlsbeans.processor.IterateTablesProcessor;
import com.github.takezoe.xlsbeans.xml.AnnotationReader;
import com.github.takezoe.xlsbeans.xssfconverter.WCell;
import com.github.takezoe.xlsbeans.xssfconverter.WSheet;

/**
 * <p>
 * このクラスは、同一の構造の表がシート内で繰り返し出現する場合のマッピングを処理を行います。
 * </p>
 * <p>
 * XLSBeansではVerticalRecordsに対応していなかったため、VerticalRecordsに対応できるように拡張しました。
 * </p>
 * @see IterateTablesProcessor
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxIterateTableProcessor extends IterateTablesProcessor {

    /**
     * デフォルトコンストラクタです。
     */
    public JxIterateTableProcessor() {}

    /**
     * VerticalRecordsへの対応のため、オーバーライドしました。
     * @param sheet シート
     * @param tables IterateTablesアノテーションの定義
     * @param reader アノテーションリーダ
     * @param process プロセッサ
     * @throws Exception 予期しない例外
     */
    @Override
    protected List<?> createTables(WSheet sheet, IterateTables tables, AnnotationReader reader, XLSBeansConfig config,
        List<NeedPostProcess> process) throws Exception {

        List<Object> resultTableList = new ArrayList<Object>();
        String label = tables.tableLabel();
        WCell after = null;
        WCell currentCell = Utils.getCell(sheet, label, after, false, !tables.optional(), config);
        while (currentCell != null) {
            // 1 table object instance
            Object obj = tables.tableClass().newInstance();
            // LabeledCellをマッピング
            processSingleLabelledCell(sheet, obj, currentCell, reader, config, process);
            // HorizontalRecordsをマッピング
            processMultipleTableCell(sheet, obj, currentCell, reader, tables, config, process);
            // VerticalRecordsをマッピング
            processMultipleTableCellForVertical(sheet, obj, currentCell, reader, tables, config, process);
            resultTableList.add(obj);
            after = currentCell;
            currentCell = Utils.getCell(sheet, label, after, false, false, config);
        }
        return resultTableList;
    }

    /**
     * VerticalRecordsのマッピングを行います。
     * @param sheet シート
     * @param tableObj オブジェクト
     * @param headerCell ヘッダセル
     * @param reader リーダ
     * @param iterateTables IterateTablesアノテーションの定義
     * @param needPostProcess プロセッサ
     * @throws Exception 予期しない例外
     */
    protected void processMultipleTableCellForVertical(WSheet sheet, Object tableObj, WCell headerCell,
        AnnotationReader reader, IterateTables iterateTables, XLSBeansConfig config, List<NeedPostProcess> needPostProcess)
        throws Exception {
        List<Object> properties = Utils.getPropertiesWithAnnotation(tableObj, reader, JxVerticalRecords.class);
        int headerColumn = headerCell.getColumn();
        int headerRow = headerCell.getRow();
        if (iterateTables.bottom() > 0) {
            headerRow += iterateTables.bottom();
        }
        JxVerticalRecordsProcessor processor = new JxVerticalRecordsProcessor();
        for (Object property : properties) {
            JxVerticalRecords ann = null;
            if (property instanceof Method) {
                ann = reader.getAnnotation(tableObj.getClass(), (Method) property, JxVerticalRecords.class);
            } else if (property instanceof Field) {
                ann = reader.getAnnotation(tableObj.getClass(), (Field) property, JxVerticalRecords.class);
            }
            if (ann != null && ann.tableLabel().equals(iterateTables.tableLabel())) {
                JxVerticalRecords records = new JxVerticalRecordsForIterateTable(ann, headerColumn, headerRow);
                if (property instanceof Method) {
                    processor.doProcess(sheet, tableObj, (Method) property, records, reader, config, needPostProcess);
                } else if (property instanceof Field) {
                    processor.doProcess(sheet, tableObj, (Field) property, records, reader, config, needPostProcess);
                }
            }
        }
    }
}
