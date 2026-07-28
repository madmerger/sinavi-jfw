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

import java.io.InputStream;

import com.github.takezoe.xlsbeans.XLSBeans;
import com.github.takezoe.xlsbeans.XLSBeansException;
import com.github.takezoe.xlsbeans.annotation.IterateTables;
import com.github.takezoe.xlsbeans.processor.FieldProcessorFactory;

/**
 * <p>
 * このクラスは、ExcelのデータとJavaのオブジェクトのマッピングを実行するユーティリティです。
 * </p>
 * @see XLSBeans
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class JxXLSBeans extends XLSBeans {

    static {
        FieldProcessorFactory.registerProcessor(IterateTables.class, new JxIterateTableProcessor());
        FieldProcessorFactory.registerProcessor(JxVerticalRecords.class, new JxVerticalRecordsProcessor());
    }

    /**
     * デフォルトコンストラクタです。
     */
    public JxXLSBeans() {}



    /**
     * <p>
     * 複数のシートかつExcelの読み込みタイプを指定できるメソッドが{@link XLSBeans}になかったため、
     * 拡張しました。
     * </p>
     * <p>
     * このメソッドによりExcel2003形式、Excel2007以上形式も読み込めるようにしました。
     * </p>
     * @param <P> ロード対象のオブジェクト
     * @param in インプットストリーム
     * @param clazz マッピング対象のクラス
     * @param type ファイル形式(WorkbookFinder.TYPE_XSSF:xlsx形式、WorkbookFinder.TYPE_HSSF:xls形式)
     * @return マッピング結果
     * @throws XLSBeansException マッピング時の例外
     */
    public <P> P[] loadMultiple(InputStream in, Class<P> clazz, String type) throws XLSBeansException {
        return loadMultiple(in, null, clazz, type);
    }

}
