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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.csv.CSVConfigs.CSVConfig;

import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

/**
 * <p>
 * このクラスは、CSV書込処理のユーティリティです。
 * </p>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class CSVWriters {

    private static final Logger L = LoggerFactory.getLogger(CSVWriters.class);
    private static final ResourceBundle R = InternalMessages.getBundle(CSVWriters.class);

    /**
     * 一時ファイル名
     */
    protected String tempFileName;

    /**
     * {@link CSVConfig}
     */
    protected CSVConfig config;

    /**
     * ストリームライタ
     */
    protected OutputStreamWriter osw;

    /**
     * ファイル書込みストリーム
     */
    protected FileOutputStream fos;

    /**
     * ライタ
     */
    protected Writer writer;

    /**
     * CSVライタ
     */
    protected CSVWriter csv;

    /**
     * 処理件数カウンタ
     */
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * {@link UnaryOperator}
     */
    protected UnaryOperator<String> transformer;

    /**
     * コンストラクタです。
     */
    protected CSVWriters() {
        this(CSVConfigs.config());
    }

    /**
     * コンストラクタです。
     * @param config コンフィグ
     */
    protected CSVWriters(CSVConfig config) {
        this.config = config;
    }

    /**
     * ストリームを初期化します。
     */
    protected void initialize() {
        File f = new File(tempFileName);
        InternalException ie = null;
        try {
            fos = new FileOutputStream(f, true);
            if (L.isDebugEnabled()) L.debug(Strings.substitute(R.getString("D-CSV#0002"), Maps.hash("filePath", f.getAbsolutePath())));
            osw = new OutputStreamWriter(fos, config.encode());
            writer = new BufferedWriter(osw);
            csv = new CSVWriter(writer, config.separator(), config.quote(), config.escape(), config.line());
        } catch (FileNotFoundException e) {
            if (L.isDebugEnabled()) L.debug(R.getString("D-CSV#0003"), e);
            ie = new InternalException(CSVWriters.class, "E-CSV#0001", Maps.hash("filePath", f.getAbsolutePath()), e);
        } catch (UnsupportedEncodingException e) {
            if (L.isDebugEnabled()) L.debug(R.getString("D-CSV#0003"), e);
            ie = new InternalException(CSVWriters.class, "E-CSV#0002", Maps.hash("encode", config.encode()), e);
        } finally {
            if (ie != null) {
                close();
                throw ie;
            }
        }
    }

    /**
     * ストリームを開きます。
     * @return ユーティリティ
     */
    public CSVWriters open() {
        initialize();
        return this;
    }

    /**
     * 一時ファイル名を生成します。
     * @return ユーティリティ
     */
    public CSVWriters touch() {
        tempFileName = config.tempDir() + File.separator + Long.toString(System.currentTimeMillis()) + "_" + UUID.randomUUID().toString();
        return this;
    }

    /**
     * 指定されたデータ配列をファイルに出力します。
     * @param line 書込み対象のデータ
     * @return ユーティリティ
     */
    public CSVWriters write(String[] line) {
        Args.checkNotNull(csv, R.getString("E-CSV#0004"));
        Args.checkNotNull(line, R.getString("E-CSV#0016"));
        csv.writeNext(transform(line));
        up();
        return this;
    }

    /**
     * ストリームなどをクローズします。
     * @return ユーティリティ
     */
    public CSVWriters close() {
        try {
            if (csv != null) {
                csv.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (osw != null) {
                osw.close();
            }
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            throw new InternalException(CSVWriters.class, "E-CSV#0003", e);
        }
        return this;
    }

    /**
     * 処理件数を取得します。
     * @return 処理件数
     */
    public int count() {
        return counter.get();
    }

    /**
     * 書込みファイルのデータを取得します。
     * @return {@link DownloadFile}
     */
    public DownloadFile get() {
        return new DownloadFile(tempFileName);
    }

    /**
     * 処理カウントをインクリメントします。
     */
    protected void up() {
        counter.incrementAndGet();
    }

    /**
     * nullを空文字へ変換する処理を実施します。
     * @param line 書込み対象のデータ
     * @return 変換後のデータ
     */
    protected String[] transform(String[] line) {
        String[] transformed;
        if (transformer == null) {
            transformed = Arrays.copyOf(line, line.length);
        } else {
            transformed = new String[line.length];
            for (int i = 0; i < line.length; i++) {
                transformed[i] = transformer.apply(line[i]);
            }
        }
        for (int i = 0; i < transformed.length; i++) {
            if (transformed[i] == null) {
                transformed[i] = "";
            }
        }
        return transformed;
    }

}