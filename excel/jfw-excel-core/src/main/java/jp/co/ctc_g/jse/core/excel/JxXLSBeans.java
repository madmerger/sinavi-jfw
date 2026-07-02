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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * <p>
 * このクラスは、ExcelのデータとJavaのオブジェクトのマッピングを実行するユーティリティです。
 * Apache POIを用いてExcelファイルを読み込みます。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxXLSBeans {

    /**
     * XSSF形式（xlsx）
     */
    public static final String TYPE_XSSF = "XSSF";

    /**
     * HSSF形式（xls）
     */
    public static final String TYPE_HSSF = "HSSF";

    private JxVerticalRecordsProcessor verticalRecordsProcessor = new JxVerticalRecordsProcessor();
    private JxIterateTableProcessor iterateTableProcessor = new JxIterateTableProcessor();

    /**
     * デフォルトコンストラクタです。
     */
    public JxXLSBeans() {}

    /**
     * Excelファイルを読み込み、指定されたクラスにマッピングします。
     * @param <P> ロード対象のオブジェクト
     * @param in インプットストリーム
     * @param clazz マッピング対象のクラス
     * @return マッピング結果
     * @throws IOException 読み込み時の例外
     */
    public <P> P load(InputStream in, Class<P> clazz) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            return mapSheet(sheet, clazz);
        }
    }

    /**
     * 複数のシートを読み込み、指定されたクラスにマッピングします。
     * @param <P> ロード対象のオブジェクト
     * @param in インプットストリーム
     * @param clazz マッピング対象のクラス
     * @param type ファイル形式
     * @return マッピング結果の配列
     * @throws IOException 読み込み時の例外
     */
    @SuppressWarnings("unchecked")
    public <P> P[] loadMultiple(InputStream in, Class<P> clazz, String type) throws IOException {
        try (Workbook workbook = createWorkbook(in, type)) {
            List<P> results = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                P result = mapSheet(sheet, clazz);
                if (result != null) {
                    results.add(result);
                }
            }
            return results.toArray((P[]) Array.newInstance(clazz, 0));
        }
    }

    private Workbook createWorkbook(InputStream in, String type) throws IOException {
        if (TYPE_HSSF.equals(type)) {
            return new HSSFWorkbook(in);
        }
        return new XSSFWorkbook(in);
    }

    @SuppressWarnings("unchecked")
    private <P> P mapSheet(Sheet sheet, Class<P> clazz) {
        try {
            P instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                JxVerticalRecords annotation = field.getAnnotation(JxVerticalRecords.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Map<JxHeaderInfo, Object[]> result = verticalRecordsProcessor.process(sheet, annotation);
                    if (!result.isEmpty()) {
                        Object[] records = result.values().iterator().next();
                        if (field.getType().isArray()) {
                            field.set(instance, records);
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            List<Object> list = new ArrayList<>();
                            for (Object r : records) {
                                list.add(r);
                            }
                            field.set(instance, list);
                        }
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }
}
