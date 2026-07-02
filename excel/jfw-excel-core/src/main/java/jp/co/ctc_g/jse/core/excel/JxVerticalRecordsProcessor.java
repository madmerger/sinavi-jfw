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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * <p>
 * このクラスは、垂直方向に連続するレコードをExcelシートから読み込むプロセッサです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxVerticalRecordsProcessor {

    /**
     * デフォルトコンストラクタです。
     */
    public JxVerticalRecordsProcessor() {}

    /**
     * 垂直方向のレコードをExcelシートから読み込みます。
     * @param sheet Excelシート
     * @param record アノテーション定義
     * @return 読み込み結果のマップ（ヘッダ情報とレコード配列）
     */
    public Map<JxHeaderInfo, Object[]> process(Sheet sheet, JxVerticalRecords record) {
        Map<JxHeaderInfo, Object[]> result = new LinkedHashMap<>();
        int headerColumn = record.headerColumn();
        int headerRow = record.headerRow();
        if (headerColumn < 0 || headerRow < 0) {
            return result;
        }
        List<JxHeaderInfo> headers = getHeaders(sheet, record);
        for (JxHeaderInfo header : headers) {
            Object[] records = readRecords(sheet, record, header);
            result.put(header, records);
        }
        return result;
    }

    /**
     * ヘッダ情報を取得します。
     * @param sheet Excelシート
     * @param record アノテーション定義
     * @return ヘッダ情報のリスト
     */
    protected List<JxHeaderInfo> getHeaders(Sheet sheet, JxVerticalRecords record) {
        List<JxHeaderInfo> headers = new ArrayList<>();
        int headerRow = record.headerRow();
        int headerColumn = record.headerColumn();
        int range = record.range();
        int limit = record.headerLimit();
        Row row = sheet.getRow(headerRow);
        if (row == null) return headers;
        int count = 0;
        for (int col = headerColumn; col <= row.getLastCellNum(); col++) {
            if (limit > 0 && count >= limit) break;
            Cell cell = row.getCell(col);
            if (cell == null || cell.getCellType() == CellType.BLANK) break;
            String label = cell.getStringCellValue();
            if (label == null || label.isEmpty()) break;
            headers.add(new JxHeaderInfo(label, range, headerRow));
            count++;
        }
        return headers;
    }

    /**
     * レコードを読み込みます。
     * @param sheet Excelシート
     * @param record アノテーション定義
     * @param header ヘッダ情報
     * @return 読み込み結果の配列
     */
    @SuppressWarnings("unchecked")
    protected Object[] readRecords(Sheet sheet, JxVerticalRecords record, JxHeaderInfo header) {
        Class<?> recordClass = record.recordClass();
        List<Object> records = new ArrayList<>();
        int startRow = header.getRowIndex() + header.getHeaderRange();
        int headerColumn = record.headerColumn();
        for (int rowIdx = startRow; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null) break;
            Cell cell = row.getCell(headerColumn);
            if (isTerminal(cell, record.terminal(), record.terminateLabel())) break;
            try {
                Object bean = recordClass.getDeclaredConstructor().newInstance();
                mapRow(bean, row, headerColumn);
                records.add(bean);
            } catch (Exception e) {
                break;
            }
        }
        return records.toArray((Object[]) Array.newInstance(recordClass, 0));
    }

    private boolean isTerminal(Cell cell, RecordTerminal terminal, String terminateLabel) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return terminal == RecordTerminal.Empty;
        }
        if (terminateLabel != null && !terminateLabel.isEmpty()) {
            String value = cell.getStringCellValue();
            if (terminateLabel.equals(value)) return true;
        }
        return false;
    }

    private void mapRow(Object bean, Row row, int startCol) {
        Field[] fields = bean.getClass().getDeclaredFields();
        int col = startCol;
        for (Field field : fields) {
            Cell cell = row.getCell(col);
            if (cell != null) {
                setFieldValue(bean, field, cell);
            }
            col++;
        }
    }

    private void setFieldValue(Object bean, Field field, Cell cell) {
        try {
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type == String.class) {
                field.set(bean, getCellStringValue(cell));
            } else if (type == int.class || type == Integer.class) {
                field.set(bean, (int) cell.getNumericCellValue());
            } else if (type == long.class || type == Long.class) {
                field.set(bean, (long) cell.getNumericCellValue());
            } else if (type == double.class || type == Double.class) {
                field.set(bean, cell.getNumericCellValue());
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(bean, cell.getBooleanCellValue());
            }
        } catch (Exception e) {
            // skip field mapping errors
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }
}
