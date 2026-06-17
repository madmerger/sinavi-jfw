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

package jp.co.ctc_g.jse.core.csv;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <p>
 * このクラスは、一時ファイルのファイル名(パスを含む)やファイル名を保持します。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class DownloadFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 一時ファイルのファイル名(パスを含む)
     */
    private String tempFileName;

    /**
     * ダイアログに表示されるファイル名
     */
    private String fileName;

    /**
     * クライアントへファイル送信中に例外が発生したときに
     * 出力したファイルを削除するかどうかのフラグ
     */
    private boolean delete;

    /**
     * デフォルトコンストラクタです。
     */
    public DownloadFile() {}

    /**
     * コンストラクタです。
     * @param tempFileName 一時ファイルのファイル名
     */
    public DownloadFile(String tempFileName) {
        this.tempFileName = tempFileName;
        this.delete = Boolean.TRUE;
    }

    /**
     * Tempファイル名を取得します。
     * @return Tempファイル名
     */
    public String getTempFileName() {
        return tempFileName;
    }

    /**
     * Tempファイル名を設定します。
     * @param tempFileName Tempファイル名
     */
    public void setTempFileName(String tempFileName) {
        this.tempFileName = tempFileName;
    }

    /**
     * 削除したかどうか
     * @return true/false
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * 削除したかどうかを設定します。
     * @param delete true：削除済み、false：未削除
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * ファイル名を取得します。
     * @return ファイル名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * ファイル名を設定します。
     * @param fileName ファイル名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
