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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * <p>
 * このクラスは、同一構造の繰り返しテーブルを処理するプロセッサです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxIterateTableProcessor {

    private JxVerticalRecordsProcessor verticalRecordsProcessor = new JxVerticalRecordsProcessor();

    /**
     * デフォルトコンストラクタです。
     */
    public JxIterateTableProcessor() {}

    /**
     * 繰り返しテーブルを処理します。
     * @param sheet Excelシート
     * @param tableLabel テーブルラベル
     * @param record レコード定義
     * @return 読み込み結果のリスト
     */
    public List<Map<JxHeaderInfo, Object[]>> process(Sheet sheet, String tableLabel, JxVerticalRecords record) {
        List<Map<JxHeaderInfo, Object[]>> results = new ArrayList<>();
        List<int[]> tablePositions = findTablePositions(sheet, tableLabel);
        for (int[] pos : tablePositions) {
            JxVerticalRecordsForIterateTable iterRecord = new JxVerticalRecordsForIterateTable(record, pos[1], pos[0]);
            Map<JxHeaderInfo, Object[]> result = verticalRecordsProcessor.process(sheet, iterRecord);
            results.add(result);
        }
        return results;
    }

    private List<int[]> findTablePositions(Sheet sheet, String tableLabel) {
        List<int[]> positions = new ArrayList<>();
        for (int rowIdx = 0; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) continue;
            for (int colIdx = 0; colIdx <= row.getLastCellNum(); colIdx++) {
                Cell cell = row.getCell(colIdx);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String value = cell.getStringCellValue();
                    if (tableLabel.equals(value)) {
                        positions.add(new int[]{rowIdx, colIdx});
                    }
                }
            }
        }
        return positions;
    }
}
