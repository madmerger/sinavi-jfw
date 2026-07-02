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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import jp.co.ctc_g.jfw.core.internal.InternalException;
import jp.co.ctc_g.jfw.core.internal.InternalMessages;
import jp.co.ctc_g.jfw.core.util.Args;
import jp.co.ctc_g.jfw.core.util.Maps;
import jp.co.ctc_g.jfw.core.util.Strings;
import jp.co.ctc_g.jse.core.csv.CSVConfigs.CSVConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * <p>
 * このクラスは、CSVファイルの読込ユーティリティです。
 * </p>
 * <p>
 * 単純なJavaBeanとのマッピングをするときは{@link jp.co.ctc_g.jse.core.csv.BeanMappingCSVReaders}を利用してください。
 * </p>
 * @see jp.co.ctc_g.jse.core.csv.BeanMappingCSVReaders
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class CSVReaders {

    private static final Logger L = LoggerFactory.getLogger(CSVReaders.class);

    protected static final ResourceBundle R = InternalMessages.getBundle(CSVReaders.class);

    /**
     * {@link CSVConfig}
     */
    protected CSVConfig config;

    /**
     * 読み込み対象のファイル
     */
    protected File file;

    /**
     * ファイル読み込みストリーム
     */
    protected FileInputStream fis;

    /**
     * ストリームリーダー
     */
    protected InputStreamReader iso;

    /**
     * リーダー
     */
    protected Reader reader;

    /**
     * CSVリーダー
     */
    protected CSVReader csv;

    /**
     * 1行文のデータ
     */
    protected String[] line;

    /**
     * 読み込み対象の行があるかどうか？
     * true:読み込み対象あり
     * false:読み込み対象なし
     */
    protected boolean hasNext;

    /**
     * 読み込み行数を保持するカウンタ
     */
    private AtomicInteger counter = new AtomicInteger();

    /**
     * デフォルトコンストラクタです。
     */
    public CSVReaders() {}

    /**
     * コンストラクタです。
     * @param file ファイル
     * @param config コンフィグ
     */
    protected CSVReaders(File file, CSVConfig config) {
        this.hasNext = true;
        this.config = config;
        this.file = file;
    }

    /**
     * コンストラクタです。
     * @param file ファイル
     */
    protected CSVReaders(File file) {
        this(file, CSVConfigs.config());
    }

    /**
     * ストリームを初期化します。
     */
    protected void initialize() {
        InternalException ie = null;
        try {
            fis = new FileInputStream(file);
            if (L.isDebugEnabled()) L.debug(Strings.substitute(R.getString("D-CSV#0002"), Maps.hash("filePath", file.getAbsolutePath())));
            iso = new InputStreamReader(fis, config.encode());
            reader = new BufferedReader(iso);
            CSVParser parser = new CSVParserBuilder()
                .withSeparator(config.separator())
                .withQuoteChar(config.quote())
                .withEscapeChar(config.escape())
                .withStrictQuotes(config.strictQuotes())
                .withIgnoreLeadingWhiteSpace(config.whitespace())
                .build();
            csv = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .withSkipLines(config.index())
                .build();
        } catch (FileNotFoundException e) {
            if (L.isDebugEnabled()) L.debug(R.getString("D-CSV#0003"), e);
            ie = new InternalException(CSVReaders.class, "E-CSV#0001", Maps.hash("filePath", file.getAbsolutePath()), e);
        } catch (UnsupportedEncodingException e) {
            if (L.isDebugEnabled()) L.debug(R.getString("D-CSV#0003"), e);
            ie = new InternalException(CSVReaders.class, "E-CSV#0002", Maps.hash("encode", config.encode()), e);
        } finally {
            if (ie != null) {
                close();
                throw ie;
            }
        }
    }

    /**
     * 読込時の設定情報を設定します。
     * @param c 設定情報
     * @return このクラス
     */
    public CSVReaders config(CSVConfig c) {
        config = c;
        return this;
    }

    /**
     * ストリームをオープンします。
     * @return ユーティリティ
     */
    public CSVReaders open() {
        initialize();
        return this;
    }

    /**
     * 次の行を読み込みます。
     * @return ユーティリティ
     */
    public CSVReaders next() {
        Args.checkNotNull(csv, R.getString("E-CSV#0005"));
        InternalException ie = null;
        try {
            line = csv.readNext();
            if (line == null) {
                hasNext = false;
                return this;
            }
        } catch (IOException | com.opencsv.exceptions.CsvValidationException e) {
            ie = new InternalException(CSVReaders.class, "E-CSV#0009", e);
        } finally {
            if (ie != null) {
                close();
                throw ie;
            }
        }
        return this;
    }

    /**
     * 次の行があるか？
     * @return ユーティリティ
     */
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * 現在の読み込み行数を返します。
     * @return ユーティリティ
     */
    public int count() {
        return counter.get();
    }

    /**
     * 1行分のデータを取得します。
     * @return 1行分のデータ
     */
    public String[] get() {
        Args.checkNotNull(line, R.getString("E-CSV#0014"));
        check();
        String[] l = line;
        up();
        validate();
        clear();
        return l;
    }

    /**
     * 内部変数を初期化します。
     */
    public void clear() {
        line = null;
    }

    /**
     * ストリームをクローズします。
     */
    public void close() {
        try {
            if (csv != null) {
                csv.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (iso != null) {
                iso.close();
            }
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            throw new InternalException(CSVReaders.class, "E-CSV#0003", e);
        }
    }

    /**
     * バリデーションを実行する場合の拡張ポイントです。
     * デフォルトでは何も提供しません。
     */
    protected void validate() {
        // デフォルトではバリデーションは行いません。
    }

    /**
     * データ配列の長さチェックを実行します。
     */
    protected void check() {
        if (config.check()) {
            if (line.length != config.length()) { 
                throw new InternalException(CSVReaders.class, "E-CSV#0008", Maps.hash("row", count()));
            }
        }
    }

    /**
     * 処理カウンターをアップします。
     */
    protected void up() {
        counter.incrementAndGet();
    }

}