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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.takezoe.xlsbeans.NeedPostProcess;
import com.github.takezoe.xlsbeans.Utils;
import com.github.takezoe.xlsbeans.XLSBeansConfig;
import com.github.takezoe.xlsbeans.XLSBeansException;
import com.github.takezoe.xlsbeans.annotation.Column;
import com.github.takezoe.xlsbeans.annotation.MapColumns;
import com.github.takezoe.xlsbeans.annotation.PostProcess;
import com.github.takezoe.xlsbeans.annotation.RecordTerminal;
import com.github.takezoe.xlsbeans.processor.VerticalRecordsProcessor;
import com.github.takezoe.xlsbeans.xml.AnnotationReader;
import com.github.takezoe.xlsbeans.xssfconverter.WCell;
import com.github.takezoe.xlsbeans.xssfconverter.WSheet;

/**
 * <p>
 * このクラスは、垂直方向に連続する列をマッピングします。
 * </p>
 * @see VerticalRecordsProcessor
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxVerticalRecordsProcessor extends VerticalRecordsProcessor {

    /**
     * デフォルトコンストラクタです。
     */
    public JxVerticalRecordsProcessor() {}

    /**
     * {@inheritDoc}
     * <p>
     * このメソッドの処理はオリジナルと同じです。
     * アノテーションのみ変更しています。
     * </p>
     */
    @Override
    public void doProcess(WSheet sheet, Object obj, Method setter, Annotation ann, AnnotationReader reader,
        XLSBeansConfig config, List<NeedPostProcess> processor) throws Exception {
        JxVerticalRecords records = (JxVerticalRecords) ann;
        Class<?>[] clazzes = setter.getParameterTypes();
        if (clazzes.length != 1) {
            throw new XLSBeansException("Arguments of '" + setter.toString() + "' is invalid.");
        } else if (List.class.isAssignableFrom(clazzes[0])) {
            List<?> value = createRecords(sheet, records, reader, config, processor);
            if (value != null) {
                setter.invoke(obj, new Object[] {
                    value
                });
            }
        } else if (clazzes[0].isArray()) {
            List<?> value = createRecords(sheet, records, reader, config, processor);
            if (value != null) {
                Class<?> type = clazzes[0].getComponentType();
                Object array = Array.newInstance(type, value.size());
                for (int i = 0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }
                setter.invoke(obj, new Object[] {
                    array
                });
            }
        } else {
            throw new XLSBeansException("Arguments of '" + setter.toString() + "' is invalid.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * このメソッドの処理はオリジナルと同じです。
     * アノテーションのみ変更しています。
     * </p>
     */
    @Override
    public void doProcess(WSheet wSheet, Object obj, Field field, Annotation ann, AnnotationReader reader,
        XLSBeansConfig config, List<NeedPostProcess> needPostProcess) throws Exception {
        JxVerticalRecords records = (JxVerticalRecords) ann;
        Class<?> clazz = field.getType();
        if (List.class.isAssignableFrom(clazz)) {
            List<?> value = createRecords(wSheet, records, reader, config, needPostProcess);
            if (value != null) {
                field.set(obj, value);
            }
        } else if (clazz.isArray()) {
            List<?> value = createRecords(wSheet, records, reader, config, needPostProcess);
            if (value != null) {
                Class<?> type = clazz.getComponentType();
                Object array = Array.newInstance(type, value.size());
                for (int i = 0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }
                field.set(obj, array);
            }
        } else {
            throw new XLSBeansException("Arguments of '" + field.toString() + "' is invalid.");
        }
    }

    /**
     * レコードとJavaのオブジェクトをマッピングします。
     */
    protected List<?> createRecords(WSheet wSheet, JxVerticalRecords records, AnnotationReader reader,
        XLSBeansConfig config, List<NeedPostProcess> needPostProcess) throws Exception {
        List<Object> columnProps = Utils.getColumnProperties(records.recordClass().newInstance(), null, reader, config);
        if (columnProps.isEmpty()) throw new XLSBeansException("VerticalRecordsには@Columnは必須です。");
        List<Object> result = new ArrayList<Object>();
        List<JxHeaderInfo> headers = new ArrayList<JxHeaderInfo>();
        // get header
        int initColumn = -1;
        int initRow = -1;
        if (records.tableLabel().equals("")) {
            initColumn = records.headerColumn();
            initRow = records.headerRow();
        } else {
            try {
                WCell labelCell = Utils.getCell(wSheet, records.tableLabel(), 0, config);
                initColumn = labelCell.getColumn() + 1;
                initRow = labelCell.getRow();
            } catch (XLSBeansException ex) {
                if (records.optional()) {
                    return null;
                } else {
                    throw ex;
                }
            }
        }
        int hColumn = initColumn;
        int hRow = initRow;
        int rangeCount = 1;
        while (true) {
            try {
                WCell cell = wSheet.getCell(hColumn, hRow);
                while (cell.getContents().equals("") && rangeCount < records.range()) {
                    cell = wSheet.getCell(hColumn, hRow + rangeCount);
                    rangeCount++;
                }
                if (cell.getContents().equals("")) {
                    break;
                } else {
                    for (int j = hColumn; j > initColumn; j--) {
                        WCell tmpCell = wSheet.getCell(j, hRow);
                        if (!tmpCell.getContents().equals("")) {
                            cell = tmpCell;
                            break;
                        }
                    }
                }
                headers.add(new JxHeaderInfo(cell.getContents(), rangeCount - 1, hRow));
                hRow = hRow + rangeCount;
                rangeCount = 1;
            } catch (ArrayIndexOutOfBoundsException ex) {
                break;
            }
            if (records.headerLimit() > 0 && headers.size() >= records.headerLimit()) {
                break;
            }
        }

        // Check for columns
        checkColumns(records.recordClass(), headers, reader, config);

        RecordTerminal terminal = records.terminal();
        if (terminal == null) {
            terminal = RecordTerminal.Empty;
        }

        // get records
        hColumn++;
        while (hColumn < wSheet.getColumns()) {
            hRow = initRow;
            // ここの処理が遅かったので、大幅にリファクタリングしています。
            Object record = records.recordClass().newInstance();
            processMapColumns(wSheet, headers, hRow, hColumn, record, reader, config);
            boolean retColumn = processColumn(wSheet, headers, hRow, hColumn, record, reader, config);
            if (retColumn) {
                result.add(record);
                for (Method method : record.getClass().getMethods()) {
                    PostProcess ann = reader.getAnnotation(record.getClass(), method, PostProcess.class);
                    if (ann != null) {
                        needPostProcess.add(new NeedPostProcess(record, method));
                    }
                }
            }
            hColumn++;
        }

        return result;
    }

    /**
     * <p>
     * {@link Column}アノテーションが付与されているプロパティを検索し、Excelのデータとマッピングします。
     * また、マッピングを実施したかどうかを判断し、呼び出し元に返却します。
     * この2つの対応はパフォーマンス対策です。
     * </p>
     * <p>
     * XLSBeansでは最大カラムの数×HeaderInfoの数を検索し、{@link Column}アノテーションのマッピングを行っていました。
     * これではデータの件数が増加すればするほど処理時間がかかることから{@link Column}アノテーションが付与されているプロパティを検査し、
     * そのプロパティとHeaderInfoのラベルが一致するかどうかを調べることで高速に処理するようにしました。
     * {@link JxHeaderInfo}に行番号を保持するプロパティを定義したのはこのためです。
     * </p>
     * <p>
     * {@link WSheet#getColumns()}は最大のカラム数を返す実装になっています。
     * 同一の構造の表が2つシート内で繰り返し出現したとき、どちらかが最大のカラム数を繰り返し処理されます。
     * そのため、空文字列がマッピングされる可能性があります。
     * これに対応するために内容を確認し、空文字列の場合：false、空文字列以外の場合：trueを返しています。
     * </p>
     */
    protected boolean processColumn(WSheet wSheet, List<JxHeaderInfo> headers, int hRow, int hColumn, Object record,
        AnnotationReader reader, XLSBeansConfig config) throws Exception {

        List<String> keys = new ArrayList<String>();
        List<Object> properties = Utils.getColumnProperties(record, null, reader, config);
        for (Object property : properties) {
            Column column = null;
            if (property instanceof Method) {
                column = reader.getAnnotation(record.getClass(), (Method) property, Column.class);
            } else if (property instanceof Field) {
                column = reader.getAnnotation(record.getClass(), (Field) property, Column.class);
            }
            for (JxHeaderInfo info : headers) {
                if (info.getHeaderLabel().equals(column.columnName())) {
                    hRow = info.getRowIndex();
                    break;
                }
            }
            WCell cell = wSheet.getCell(hColumn, hRow);
            WCell valueCell = cell;
            if (!valueCell.getContents().equals("")) {
                String key = "";
                if (property instanceof Method) {
                    key = ((Method) property).getName();
                    Utils.setPosition(hColumn, hRow, record, Utils.toPropertyName(key));
                    Utils.invokeSetter((Method) property, record, valueCell.getContents(), config);
                } else if (property instanceof Field) {
                    key = ((Field) property).getName();
                    Utils.setPosition(hColumn, hRow, record, key);
                    Utils.setField((Field) property, record, valueCell.getContents(), config);
                }
                keys.add(key);
            }
        }
        return !keys.isEmpty();
    }

    /**
     * <p>
     * {@link Column}アノテーションが付与されているプロパティを検索し、Excelのデータとマッピングします。
     * </p>
     */
    protected void processMapColumns(WSheet sheet, List<JxHeaderInfo> headerInfos, int begin, int column, Object record,
        AnnotationReader reader, XLSBeansConfig config) throws Exception {

        List<Object> properties = Utils.getMapColumnProperties(record, reader);
        for (Object property : properties) {
            MapColumns ann = null;
            if (property instanceof Method) {
                ann = reader.getAnnotation(record.getClass(), (Method) property, MapColumns.class);
            } else if (property instanceof Field) {
                ann = reader.getAnnotation(record.getClass(), (Field) property, MapColumns.class);
            }
            boolean flag = false;
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (JxHeaderInfo headerInfo : headerInfos) {
                if (headerInfo.getHeaderLabel().equals(ann.previousColumnName())) {
                    flag = true;
                    begin++;
                    continue;
                }
                if (flag) {
                    WCell cell = sheet.getCell(column, begin + headerInfo.getHeaderRange());
                    map.put(headerInfo.getHeaderLabel(), cell.getContents());
                }
                begin = begin + headerInfo.getHeaderRange() + 1;
            }
            if (!map.isEmpty()) {
                if (property instanceof Method) {
                    ((Method) property).invoke(record, map);
                } else if (property instanceof Field) {
                    ((Field) property).set(record, map);
                }
            }
        }
    }

    protected void checkColumns(Class<?> recordClass, List<JxHeaderInfo> headers, AnnotationReader reader,
        XLSBeansConfig config) throws Exception {

        for (Object property : Utils.getColumnProperties(recordClass.newInstance(), null, reader, config)) {
            Column column = null;
            if (property instanceof Method) {
                column = reader.getAnnotation(recordClass, (Method) property, Column.class);
            } else if (property instanceof Field) {
                column = reader.getAnnotation(recordClass, (Field) property, Column.class);
            }
            if (column != null && !column.optional()) {
                String columnName = column.columnName();
                boolean find = false;
                for (JxHeaderInfo info : headers) {
                    if (info.getHeaderLabel().equals(columnName)) {
                        find = true;
                        break;
                    }
                }
                if (!find) { throw new XLSBeansException("Column '" + columnName + "' doesn't exist."); }
            }
        }
    }

}
