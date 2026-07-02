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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * このアノテーションは、垂直方向に連続する列をマッピングするアノテーションです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JxVerticalRecords {

    /**
     * 例外を発生させるかどうかを指定します。
     */
    boolean optional() default false;

    /**
     * テーブルラベルを指定します。
     */
    String tableLabel() default "";

    /**
     * 読み込みの終了となるラベルがある場合にそのラベルを指定します。
     */
    String terminateLabel() default "";

    /**
     * ヘッダの列番号を指定します。
     */
    int headerColumn() default -1;

    /**
     * ヘッダの行番号を指定します。
     */
    int headerRow() default -1;

    /**
     * レコードとして読み込む型を指定します。
     */
    Class<?> recordClass();

    /**
     * 終了タイプを指定します。
     */
    RecordTerminal terminal() default RecordTerminal.Empty;

    /**
     * ヘッダの範囲を指定します。
     */
    int range() default 1;

    /**
     * ヘッダの最大数を指定します。
     */
    int headerLimit() default 0;

}
